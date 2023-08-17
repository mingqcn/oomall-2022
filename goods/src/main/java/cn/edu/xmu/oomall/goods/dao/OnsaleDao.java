//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.goods.dao;


import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.dao.activity.ActivityDao;
import cn.edu.xmu.oomall.goods.dao.bo.Onsale;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.mapper.jpa.ActivityOnsalePoMapper;
import cn.edu.xmu.oomall.goods.mapper.jpa.OnsalePoMapper;
import cn.edu.xmu.oomall.goods.mapper.po.ActivityOnsalePo;
import cn.edu.xmu.oomall.goods.mapper.po.OnsalePo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
@RefreshScope
public class OnsaleDao {

    private static final String KEY = "O%d";
    private static final String VALID_KEY = "OV%d";

    private final static Logger logger = LoggerFactory.getLogger(OnsaleDao.class);

    public  final static Onsale NOTEXIST = new Onsale(){{
        setId(-1L);
    }};

    @Value("${oomall.onsale.timeout}")
    private int timeout;

    private OnsalePoMapper onsalePoMapper;

    private ActivityDao activityDao;

    private ShopDao shopDao;

    private ProductDao productDao;

    private RedisUtil redisUtil;

    private ActivityOnsalePoMapper activityOnsalePoMapper;

    @Autowired
    @Lazy
    public OnsaleDao(OnsalePoMapper onsalePoMapper, ActivityDao activityDao, ShopDao shopDao, ProductDao productDao, RedisUtil redisUtil,ActivityOnsalePoMapper activityOnsalePoMapper) {
        this.onsalePoMapper = onsalePoMapper;
        this.activityDao = activityDao;
        this.shopDao = shopDao;
        this.productDao = productDao;
        this.redisUtil = redisUtil;
        this.activityOnsalePoMapper = activityOnsalePoMapper;
    }

