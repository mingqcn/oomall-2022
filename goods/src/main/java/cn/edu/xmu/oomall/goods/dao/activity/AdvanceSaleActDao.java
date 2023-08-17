//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.activity;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.oomall.goods.dao.OnsaleDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.dao.bo.Activity;
import cn.edu.xmu.oomall.goods.dao.bo.AdvanceSaleAct;
import cn.edu.xmu.oomall.goods.dao.bo.Onsale;
import cn.edu.xmu.oomall.goods.mapper.jpa.ActivityOnsalePoMapper;
import cn.edu.xmu.oomall.goods.mapper.jpa.ActivityPoMapper;
import cn.edu.xmu.oomall.goods.mapper.mongo.AdvanceSaleActPoMapper;
import cn.edu.xmu.oomall.goods.mapper.po.ActivityOnsalePo;
import cn.edu.xmu.oomall.goods.mapper.po.ActivityPo;
import cn.edu.xmu.oomall.goods.mapper.po.AdvanceSaleActPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;
import static cn.edu.xmu.javaee.core.util.Common.copyObj;

@Repository
public class AdvanceSaleActDao implements ActivityInf{

    private Logger logger = LoggerFactory.getLogger(AdvanceSaleActDao.class);

    private AdvanceSaleActPoMapper actPoMapper;
    private OnsaleDao onsaleDao;
    private ActivityDao activityDao;
    private ProductDao productDao;
    private ActivityPoMapper activityPoMapper;
    private ActivityOnsalePoMapper activityOnsalePoMapper;

    @Autowired
    public AdvanceSaleActDao(AdvanceSaleActPoMapper actPoMapper, OnsaleDao onsaleDao, ActivityDao activityDao, ProductDao productDao, ActivityPoMapper activityPoMapper, ActivityOnsalePoMapper activityOnsalePoMapper) {
        this.actPoMapper = actPoMapper;
        this.onsaleDao = onsaleDao;
        this.activityDao = activityDao;
        this.productDao = productDao;
        this.activityPoMapper = activityPoMapper;
        this.activityOnsalePoMapper = activityOnsalePoMapper;
    }


    @Override
    public Activity getActivity(ActivityPo po)  throws RuntimeException{
        Activity bo = cloneObj(po, AdvanceSaleAct.class);
        Optional<AdvanceSaleActPo> ret = this.actPoMapper.findById(po.getObjectId());
        ret.ifPresent(actPo -> {
            copyObj(actPo, bo);
        } );
        return bo;
    }

    @Override
    public String insert(Activity bo) throws RuntimeException{
        AdvanceSaleActPo po = cloneObj(bo, AdvanceSaleActPo.class);
        AdvanceSaleActPo newPo = this.actPoMapper.insert(po);
        return newPo.getObjectId();
    }

    @Override
    public void save(Activity bo) throws RuntimeException{
        AdvanceSaleActPo po = cloneObj(bo, AdvanceSaleActPo.class);
        this.actPoMapper.save(po);
    }


