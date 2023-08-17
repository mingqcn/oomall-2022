package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.StatusDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.shop.controller.vo.*;
import cn.edu.xmu.oomall.shop.dao.bo.Shop;
import cn.edu.xmu.oomall.shop.service.ShopService;
import cn.edu.xmu.oomall.shop.service.dto.ShopDto;
import cn.edu.xmu.oomall.shop.service.dto.ProductServiceDto;
import cn.edu.xmu.oomall.shop.service.dto.SimpleShopDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.*;

/**
 * @author chenyz
 * @date 2022-11-26 10:35
 */

@RestController
@RequestMapping(value = "/shops", produces = "application/json;charset=UTF-8")
public class ShopController {

    private final Logger logger = LoggerFactory.getLogger(ShopController.class);
    
    private ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }
    
    /**
    * 获得店铺的所有状态
    * 
    * @param
    * @return List<StatusDto>
    * @author chenyz
    * @date 2022-11-26 10:40
    */
    @GetMapping("/states")
    public ReturnObject retrieveShopStates() {
        return new ReturnObject(Shop.STATUSNAMES.keySet().stream().map(key -> new StatusDto(key, Shop.STATUSNAMES.get(key))).collect(Collectors.toList()));
    }

    /**
    * 店家申请店铺
    * 
    * @param shopVo
    * @param user
    * @return SimpleShopDto
    * @author chenyz
    * @date 2022-11-26 10:51
    */
    @PostMapping("")
    @Audit(departName = "shops")
    public ReturnObject createShops(@Validated @RequestBody ShopVo shopVo, @LoginUser UserDto user) {
        if(NOSHOP != user.getDepartId() && PLATFORM != user.getDepartId())
            throw new BusinessException(ReturnNo.SHOP_USER_HASSHOP, String.format(ReturnNo.SHOP_USER_HASSHOP.getMessage(), user.getId()));
        SimpleShopDto ret = shopService.createShops(shopVo.getName(), shopVo.getConsignee().getRegionId(),
                shopVo.getConsignee().getAddress(), shopVo.getConsignee().getName(),
                shopVo.getConsignee().getMobile(), shopVo.getType(), shopVo.getFreeThreshold(), user);
        return new ReturnObject(ReturnNo.CREATED, ret);
    }

    /**
    * 顾客查询店铺信息(只返回上线和下线状态的商铺)
    *
    * @param type 店铺类型
    * @param name 店铺名称
    * @return PageDto<SimpleShopDto>
    * @author chenyz
    * @date 2022-11-26 11:02
    */
    @GetMapping("")
    public ReturnObject retrieveShops(@RequestParam(required = false) Byte type,
                                      @RequestParam(required = false) String name,
                                      @RequestParam(required = false,defaultValue = "1") Integer page,
                                      @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        PageDto<SimpleShopDto> ret = shopService.retrieveValidShops(type, name, page, pageSize);
        return new ReturnObject(ret);
    }

    /**
    * 店家修改店铺信息
    * 
    * @param id
    * @param vo
    * @param user
    * @return ReturnObject
    * @author chenyz
    * @date 2022-11-26 18:02
    */
    @PutMapping("/{id}")
    @Audit(departName = "shops")
    public ReturnObject updateShop(@PathVariable("id") Long id,
                                   @Validated @RequestBody ShopModifyVo vo,
                                   @LoginUser UserDto user){
        if(PLATFORM != user.getDepartId() && id != user.getDepartId()){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺", id, user.getDepartId()));
        }
        return shopService.updateShop(id, vo.getConsignee().getName(), vo.getConsignee().getMobile(), vo.getConsignee().getRegionId(), vo.getConsignee().getAddress(), vo.getFreeThreshold(), user);
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
    @DeleteMapping("/{id}")
    @Audit(departName = "shops")
    public ReturnObject deleteShop(@PathVariable("id") Long id, @LoginUser UserDto userDto){
        if(PLATFORM != userDto.getDepartId() && id != userDto.getDepartId()){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺", id, userDto.getDepartId()));
        }
        return shopService.deleteShop(id, userDto);
    }

    /**
    * 顾客获得店铺信息
    * 
    * @param id
    * @return ShopDto
    * @author chenyz
    * @date 2022-11-26 20:10
    */
    @GetMapping("/{id}")
    public ReturnObject findShopById(@PathVariable("id") Long id){
        logger.debug("findShopById: id = {}", id);
        ShopDto shop = shopService.findShopById(id);
        logger.debug("findShopById: shop = {}", shop);
        return new ReturnObject(shop);
    }

    /**
    * 管理员查询店铺信息(会返回所有状态的商铺)
    * 
    * @param id
    * @param type
    * @param status
    * @param name
    * @return PageDto<SimpleShopDto>
    * @author chenyz
    * @date 2022-11-26 20:54
    */
    @GetMapping("/{id}/shops")
    @Audit(departName = "shops")
    public ReturnObject retrieveAllShops(@PathVariable("id") Long id, @LoginUser UserDto userDto,
                                         @RequestParam(required = false) Byte type,
                                         @RequestParam(required = false) Byte status,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false,defaultValue = "1") Integer page,
                                         @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        if(PLATFORM != id || PLATFORM != userDto.getDepartId()) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺", id, userDto.getDepartId()));
        }
        PageDto<SimpleShopDto> ret = shopService.retrieveShops(type, status, name, page, pageSize);
        return new ReturnObject(ret);
    }

    /**
    * 平台管理员审核店铺信息
    * 
    * @param id
    * @param shopAuditVo
    * @return ReturnObject
    * @author chenyz
    * @date 2022-11-26 21:46
    */
    @PutMapping ("/{shopId}/newshops/{id}/audit")
    @Audit(departName = "shops")
    public ReturnObject updateShopAudit(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id, @LoginUser UserDto userDto, @Validated @RequestBody ShopAuditVo shopAuditVo){
        if(PLATFORM != userDto.getDepartId() || PLATFORM != shopId) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺", id, userDto.getDepartId()));
        }
        ReturnObject ret = new ReturnObject();
        if(shopAuditVo.getConclusion())
             ret = shopService.updateShopAudit(id, userDto);
        return ret;
    }

    /**
    * 平台管理员上线店铺
    * 
    * @param id
    * @return ReturnObject
    * @author chenyz
    * @date 2022-11-26 21:52
    */
    @PutMapping ("/{id}/online")
    @Audit(departName = "shops")
    public ReturnObject updateShopOnline(@PathVariable("id") Long id, @LoginUser UserDto userDto){
        if(PLATFORM != userDto.getDepartId()) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺", id, userDto.getDepartId()));
        }
        return shopService.updateShopStatus(id, Shop.ONLINE, userDto);
    }

    /**
    * 平台管理员下线店铺
    * 
    * @param id
    * @return ReturnObject
    * @author chenyz
    * @date 2022-11-26 21:56
    */
    @PutMapping("/{id}/offline")
    @Audit(departName = "shops")
    public ReturnObject updateShopOffline(@PathVariable("id") Long id, @LoginUser UserDto userDto){
        if(PLATFORM != userDto.getDepartId()) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺", id, userDto.getDepartId()));
        }
        return shopService.updateShopStatus(id, Shop.OFFLINE, userDto);
    }

    /**
    * 定义服务商在某个地区服务的商品
    *
    * @param did 商铺id
    * @param pid 商品id
    * @param mid 服务商id
    * @param rid 地区id
    * @param vo 服务信息
    * @return SimpleProductServiceDto
    * @author chenyz
    * @date 2022-11-26 23:39
    */
    @PostMapping("/{did}/products/{id}/maintainers/{mid}/regions/{rid}")
    @Audit(departName = "shops")
    public ReturnObject createProductService(@PathVariable("did") Long did, @PathVariable("id") Long pid,
                                             @PathVariable("mid") Long mid, @PathVariable("rid") Long rid,
                                             @Validated @RequestBody ProductServiceVo vo, @LoginUser UserDto userDto){
        if(PLATFORM != did && did != mid){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺", mid, did));
        }
        ProductServiceDto ret = shopService.createProductService(did, pid, mid, rid, vo.getBeginTime(), vo.getEndTime(), vo.getPriority(), userDto);
        return new ReturnObject(ReturnNo.CREATED, ret);
    }

    /**
    * 修改服务商在某个地区服务的商品
    *
    * @param did 商铺id
    * @param id 商品服务id
    * @param vo 服务信息
    * @return ReturnObject
    * @author chenyz
    * @date 2022-11-27 0:33
    */
    @PutMapping("/{did}/productservices/{id}")
    @Audit(departName = "shops")
    public ReturnObject updateProductService(@PathVariable("did") Long did, @PathVariable("id") Long id,
                                         @Validated @RequestBody ProductServiceVo vo, @LoginUser UserDto userDto){
        return shopService.updateProductService(did, id, vo.getBeginTime(), vo.getEndTime(), vo.getInvalid(), vo.getPriority(), userDto);
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
    @DeleteMapping("/{did}/productservices/{id}")
    @Audit(departName = "shops")
    public ReturnObject deleteProductService(@PathVariable("did") Long did, @PathVariable("id") Long id, @LoginUser UserDto userDto){
        return shopService.deleteProductService(did, id, userDto);
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
    @GetMapping("/{did}/products/{pid}/region/{srid}")
    @Audit(departName = "shops")
    public ReturnObject retrieveProductServiceByProductIdAndRegionId(@PathVariable("did") Long did, @PathVariable("pid") Long pid,
                                                                     @PathVariable("srid") Long rid, @LoginUser UserDto userDto,
                                                                     @RequestParam(required = false,defaultValue = "0") Byte all,
                                                                     @RequestParam(required = false,defaultValue = "1") Integer page,
                                                                     @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        PageDto<SimpleShopDto> shopDtoPageDto = shopService.retrieveProductServiceByProductIdAndRegionId(did, pid, rid, all, userDto, page, pageSize);
        return new ReturnObject(shopDtoPageDto);
    }
}
