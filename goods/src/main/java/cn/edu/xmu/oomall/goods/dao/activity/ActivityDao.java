//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.activity;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.dao.bo.*;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.mapper.jpa.ActivityOnsalePoMapper;
import cn.edu.xmu.oomall.goods.mapper.jpa.ActivityPoMapper;
import cn.edu.xmu.oomall.goods.mapper.po.ActivityOnsalePo;
import cn.edu.xmu.oomall.goods.mapper.po.ActivityPo;
import cn.edu.xmu.oomall.goods.service.dto.ActivityOnsaleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class ActivityDao {
    private Logger logger = LoggerFactory.getLogger(ActivityDao.class);

    private final static String KEY = "A%d";

    private final static String ONSALE_ACT = "AO%d";

    @Value("${oomall.activity.timeout}")
    private int timeout;

    private RedisUtil redisUtil;

    private ApplicationContext  context;

    private ActivityOnsalePoMapper activityOnsalePoMapper;

    private ActivityPoMapper activityPoMapper;

    private ShopDao shopDao;
    @Autowired
    public ActivityDao(RedisUtil redisUtil, ApplicationContext context, ActivityOnsalePoMapper activityOnsalePoMapper, ActivityPoMapper activityPoMapper,ShopDao shopDao) {
        this.redisUtil = redisUtil;
        this.context = context;
        this.activityOnsalePoMapper = activityOnsalePoMapper;
        this.activityPoMapper = activityPoMapper;
        this.shopDao=shopDao;
    }

    /**
     * 返回Bean对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-22 16:11
     * @param po
     * @return
     */
    private ActivityInf findActivityDao(ActivityPo po){
        return (ActivityInf) context.getBean(po.getActClass());
    }

    /**
     * 获得销售对象的活动
     * @author Ming Qiu
     * <p>
     * date: 2022-11-27 16:54
     * @param onsaleId
     * @return
     */
    public List<Activity> retrieveByOnsaleId(Long onsaleId){
        if (null == onsaleId){
            return null;
        }

        String key = String.format(ONSALE_ACT, onsaleId);
        if (redisUtil.hasKey(key)) {
            List<Long> actIds = (ArrayList<Long>) redisUtil.get(key);
            return actIds.stream().map(id -> {
                Activity ret = null;
                try {
                    ret = this.findById(id);
                } catch (BusinessException e) {
                    if (ReturnNo.RESOURCE_ID_NOTEXIST != e.getErrno()) {
                        throw e;
                    }
                }
                return ret;
            }).filter(obj -> null != obj).collect(Collectors.toList());
        }

        Pageable pageable = PageRequest.of(0, MAX_RETURN);
        Page<ActivityOnsalePo> actOnsalePos =  this.activityOnsalePoMapper.findByOnsaleIdEquals(onsaleId, pageable);

        return actOnsalePos.stream().map(po ->po.getActId()).map(actId ->{
            Activity ret1 = null;
            try{
                ret1 = this.findById(actId);
            }catch (BusinessException e){
                if (ReturnNo.RESOURCE_ID_NOTEXIST != e.getErrno()){
                    throw e;
                }
            }
            return ret1;
        }).filter(obj -> null != obj).collect(Collectors.toList());
    }

    /**
     * 根据id获得对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-27 18:29
     * @param id
     * @return
     */
    public Activity findById(Long id) throws RuntimeException{
        if (null == id){
            return null;
        }

        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)){
            return (Activity) redisUtil.get(key);
        }

        Optional<ActivityPo> ret = this.activityPoMapper.findById(id);
        if (ret.isPresent()){
            ActivityInf inf = this.findActivityDao(ret.get());
            Activity bo = inf.getActivity(ret.get());
            redisUtil.set(key, bo, timeout);
            return bo;
        }else{
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "活动", id));
        }
    }

    public List<String> save(Activity bo, UserDto user) throws RuntimeException{
        List<String> delKeys = new ArrayList<>();
        String key = String.format(KEY, bo.getId());
        ActivityPo po = cloneObj(bo, ActivityPo.class);
        ActivityInf inf = this.findActivityDao(po);
        if (this.activityPoMapper.existsById(bo.getId())){
            inf.save(bo);
            putUserFields(po, "modifier", user);
            putGmtFields(po, "modified");
            delKeys.add(key);
            this.activityPoMapper.save(po);
        }else{
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"活动", bo.getId()));
        }
        return delKeys;
    }

    public Activity insert(Activity bo, UserDto user) throws RuntimeException{
        ActivityPo po = cloneObj(bo, ActivityPo.class);
        ActivityInf inf = this.findActivityDao(po);
        String objectId = inf.insert(bo);
        po.setObjectId(objectId);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        ActivityPo newPo = activityPoMapper.save(po);
        bo.setId(newPo.getId());
        return bo;
    }

    //根据actId在activityonsale中间表中查找对象
   public List<ActivityOnsaleDto> retrieveActivityOnsaleByActId(Long actId){
        List<ActivityOnsalePo> ret = activityOnsalePoMapper.findByActIdEquals(actId);
        return ret.stream().map(po -> cloneObj(po, ActivityOnsaleDto.class)).collect(Collectors.toList());
    }

    public ReturnNo insertActivityOnsale( Long id,Long onsaleId, UserDto user) throws RuntimeException
    {
        ActivityOnsalePo activityOnsalePo=new ActivityOnsalePo();
        if(null!=user){
            putGmtFields(activityOnsalePo,"create");
            putUserFields(activityOnsalePo,"creator",user);
        }
        activityOnsalePo.setActId(id);
        activityOnsalePo.setOnsaleId(onsaleId);
        activityOnsalePoMapper.save(activityOnsalePo);
        return ReturnNo.OK;
    }
    /**
     * 新增ActivityOnsale关系
     * @author Liang nan
     */
    public void addActivityOnsale(Long id, Onsale onsale, UserDto creator) {
        Activity activity =this.findById(id);
        //预售与优惠和团购活动不能并存，出215错误
        if (onsale.getType() == onsale.GROUPON && (activity instanceof CouponAct || activity instanceof AdvanceSaleAct)) {
            throw new BusinessException(ReturnNo.ADVSALE_NOTCOEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "销售活动", onsale.getId()));
        }
        //团购与优惠和预售活动不并存 出216错误
        if (onsale.getType() == onsale.ADVSALE && (activity instanceof CouponAct || activity instanceof GrouponAct)) {
            throw new BusinessException(ReturnNo.GROUPON_NOTCOEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "销售活动", onsale.getId()));
        }
        ActivityOnsalePo po = new ActivityOnsalePo();
        po.setOnsaleId(onsale.getId());
        po.setActId(id);
        putUserFields(po, "creator", creator);
        putGmtFields(po, "create");
        activityOnsalePoMapper.save(po);
    }

    /**
     * 删除活动与所有onsale的关系
     * * @author Liang nan
     */
    public void delActivityOnsaleByActId(Long id) {
        Pageable pageable=PageRequest.of(0,MAX_RETURN);
        Page<ActivityOnsalePo> actOnsalePos = activityOnsalePoMapper.findByActIdEquals(id, pageable);
        if (null == actOnsalePos || actOnsalePos.toList().size() == 0) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        activityOnsalePoMapper.deleteByActId(id);
    }
}
