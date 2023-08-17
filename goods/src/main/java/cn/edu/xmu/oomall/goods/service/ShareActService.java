package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.controller.vo.ShareActVo;
import cn.edu.xmu.oomall.goods.dao.OnsaleDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.dao.activity.ActivityDao;
import cn.edu.xmu.oomall.goods.dao.activity.ShareActDao;
import cn.edu.xmu.oomall.goods.dao.bo.*;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.mapper.jpa.ActivityOnsalePoMapper;
import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import cn.edu.xmu.oomall.goods.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

/**
 * @author 黄坤鹏
 * @date 2022/11/30 10:15
 */
@Service
public class ShareActService {

    private Logger logger = LoggerFactory.getLogger(ShareActService.class);

    private ShareActDao shareActDao;

    private OnsaleDao onsaleDao;

    private ActivityDao activityDao;

    private ProductDao productDao;

    private ShopDao shopDao;

    private RedisUtil redisUtil;

    private static final String SHAREACT = "shareActDao";
    private final ActivityOnsalePoMapper activityOnsalePoMapper;

    @Autowired
    public ShareActService(ShareActDao shareActDao,
                           OnsaleDao onsaleDao,
                           ActivityDao activityDao,
                           ProductDao productDao,
                           ShopDao shopDao,
                           RedisUtil redisUtil,
                           ActivityOnsalePoMapper activityOnsalePoMapper){
        this.redisUtil = redisUtil;
        this.onsaleDao = onsaleDao;
        this.activityDao = activityDao;
        this.productDao = productDao;
        this.shopDao = shopDao;
        this.shareActDao = shareActDao;
        this.activityOnsalePoMapper = activityOnsalePoMapper;
    }

    /**
     * 查询特定商铺的分享活动
     * @param shopId 商铺Id
     * @param productId 商品Id
     * @param onsaleId 销售Id
     * @param page 页码
     * @param pageSize 页大小
     * @return
     */
    @Transactional
    public PageDto<SimpleShareActDto> retrieveByShopIdAndOnsaleIdAndProductId(Long shopId, Long productId, Long onsaleId, Integer page, Integer pageSize){
        //通过shopId、销售id、productId查询销售对象
        List<Onsale> onsales = onsaleDao.retrieveByShopIdAndOnsaleIdAndProductId(shopId, onsaleId, productId);
        List<Activity> actList = new ArrayList<>();
        //获取并合并其关联的一系列活动
        onsales.forEach(onsale -> {
            actList.addAll(onsale.getActList());
        });
        List<SimpleShareActDto> shareActList = actList.stream()
                .filter(activity -> SHAREACT.equals(activity.getActClass()))
                .skip((long) (page-1) * pageSize).limit(pageSize)
                .map(shareAct -> {
                    SimpleShareActDto simpleShareActDto = new SimpleShareActDto();
                    copyObj(shareAct, simpleShareActDto);
                    return simpleShareActDto;
                })
                .collect(Collectors.toList());

        return new PageDto<>(shareActList, page , pageSize);
    }

    /**
     * 管理员新增分享活动
     * @param shopId 商铺号
     * @param shareActVo 活动信息
     * @param userDto 登录用户
     */
    @Transactional
    public SimpleShareActDto createShareAct(Long shopId, ShareActVo shareActVo, UserDto userDto){
        List<ThresholdPo> thresholdPos = shareActVo.getThresholds().stream().map(stragety -> new ThresholdPo(stragety.getQuantity(), stragety.getPercentage())).collect(Collectors.toList());
        ShareAct shareAct = new ShareAct();
        shareAct.setThresholds(thresholdPos);
        shareAct.setName(shareActVo.getName());
        shareAct.setShopId(shopId);
        shareAct.setActClass(SHAREACT);
        Activity act = activityDao.insert(shareAct, userDto);
        return cloneObj(act, SimpleShareActDto.class);
    }

