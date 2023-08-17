package cn.edu.xmu.oomall.shop.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.shop.dao.ShopDao;
import cn.edu.xmu.oomall.shop.dao.ShopServiceProductDao;
import cn.edu.xmu.oomall.shop.dao.bo.Shop;
import cn.edu.xmu.oomall.shop.dao.bo.ShopServiceProduct;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.shop.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.util.Common.*;

/**
 * @author chenyz
 * @date 2022-11-26 10:38
 */
@Service
public class ShopService {

    private static  final Logger logger = LoggerFactory.getLogger(ShopService.class);

    private ShopDao shopDao;

    private ShopServiceProductDao shopServiceProductDao;

    private GoodsDao goodsDao;

    private FreightDao freightDao;

    @Autowired
    public ShopService(ShopDao shopDao, ShopServiceProductDao shopServiceProductDao, GoodsDao goodsDao, FreightDao freightDao){
        this.shopDao = shopDao;
        this.shopServiceProductDao = shopServiceProductDao;
        this.goodsDao = goodsDao;
        this.freightDao = freightDao;
    }

    /**
    * 店家申请店铺
    *
    * @param
    * @return SimpleShopDto
    * @author chenyz
    * @date 2022-11-26 18:04
    */
    public SimpleShopDto createShops(String name, Long regionId, String address, String consignee, String mobile, Byte type, Integer freeThreshold, UserDto user){
        if(PLATFORM != user.getDepartId()){
            PageDto<Shop> shops = shopDao.retrieveByCreatorId(user.getId(), 0, 10);
            if(!shops.getList().isEmpty()) {
                throw new BusinessException(ReturnNo.SHOP_USER_HASSHOP, String.format(ReturnNo.SHOP_USER_HASSHOP.getMessage(), user.getId()));
            }
        }
        Shop shop = Shop.builder().name(name).regionId(regionId).address(address).consignee(consignee).mobile(mobile).type(type).status(Shop.NEW).freeThreshold(freeThreshold).build();
        return cloneObj(shopDao.insert(shop, user), SimpleShopDto.class);
    }

    /**
    * 顾客查询店铺信息(只返回上线和下线状态的商铺)
    *
    * @param type 店铺类型
    * @param name 店铺名称
    * @return PageInfo<SimpleShopDto>
    * @author chenyz
    * @date 2022-11-26 18:04
    */
    public PageDto<SimpleShopDto> retrieveValidShops(Byte type, String name, Integer page, Integer pageSize){
        PageDto<Shop> shopPageDto = shopDao.retrieveValidByTypeAndName(type, name, page, pageSize);
        List<SimpleShopDto> ret = new ArrayList<>();
        if(null != shopPageDto && shopPageDto.getList().size() > 0)
            ret = shopPageDto.getList().stream().map(po -> cloneObj(po, SimpleShopDto.class)).collect(Collectors.toList());
        return new PageDto<>(ret, page, pageSize);
    }

    /**
     * 管理员查询店铺信息(会返回所有状态的商铺)
     *
     * @param type
     * @param status
     * @param name
     * @return PageDto<SimpleShopDto>
     * @author chenyz
     * @date 2022-11-26 20:54
     */
    public PageDto<SimpleShopDto> retrieveShops(Byte type, Byte status, String name, Integer page, Integer pageSize){
        PageDto<Shop> shopPageDto = shopDao.retrieveByTypeAndStatusAndName(type, status, name, page, pageSize);
        List<SimpleShopDto> ret = new ArrayList<>();
        if(null != shopPageDto && shopPageDto.getList().size() > 0)
            ret = shopPageDto.getList().stream().map(po -> cloneObj(po, SimpleShopDto.class)).collect(Collectors.toList());
        return new PageDto<>(ret, page, pageSize);
    }