    /**
     * 根据id查询对象
     * @author: 兰文强
     * @date: 2022/12/20 20:28
     */
    public AdvanceSaleAct findById(Long id){
        Activity act = activityDao.findById(id);
        //判断是否为预售活动
        if(!AdvanceSaleAct.ACTCLASS.equals(act.getActClass())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST,String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"预售活动",id));
        }
        return (AdvanceSaleAct) act;
    }


    /**
     * 根据id查询上线的预售活动
     * @author: 兰文强
     * @date: 2022/12/20 20:45
     */
    public AdvanceSaleAct findValidAdvanceSaleActById(Long id){
        AdvanceSaleAct act = this.findById(id);
        //判断是否上线
        if(!valid(act)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "预售活动", act.getId()));
        }
        return act;
    }


    /**
     * 判断预售活动是否上线
     * @author: 兰文强
     * @date: 2022/12/20 21:29
     */
    private boolean valid(Activity act) throws BusinessException{
        List<ActivityOnsalePo> activityOnsalePoList = activityOnsalePoMapper.findByActIdEquals(act.getId());
        if(activityOnsalePoList==null) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "预售活动", act.getId()));
        }
        return activityOnsalePoList.stream().anyMatch(o -> onsaleDao.findById(o.getId()).getInvalid().equals((byte) 1));
    }

    /**
     * 管理员取消预售活动
     */
    public void delActivityById(Long id, Long shopId) {
        Activity act = this.findById(id);
        //判断是否为该商铺的活动
        if(!shopId.equals(act.getShopId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "预售活动", id, shopId));
        }
        List<ActivityOnsalePo> activityOnsalePoList = activityOnsalePoMapper.findByActIdEquals(id);
        if (null == activityOnsalePoList || activityOnsalePoList.size() == 0) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        activityOnsalePoMapper.deleteByActId(id);
    }


    /**
     * 根据shopId和productId查询所有有效的预售活动
     * @author 兰文强 25120202201946
     * @date 2022/12/14 23:16
     */
    public PageDto<Activity> retrieveValidByShopIdAndProductId(Long shopId, Long productId, Integer page, Integer pageSize) {
        List<Onsale> onsales;
        List<Activity> ret = new ArrayList<>();
        if (null != productId) {
            onsales = onsaleDao.retrieveByProductId(productId, 0, MAX_RETURN);
            for (Onsale bo : onsales) {
                ret.addAll(bo.getActList());
            }
            if (null != shopId) {
                ret = ret.stream().filter(bo -> bo.getShopId().equals(shopId)).collect(Collectors.toList());
            }
        } else {
            if (null != shopId) {
                ret = retrieveByShopId(shopId, 0, MAX_RETURN).getList();
            } else {
                ret = retrieveValidAdvAct(0,MAX_RETURN).getList();
            }
        }
        ret = ret.stream().filter(bo-> AdvanceSaleAct.ACTCLASS.equals(bo.getActClass()))
                .filter(this::valid)
                .skip((long) (page-1) * pageSize).limit(pageSize)
                .map(bo -> cloneObj(bo,AdvanceSaleAct.class)).collect(Collectors.toList());
        return new PageDto<>(ret, page, pageSize);
    }


    /**
     * 返回所有有效的预售活动
     * @author 兰文强 25120202201946
     * @date 2022/12/15 23:28
     */
    public PageDto<Activity> retrieveValidAdvAct(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ActivityPo> activities = activityPoMapper.findAllByActClassEquals(
                AdvanceSaleAct.ACTCLASS,pageable);
        List<Activity> ret = new ArrayList<>();
        for(ActivityPo po:activities){
            ret.add(activityDao.findById(po.getId()));
        }
        ret = ret.stream().filter(this::valid)
                .skip((long) (page) * pageSize).limit(pageSize)
                .collect(Collectors.toList());
        return new PageDto<>(ret, page, pageSize);
    }


    /**
     * 根据shopId查询预售活动
     * author 兰文强 25120202201946
     * date 2022/12/10 11:34
     */
    public PageDto<Activity> retrieveByShopId(Long shopId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ActivityPo> pos = activityPoMapper.findAllByActClassEqualsAndShopId(AdvanceSaleAct.ACTCLASS, shopId, pageable);
        List<Activity> ret = pos.stream().map(
                this::getActivity
        ).collect(Collectors.toList());
        return new PageDto<>(ret, page, pageSize);
    }


    /**
     * 管理员查询特定商铺的所有预售活动
     * @author: 兰文强
     * @date: 2022/12/19 21:01
     */
    public PageDto<Activity> retrieveByShopIdAndProductIdAndOnsaleId(Long shopId, Long productId, Long onsaleId,Integer page,Integer pageSize){
        List<Activity> actList = this.retrieveByShopId(shopId,0, MAX_RETURN).getList();
        try {
            if(productId!=null) {
                actList.retainAll(productDao.findProductById(productId).getActList());
            }
            if(onsaleId!=null){
                actList.retainAll(onsaleDao.findById(onsaleId).getActList());
            }
        }catch (NullPointerException e){
            return new PageDto<>(new ArrayList<>(),page,pageSize);
        }
        actList = actList.stream().filter(activity -> AdvanceSaleAct.ACTCLASS.equals(activity.getActClass()))
                .filter(shareAct -> shopId.equals(shareAct.getShopId()))
                .skip((long) (page - 1) * pageSize).limit(pageSize)
                .collect(Collectors.toList());
        return new PageDto<>(actList,page,pageSize);
    }

}