    /**
     * 查询分享活动
     * @param shopId 商铺id
     * @param productId 商品id
     * @param page 页码
     * @param pageSize 页大小
     */
    @Transactional
    public PageDto<SimpleShareActDto> retrieveByShopIdAndProductIdAndInvalidEquals(Long shopId, Long productId, Integer page, Integer pageSize){
        //通过shopId、销售productId查询有效的销售对象
        List<Onsale> onsales = onsaleDao.retrieveByShopIdAndProductIdAndInvalidEquals(shopId, productId);
        List<Activity> actList = new ArrayList<>();
        //获取并合并其关联的一系列活动
        onsales.forEach(onsale -> {
            actList.addAll(onsale.getActList());
        });
        //查找其中是分享活动且其shopId相等的活动
        List<SimpleShareActDto> shareActList = actList.stream()
                .filter(activity -> SHAREACT.equals(activity.getActClass()))
                .skip((long) (page - 1) * pageSize).limit(pageSize)
                .map(shareAct -> {
                    SimpleShareActDto simpleShareActDto = new SimpleShareActDto();
                    copyObj(shareAct, simpleShareActDto);
                    return simpleShareActDto;
                })
                .collect(Collectors.toList());

        return new PageDto<>(shareActList, page, pageSize);
    }

    /**
     * 查看分享活动详情
     * @param id 分享活动id
     * @return
     */
    @Transactional
    public ShareActDto findById(Long id){
        Activity activity = activityDao.findById(id);
        //判断是否为分享活动
        if(!SHAREACT.equals(activity.getActClass())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "分享活动", id));
        }
        //通过中间表找到分享活动对应的多个onSale销售
        List<ActivityOnsaleDto> activityOnsales = shareActDao.retrieveActivityOnsaleByActId(id);
        List<SimpleOnsaleDto> onSaleDtoList = activityOnsales.stream().map(ActivityOnsaleDto::getOnsaleId)
                .map(onsaleId -> {
                    Onsale onsale = onsaleDao.findById(onsaleId);
                    SimpleOnsaleDto onSaleDto = cloneObj(onsale, SimpleOnsaleDto.class);
                    Product product = onsale.getProduct();
                    IdNameDto productDto = new IdNameDto(product.getId(), product.getName());
                    onSaleDto.setProduct(productDto);
                    return onSaleDto;
                }).collect(Collectors.toList());
        //组装dto数据
        ShareActDto shareActDto = new ShareActDto();
        copyObj(activity, shareActDto);
        InternalReturnObject<Shop> ret = shopDao.getShopById(activity.getShopId());
        if(ReturnNo.OK != ReturnNo.getByCode(ret.getErrno())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺", activity.getShopId()));
        }
        Shop shop = ret.getData();
        shareActDto.setShop(new IdNameTypeDto(shop.getId(), shop.getName(), shop.getType()));
        shareActDto.setOnsaleList(onSaleDtoList);
        return shareActDto;
    }

    /**
     * 管理员在已有销售上增加活动
     * @param onsaleId 销售id
     * @param actId 活动id
     * @param userDto 登录用户
     * @return
     */
    @Transactional
    public SimpleOnsaleDto createActivityOnsale(Long onsaleId, Long actId, UserDto userDto){
        Activity activity = activityDao.findById(actId);
        //判断是否为分享活动
        if(!SHAREACT.equals(activity.getActClass())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "分享活动",activity.getId()));
        }
        Onsale onsale = onsaleDao.findById(onsaleId);
        ActivityOnsaleDto activityOnsale = new ActivityOnsaleDto();
        activityOnsale.setOnsaleId(onsaleId);
        activityOnsale.setActId(actId);
        shareActDao.insertActivityOnsale(activityOnsale, userDto);
        //组装dto数据
        Product product = productDao.findProductById(onsale.getProductId());
        SimpleOnsaleDto onSaleDto = new SimpleOnsaleDto();
        copyObj(onsale, onSaleDto);
        onSaleDto.setProduct(new IdNameDto(product.getId(), product.getName()));
        return onSaleDto;
    }

    /**
     * 管理员取消已有销售上的活动
     * @param onsaleId 销售id
     * @param actId 活动id
     */
    @Transactional
    public void cancelActivityOnsale(Long shopId, Long onsaleId, Long actId){
        //是否存在该活动
        Activity activity = activityDao.findById(actId);
        //判断该活动是否是该商铺的
        if(shopId != activity.getShopId()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "活动", activity.getId(), shopId));
        }
        //判断是否为分享活动
        if(!SHAREACT.equals(activity.getActClass())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "分享活动", activity.getId()));
        }
        //是否存在该销售
        Onsale onsale = onsaleDao.findById(onsaleId);
        shareActDao.delActivityOnsaleByActIdAndOnsaleId(actId, onsaleId);
    }

    /**
     * 查看特定分享活动详情
     * @param shopId 商铺id
     * @param id 活动id
     */
    @Transactional
    public FullShareActDto findByShopIdAndActId(Long shopId, Long id){
        Activity activity = activityDao.findById(id);
        //判断是否为分享活动
        if(!SHAREACT.equals(activity.getActClass())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "分享活动", id));
        }
        //商铺信息是否一致
        if(shopId != activity.getShopId()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "活动", activity.getId(), shopId));
        }
        //通过中间表找到分享活动对应的多个onSale销售
        List<ActivityOnsaleDto> activityOnsales = shareActDao.retrieveActivityOnsaleByActId(id);
        List<SimpleOnsaleDto> onSaleDtoList = activityOnsales.stream().map(ActivityOnsaleDto::getOnsaleId)
                .map(onsaleId -> {
                    Onsale onsale = onsaleDao.findById(onsaleId);
                    SimpleOnsaleDto onSaleDto = cloneObj(onsale, SimpleOnsaleDto.class);
                    Product product = onsale.getProduct();
                    IdNameDto productDto = new IdNameDto(product.getId(), product.getName());
                    onSaleDto.setProduct(productDto);
                    return onSaleDto;
                }).collect(Collectors.toList());
        //组装dto数据
        FullShareActDto shareActDto = new FullShareActDto();
        copyObj(activity, shareActDto);
        shareActDto.setCreator(new IdNameDto(activity.getCreatorId(), activity.getCreatorName()));
        shareActDto.setModifier(new IdNameDto(activity.getModifierId(), activity.getModifierName()));
        InternalReturnObject<Shop> ret = shopDao.getShopById(activity.getShopId());
        if(ReturnNo.OK != ReturnNo.getByCode(ret.getErrno())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺", activity.getShopId()));
        }
        Shop shop = ret.getData();
        shareActDto.setShop(new IdNameTypeDto(shop.getId(), shop.getName(), shop.getType()));
        shareActDto.setOnsaleList(onSaleDtoList);
        return shareActDto;
    }

    /**
     * 修改平台分享活动的内容
     * @param id 活动id
     * @param shareActVo 修改信息
     * @param userDto 登录用户
     */
    @Transactional
    public void updateByActId(Long shopId, Long id, ShareActVo shareActVo, UserDto userDto){
        Activity activity = activityDao.findById(id);
        //判断该活动是否是该商铺的
        if(shopId != activity.getShopId()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "活动", activity.getId(), shopId));
        }
        //判断是否为分享活动
        if(!SHAREACT.equals(activity.getActClass())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "分享活动", id));
        }
        List<ThresholdPo> thresholdPos = shareActVo.getThresholds().stream().map(stragety -> new ThresholdPo(stragety.getQuantity(), stragety.getPercentage())).collect(Collectors.toList());
        ShareAct shareAct = new ShareAct();
        shareAct.setId(activity.getId());
        shareAct.setName(shareActVo.getName());
        shareAct.setThresholds(thresholdPos);
        shareAct.setActClass(SHAREACT);
        activityDao.save(shareAct, userDto);
    }

    @Transactional
    public Long caluculateShareActRebate(Integer quantity, Long shareActId){
        if(quantity == 0 || null == quantity)
            return 0L;
        ShareAct activity = (ShareAct) activityDao.findById(shareActId);
        List<ThresholdPo> thresholds = activity.getThresholds();
        thresholds.add(new ThresholdPo(0, 0L));
        //按照门槛的quantity从小到大排序
        Collections.sort(thresholds, new Comparator<ThresholdPo>() {
            @Override
            public int compare(ThresholdPo o1, ThresholdPo o2) {
                return o1.getQuantity() - o2.getQuantity();
            }
        });
        Long rebate = 0L;
        for(int i=1; i < thresholds.size(); i++){
            ThresholdPo threshold = thresholds.get(i);
            if(quantity <= threshold.getQuantity() || quantity > threshold.getQuantity() && i == thresholds.size() - 1){
                rebate += (quantity - thresholds.get(i-1).getQuantity()) * threshold.getPercentage();
                break;
            } else {
                rebate += (threshold.getQuantity() - thresholds.get(i-1).getQuantity()) * threshold.getPercentage();
            }
        }
        return rebate;
    }

}