    /**
     * 店家获得店铺信息
     *
     * @param id
     * @return ShopDto
     * @author chenyz
     * @date 2022-11-26 20:10
     */
    public ShopDto findShopById(Long id){
        Optional<Shop> ret = shopDao.findById(id);
        if (ret.isPresent()) {
            Shop shop = ret.get();
            IdNameDto creator = (null == shop.getCreatorId()) ? null : IdNameDto.builder().id(shop.getCreatorId()).name(shop.getCreatorName()).build();
            IdNameDto modifier = (null == shop.getModifierId()) ? null : IdNameDto.builder().id(shop.getModifierId()).name(shop.getModifierName()).build();
            ConsigneeDto consignee = ConsigneeDto.builder().regionId(shop.getRegionId()).address(shop.getAddress()).name(shop.getConsignee()).mobile(shop.getMobile()).build();
            return ShopDto.builder().id(shop.getId()).creator(creator).modifier(modifier).gmtCreate(shop.getGmtCreate()).gmtModified(shop.getGmtModified())
                    .name(shop.getName()).deposit(shop.getDeposit()).depositThreshold(shop.getDepositThreshold()).status(shop.getStatus()).consignee(consignee).type(shop.getType()).freeThreshold(shop.getFreeThreshold()).build();
        }else{
            return null;
        }
    }