    private Onsale getBo(OnsalePo po, Optional<String> redisKey){
        Onsale bo = Onsale.builder().id(po.getId()).creatorId(po.getCreatorId()).creatorName(po.getCreatorName()).gmtCreate(po.getGmtCreate()).gmtModified(po.getGmtModified()).modifierId(po.getModifierId()).modifierName(po.getModifierName())
        .quantity(po.getQuantity()).maxQuantity(po.getMaxQuantity()).price(po.getPrice()).endTime(po.getEndTime()).beginTime(po.getBeginTime()).invalid(po.getInvalid()).type(po.getType()).productId(po.getProductId()).shopId(po.getShopId()).build();
        Long newTimeout = this.getNewTimeout(po.getEndTime());
        this.setBo(bo);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, newTimeout));
        return bo;
    }

    /**
     * 计算过期时间，应该不超过onsale的endtime
     * @author Ming Qiu
     * <p>
     * date: 2022-12-04 19:20
     * @param endTime
     * @return
     */
    private Long getNewTimeout(LocalDateTime endTime) {
        Long diff = Duration.between(LocalDateTime.now(), endTime).toSeconds();
        Long newTimeout = Math.min(this.timeout,  diff);
        return newTimeout;
    }

    private void setBo(Onsale bo){
        bo.setActivityDao(this.activityDao);
        bo.setShopDao(this.shopDao);
        bo.setProductDao(this.productDao);
    }
    /**
     * 获得货品的最近的价格和库存
     *
     * @param productId 货品对象
     * @return 规格对象
     */
    public Onsale findLatestValidOnsaleByProductId(Long productId) throws RuntimeException{
        logger.debug("findLatestValidOnsale: id ={}",productId);
        String key = String.format(VALID_KEY, productId);
        if (redisUtil.hasKey(key)){
            Integer onsaleId = (Integer) redisUtil.get(key);
            if (!onsaleId.equals(NOTEXIST.getId().intValue())) {
                try {
                    Onsale bo = this.findById(Long.valueOf(onsaleId));
                    setBo(bo);
                    return bo;
                } catch (BusinessException e) {
                    if (ReturnNo.RESOURCE_ID_NOTEXIST != e.getErrno()) {
                        throw e;
                    }
                }
            }
            return NOTEXIST;
        }

        Pageable pageable = PageRequest.of(0, MAX_RETURN, Sort.by("beginTime").ascending());
        LocalDateTime now = LocalDateTime.now();
        Page<OnsalePo> retObj = this.onsalePoMapper.findByProductIdEqualsAndEndTimeAfter(productId, now, pageable);
        if (retObj.isEmpty() ){
            redisUtil.set(key, NOTEXIST.getId(), timeout);
            return NOTEXIST;
        }else{
            OnsalePo po = retObj.stream().limit(1).collect(Collectors.toList()).get(0);
            Onsale bo =  this.getBo(po, Optional.ofNullable(null));
            redisUtil.set(key, bo.getId(), getNewTimeout(bo.getEndTime()) );
            return bo;
        }
    }

    /**
     * 用id找对象
     * @author Ming Qiu
     * <p>
     * date: 2022-12-04 8:34
     * @param id
     * @return
     * @throws RuntimeException
     */
    public Onsale findById(Long id) throws RuntimeException {
        logger.debug("findById: id ={}", id);
        if (null == id) {
            return null;
        }

        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            Onsale bo = (Onsale) redisUtil.get(key);
            setBo(bo);
            return bo;
        }
        Optional<OnsalePo> retObj = this.onsalePoMapper.findById(id);
        if (retObj.isEmpty() ){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "销售", id));
        }else{
            OnsalePo po = retObj.get();
            return this.getBo(po, Optional.of(key));
        }

    }
   /*
     * 保存Onsale到数据库中去
     * @param onsale Onsale对象
     * @param user 操作管理员对象
     */
    public Onsale insert(Onsale onsale, UserDto user) {
        logger.debug("insertOnsale onsale={}",onsale);
        OnsalePo onsalePo = cloneObj(onsale, OnsalePo.class);

        if(null!=user){
            putGmtFields(onsalePo,"create");
            putUserFields(onsalePo,"creator",user);
        }
        onsalePo.setId(null);
        OnsalePo ret = this.onsalePoMapper.save(onsalePo);
        return cloneObj(ret,Onsale.class);
    }

    /**
     * 修改Onsale,保存到数据库
     * @param onsale
     * @param user
     * @return redisKey
     */
    public String save(Onsale onsale, UserDto user){
        logger.debug("saveOnsale onsale={}",onsale);
        OnsalePo onsalePo = cloneObj(onsale, OnsalePo.class);
        if(null!=user){
            putGmtFields(onsalePo,"create");
            putUserFields(onsalePo,"creator",user);
        }
        assert(null != onsalePo.getId());

        OnsalePo newPo = this.onsalePoMapper.save(onsalePo);
        if (NOTEXIST.equals(newPo.getId())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "销售", onsalePo.getId()));
        }
        return String.format(KEY,onsale.getId());
    }

    /**
     * 根据productId 获得Onsale对象集合
     * @param productId
     * @param page
     * @param pageSize
     * @return
     */
    public List<Onsale> retrieveByProductId( Long productId, Integer page, Integer pageSize) {
        Pageable pageable=PageRequest.of(page,pageSize);
        Page<OnsalePo> retObj = this.onsalePoMapper.findByProductIdIs(productId, pageable);
        if(null==retObj){
            return new ArrayList<>();
        }
        List<Onsale> onsales = retObj.stream().map(po->{
            return getBo(po,Optional.ofNullable(null));
        }).collect(Collectors.toList());
        logger.debug("findOnsale : productId={}",productId);
        return onsales;
    }



    /**
     * 寻找时间重叠的Onsale对象
     * @author Ming Qiu
     * <p>
     * date: 2022-12-10 19:04
     * @param productId
     * @param beginTime
     * @param endTime
     * @return
     */
    public Onsale findOverlapOnsale(Long productId, LocalDateTime beginTime, LocalDateTime endTime){
        if (null == productId){
            return null;
        }

        PageRequest pageable = PageRequest.of(0,1, Sort.by(Sort.Direction.ASC, "beginTime"));
        Page<OnsalePo> ret =  this.onsalePoMapper.findOverlap(productId, beginTime, endTime, pageable);
        if (!ret.isEmpty()){
            OnsalePo po = ret.getContent().get(0);
            return this.getBo(po, Optional.ofNullable(null));
        }else{
            return NOTEXIST;
        }
    }

    //根据shopId、onsaleId和productId查找销售
    public List<Onsale> retrieveByShopIdAndOnsaleIdAndProductId(Long shopId, Long onsaleId, Long productId){
        Pageable pageable = PageRequest.of(0, MAX_RETURN);
        //通过主键onsaleId(如果非空)查找的bo对象
        Onsale bo = null;
        Page<OnsalePo> onsalePos;

        if(null != onsaleId && null != productId) {
            bo = this.findById(onsaleId);
            onsalePos = onsalePoMapper.findByShopIdAndProductId(shopId, productId, pageable);
        }
        else if(null != onsaleId){
            bo = this.findById(onsaleId);
            onsalePos = onsalePoMapper.findByShopId(shopId, pageable);
        }
        else if(null != productId)
            onsalePos = onsalePoMapper.findByShopIdAndProductId(shopId, productId, pageable);
        else
            onsalePos = onsalePoMapper.findByShopId(shopId, pageable);

        List<Onsale> onsaleList = onsalePos.getContent().stream().map(po -> {
            Onsale onsale = cloneObj(po, Onsale.class);
            setBo(onsale);
            return onsale;
        }).collect(Collectors.toList());

        //如果通过主键查找的销售对象存在，合并完之后由于可能存在重复故根据id做去重处理
        if(null != bo){
            onsaleList.add(bo);
            onsaleList = onsaleList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Onsale::getId))), ArrayList::new));
        }
        return onsaleList;
    }

    //根据shopId、productId查找有效的销售
    public List<Onsale> retrieveByShopIdAndProductIdAndInvalidEquals(Long shopId, Long productId){
        Pageable pageable = PageRequest.of(0, MAX_RETURN);
        Page<OnsalePo> onsalePos;

        if(null != shopId && null != productId)
            onsalePos = onsalePoMapper.findByShopIdAndProductIdAndInvalidEquals(shopId, productId, Onsale.NORMAL, pageable);
        else if(null != shopId)
            onsalePos = onsalePoMapper.findByShopIdAndInvalidEquals(shopId, Onsale.NORMAL, pageable);
        else if(null != productId)
            onsalePos = onsalePoMapper.findByProductIdAndInvalidEquals(productId, Onsale.NORMAL, pageable);
        else
            onsalePos = onsalePoMapper.findByInvalidEquals(Onsale.NORMAL, pageable);

        return onsalePos.getContent().stream().map(po -> {
            Onsale onsale = cloneObj(po, Onsale.class);
            setBo(onsale);
            return onsale;
        }).collect(Collectors.toList());
    }

    /**
     * @param actId 活动id
     * @author Liang nan
     */
    public List<Onsale> retrieveByActId(Long actId, Integer page, Integer pageSize) throws RuntimeException {
        if (null == actId) {
            return null;
        }
        Pageable pageable=PageRequest.of(page,pageSize);
        Page<ActivityOnsalePo> actOnsalePos = activityOnsalePoMapper.findByActIdEquals(actId, pageable);
        List<Onsale> onsaleList = actOnsalePos.getContent().stream().map(po -> po.getOnsaleId()).map(onsaleId -> {
            Onsale ret = null;
            try {
                ret = this.findById(onsaleId);
            } catch (BusinessException e) {
                if (ReturnNo.RESOURCE_ID_NOTEXIST != e.getErrno()) {
                    throw e;
                }
            }
            return ret;
        }).filter(obj -> null != obj).collect(Collectors.toList());
        if(onsaleList==null){
            return new ArrayList<>();
        }
        return onsaleList;
    }

}
