package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.controller.vo.OrderInfoVo;
import cn.edu.xmu.oomall.goods.dao.OnsaleDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.dao.activity.ActivityDao;
import cn.edu.xmu.oomall.goods.dao.bo.*;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponDiscount;
import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponLimitation;
import cn.edu.xmu.oomall.goods.mapper.po.strategy.Item;
import cn.edu.xmu.oomall.goods.service.dto.*;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;
import static cn.edu.xmu.javaee.core.util.Common.copyObj;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;

@Service
public class CouponActService {

    private static final Logger logger = LoggerFactory.getLogger(CouponActService.class);
    private ActivityDao activityDao;
    private OnsaleDao onsaleDao;
    private ShopDao shopDao;
    private ProductDao productDao;
    private static final String COUPONACT = "couponActDao";

    @Autowired
    public CouponActService(ActivityDao activityDao, OnsaleDao onsaleDao ,ProductDao productDao,ShopDao shopDao) {
        this.activityDao = activityDao;
        this.onsaleDao = onsaleDao;
        this.productDao=productDao;
        this.shopDao=shopDao;
    }

    /**
     * 新建己方优惠活动
     *
     * @param shopId 商户id
     */
    @Transactional
    public SimpleCouponActivityDto addCouponactivity(Long shopId, CouponAct couponAct, UserDto creator) {
        couponAct.setActClass("couponActDao");
        couponAct.setShopId(shopId);
        Activity bo = this.activityDao.insert(couponAct, creator);
        SimpleCouponActivityDto dto = cloneObj(bo, SimpleCouponActivityDto.class);
        dto.setCouponTime(couponAct.getCouponTime());
        dto.setQuantity(couponAct.getQuantity());
        return dto;
    }

    /**
     * 返回店铺的所有状态优惠活动列表
     *
     * @param shopId    商户Id
     * @param onsaleId  销售Id
     * @param productId 商品id
     * @param page      页码
     * @param pageSize  每页数目
     */
    @Transactional
    public PageDto<SimpleCouponActivityDto> retrieveByShopIdAndProductIdAndOnsaleId(Long shopId, Long productId, Long onsaleId, Integer page, Integer pageSize) {
        //通过销售id或productId查询销售对象
        List<Onsale> onsales = onsaleDao.retrieveByShopIdAndOnsaleIdAndProductId(shopId, onsaleId, productId);
        List<Activity> actList = new ArrayList<>();
        //获取并合并其关联的一系列活动
        onsales.forEach(onsale -> {
            actList.addAll(onsale.getActList());
        });

        List<SimpleCouponActivityDto> couponActList = actList.stream()
                .filter(activity -> COUPONACT.equals(activity.getActClass()))
                .filter(couponAct -> shopId.equals(couponAct.getShopId()))
                .skip((long) (page-1) * pageSize).limit(pageSize)
                .map(couponAct -> {
                    SimpleCouponActivityDto simpleCouponActDto = new SimpleCouponActivityDto();
                    copyObj(couponAct, simpleCouponActDto);
                    return simpleCouponActDto;
                })
                .collect(Collectors.toList());

        return new PageDto<>(couponActList, page, pageSize);
    }

    /**
     * 根据id返回优惠活动
     *
     * @param shopId 商户Id
     * @param id     活动Id
     * @return
     */
    @Transactional
    public CouponActivityDto findCouponActivityById(Long shopId, Long id) throws BusinessException {
        Activity activity = this.activityDao.findById(id);
        //范围检查
        if (shopId != PLATFORM && !activity.getShopId().equals(shopId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "优惠活动", activity.getId(), shopId));
        }
        //判断是否为优惠活动
        if (!(activity instanceof CouponAct)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "优惠活动", id));
        }
        //组装dto数据
        CouponActivityDto dto = cloneObj(activity, CouponActivityDto.class);
        dto.setShop(cloneObj(this.shopDao.getShopById(activity.getShopId()).getData(), IdNameTypeDto.class));
        List<Onsale> onsaleList= this.onsaleDao.retrieveByActId(id,0,MAX_RETURN);
        List<SimpleOnsaleDto> dtoList=onsaleList.stream().map(obj -> {
            SimpleOnsaleDto simpleOnsaleDto = new SimpleOnsaleDto();
            copyObj(obj, simpleOnsaleDto);
            return simpleOnsaleDto;
        }).collect(Collectors.toList());
        dto.setOnsaleList(dtoList);
        LocalDateTime beginTime=onsaleList.get(0).getBeginTime();
        for(Onsale onsale:onsaleList){
           if(onsale.getBeginTime().isBefore(beginTime))
               beginTime=onsale.getBeginTime();
        }
        LocalDateTime endTime=onsaleList.get(0).getEndTime();
        for(Onsale onsale:onsaleList){
            if(onsale.getEndTime().isAfter(endTime))
                endTime=onsale.getBeginTime();
        }
        dto.setBeginTime(beginTime);
        dto.setEndTime(endTime);
        return dto;
    }

    /**
     * 修改己方某优惠活动
     *
     * @param shopId 商户Id
     * @param id     活动Id
     */
    @Transactional
    public void updateCouponActivityById(Long shopId, Long id, CouponAct act, UserDto modifier) {
        Activity activity = this.activityDao.findById(id);
        //范围检查
        if (shopId != PLATFORM && !activity.getShopId().equals(shopId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "优惠活动", activity.getId(), shopId));
        }
        //判断是否为优惠活动
        if (!(activity instanceof CouponAct)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "优惠活动", id));
        }
        act.setId(id);
        act.setObjectId(activity.getObjectId());
        act.setActClass("couponActDao");
        this.activityDao.save(act, modifier);
    }

    /**
     * 取消己方某优惠活动
     *
     * @param shopId 商户Id
     * @param id     活动Id
     */
    @Transactional
    public void deleteCouponActivityById(Long shopId, Long id) {
        Activity activity = this.activityDao.findById(id);
        //范围检查
        if (shopId != PLATFORM && !activity.getShopId().equals(shopId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "优惠活动", activity.getId(), shopId));
        }
        //判断是否为优惠活动
        if (!(activity instanceof CouponAct)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "优惠活动", id));
        }
        this.activityDao.delActivityOnsaleByActId(id);

    }

    /**
     *  计算商品优惠价格
     *
     * @param id 活动Id
     */
    @Transactional
    public List<DiscountDto> showOwncouponactivities1(Long id, List<OrderInfoVo> orderInfoVoList){
        List<DiscountDto> discountDtoList=new ArrayList<>();
        List<Item> itemList=new ArrayList<>();
        Activity activity=activityDao.findById(id);
        //判断是否为优惠活动
        if (!(activity instanceof CouponAct)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "优惠活动", id));
        }
        for(OrderInfoVo vo:orderInfoVoList){
            Product product=productDao.findProductByOnsaleId(vo.getOnsaleId());
            Item item=cloneObj(product,Item.class);
            item.setPrice(product.getPrice());
            item.setQuantity(vo.getQuantity());
            item.setOnsaleId(vo.getOnsaleId());
            item.setCouponActivityId(activity.getId());
            itemList.add(item);
        }

        try {
            BaseCouponDiscount baseCouponDiscount= BaseCouponDiscount.getInstance(((CouponAct)activity).getStrategy());
            baseCouponDiscount.compute(itemList);
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        for(Item item:itemList){
            DiscountDto discountDto=cloneObj(item,DiscountDto.class);
            discountDtoList.add(discountDto);
        }
        return discountDtoList;
    }
}