    /**
    * 店家修改店铺信息
    *
    * @param
    * @return ReturnObject
    * @author chenyz
    * @date 2022-11-26 18:05
    */
    public ReturnObject updateShop(Long id, String name, String mobile, Long regionId, String address, Integer freeThreshold, UserDto user){
        Optional<Shop> ret = shopDao.findById(id);
        Shop shop = ret.get();
        if(!shop.getStatus().equals(Shop.ABANDON)){
            shop.setConsignee(name);
            shop.setMobile(mobile);
            shop.setRegionId(regionId);
            shop.setAddress(address);
            shop.setFreeThreshold(freeThreshold);
            shopDao.save(shop, user);
        }else{
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "商铺", id, shop.getStatusName()));
        }
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 平台管理员修改店铺状态(需要满足状态迁移条件)
     *
     * @param shopId
     * @param status
     * @return ReturnObject
     * @author chenyz
     * @date 2022-11-26 21:46
     */
    public ReturnObject updateShopStatus(Long shopId, Byte status, UserDto userDto){
        Optional<Shop> ret = shopDao.findById(shopId);
        Shop shop = ret.get();
        if(shop.allowStatus(status)){
            shop.setStatus(status);
            shopDao.save(shop, userDto);
        }else{
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "商铺", shopId, shop.getStatusName()));
        }
        return new ReturnObject(ReturnNo.OK);
    }

    /**
    * 平台管理员审核店铺
    *
    * @param shopId
    * @return ReturnObject
    * @author chenyz
    * @date 2022-12-29 11:06
    */
    public ReturnObject updateShopAudit(Long shopId, UserDto userDto){
        Optional<Shop> ret = shopDao.findById(shopId);
        Shop shop = ret.get();
        if(Shop.NEW == shop.getStatus()){
            shop.setStatus(Shop.OFFLINE);
            shopDao.save(shop, userDto);
        }else{
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "商铺", shopId, shop.getStatusName()));
        }
        return new ReturnObject(ReturnNo.OK);
    }

    /**
    * 平台管理员或店家关闭店铺(只有下线的商铺才能关闭)
    * 
    * @param id
    * @param userDto
    * @return ReturnObject
    * @author chenyz
    * @date 2022-11-26 18:24
    */
    public ReturnObject deleteShop(Long id, UserDto userDto){
        Optional<Shop> ret = shopDao.findById(id);
        Shop shop = ret.get();
        if(Shop.SERVICE == shop.getType()){
            List<Long> ids = shopServiceProductDao.retrieveProductServiceByMaintainerId(id);
            ids.stream().forEach(SPid -> shopServiceProductDao.deleteById(SPid));
        }
        this.updateShopStatus(id, Shop.ABANDON, userDto);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 定义服务商在某个地区服务的商品
     *
     * @param did 商铺id
     * @param pid 商品id
     * @param mid 服务商id
     * @param rid 地区id
     * @return SimpleProductServiceDto
     * @author chenyz
     * @date 2022-11-26 23:39
     */
    public ProductServiceDto createProductService(Long did, Long pid, Long mid, Long rid, LocalDateTime beginTime, LocalDateTime endTime, Integer priority, UserDto userDto){
        Optional<Shop> ret = shopDao.findById(mid);
        Shop shop = ret.orElse(null);
        //当前商铺不是服务商
        if(!shop.getType().equals(Shop.SERVICE))
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, String.format(ReturnNo.FIELD_NOTVALID.getMessage(), "服务商id"));
        //处于停用、申请的服务商无法定义
        if(shop.getStatus().equals(Shop.ABANDON) || shop.getStatus().equals(Shop.NEW))
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "商铺", mid, shop.getStatusName()));
        //判断该商品和地区是否存在
        if (null == goodsDao.retrieveProductById(pid).getData() || null == freightDao.findRegionById(rid).getData())
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商品或地区", pid));

        ShopServiceProduct serviceProduct = new ShopServiceProduct(beginTime, endTime, ShopServiceProduct.INVALID, null == priority ? 1000: priority, pid, mid, rid);
        ShopServiceProduct bo = this.shopServiceProductDao.insert(serviceProduct, userDto);
        logger.info("ShopId:{}", bo.getMaintainerId());
        IdNameDto creator = (null == shop.getCreatorId()) ? null : IdNameDto.builder().id(shop.getCreatorId()).name(shop.getCreatorName()).build();
        IdNameDto modifier = (null == shop.getModifierId()) ? null : IdNameDto.builder().id(shop.getModifierId()).name(shop.getModifierName()).build();
        ProductServiceDto serviceDto = ProductServiceDto.builder().id(bo.getId()).beginTime(bo.getBeginTime()).endTime(bo.getEndTime()).invalid(bo.getInvalid()).priority(bo.getPriority()).product(bo.getProduct()).maintainer(cloneObj(bo.getShop(), SimpleShopDto.class)).region(bo.getRegion()).creator(creator).modifier(modifier).gmtCreate(bo.getGmtCreate()).gmtModified(bo.getGmtModified()).build();
        return serviceDto;
    }

    /**
     * 修改服务商在某个地区服务的商品
     *
     * @param did 商铺id
     * @param id 商品服务id
     * @return ReturnObject
     * @author chenyz
     * @date 2022-11-27 0:33
     */
    public ReturnObject updateProductService(Long did, Long id, LocalDateTime beginTime, LocalDateTime endTime, Byte invalid, Integer priority, UserDto userDto){
        Optional<ShopServiceProduct> ret = shopServiceProductDao.findById(id);
        ShopServiceProduct serviceProduct = ret.orElse(null);
        if (PLATFORM != did && !did.equals(serviceProduct.getMaintainerId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺", did, serviceProduct.getMaintainerId()));
        }
        serviceProduct.setBeginTime(beginTime);
        serviceProduct.setEndTime(endTime);
        serviceProduct.setInvalid(invalid);
        serviceProduct.setPriority(priority);
        shopServiceProductDao.save(serviceProduct, userDto);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 取消服务商在某个地区服务的商品
     *
     * @param did 商铺id
     * @param id 商品服务id
     * @return ReturnObject
     * @author chenyz
     * @date 2022-11-27 0:45
     */
    public ReturnObject deleteProductService(Long did, Long id, UserDto userDto){
        Optional<ShopServiceProduct> ret = shopServiceProductDao.findById(id);
        ShopServiceProduct serviceProduct = ret.orElse(null);
        if (PLATFORM != did && !did.equals(serviceProduct.getMaintainerId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺", did, serviceProduct.getMaintainerId()));
        }
        shopServiceProductDao.deleteById(id);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 通过商品和地区找到服务商
     *
     * @param did 商铺id
     * @param pid 商品id
     * @param rid 地区id
     * @return
     * @author chenyz
     * @date 2022-11-27 1:16
     */
    public PageDto<SimpleShopDto> retrieveProductServiceByProductIdAndRegionId(Long did, Long pid, Long rid, Byte all, UserDto userDto, Integer page, Integer pageSize){
        return shopServiceProductDao.retrieveProductServiceByProductIdAndRegionId(pid, rid, all, page, pageSize);
    }

}
