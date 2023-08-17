package cn.edu.xmu.oomall.shop.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.ShopTestApplication;
import cn.edu.xmu.oomall.shop.dao.bo.Shop;
import cn.edu.xmu.oomall.shop.dao.bo.ShopServiceProduct;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Product;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import cn.edu.xmu.oomall.shop.service.dto.ShopDto;
import cn.edu.xmu.oomall.shop.service.dto.ProductServiceDto;
import cn.edu.xmu.oomall.shop.service.dto.SimpleShopDto;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author chenyz
 * @date 2022-11-29 11:06
 */
@SpringBootTest(classes = ShopTestApplication.class)
@Transactional
public class ShopServiceTest {

    @Autowired
    ShopService shopService;

    @MockBean
    RocketMQTemplate rocketMQTemplate;
    @MockBean
    GoodsDao goodsDao;

    @MockBean
    FreightDao freightDao;

    @MockBean
    RedisUtil redisUtil;

    UserDto user = new UserDto(1L, "test1", 0L, 1);

    @Test
    public void createShops(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setDepartId(-1L);
        user.setName("test1");
        user.setUserLevel(1);
        SimpleShopDto retShop = shopService.createShops("test", 2L, "test", "John", "123", Shop.SERVICE, 0, user);
        assertThat(retShop.getName()).isEqualTo("test");
        assertThat(retShop.getType()).isEqualTo(Shop.SERVICE);
    }

    @Test
    public void retrieveValidShops1(){
        List<SimpleShopDto> list = shopService.retrieveValidShops(null, null, 1, 10).getList();
        assertThat(list.size()).isEqualTo(10);
        SimpleShopDto bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(bo.getType()).isEqualTo(Shop.RETAILER);
    }

    @Test
    public void retrieveValidShops2(){
        List<SimpleShopDto> list = shopService.retrieveValidShops(Shop.SERVICE, "服务商1", 1, 10).getList();
        assertThat(list.size()).isEqualTo(1);
        SimpleShopDto bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(45L);
        assertThat(bo.getName()).isEqualTo("服务商1");
        assertThat(bo.getType()).isEqualTo(Shop.SERVICE);
    }

    @Test
    public void retrieveValidShops3(){
        List<SimpleShopDto> list = shopService.retrieveValidShops(Shop.SERVICE, "坚持就是胜利", 1, 10).getList();
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    public void retrieveShops1(){
        List<SimpleShopDto> list = shopService.retrieveShops(null, null, null, 1, 20).getList();
        assertThat(list.size()).isEqualTo(14);
        SimpleShopDto bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(bo.getType()).isEqualTo(Shop.RETAILER);
    }

    @Test
    public void retrieveShops2(){
        List<SimpleShopDto> list = shopService.retrieveShops(null, Shop.NEW, null, 1, 10).getList();
        assertThat(list.size()).isEqualTo(4);
        SimpleShopDto bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(8L);
        assertThat(bo.getName()).isEqualTo("商铺8");
        assertThat(bo.getType()).isEqualTo(Shop.RETAILER);
    }

    @Test
    public void retrieveShops3(){
        List<SimpleShopDto> list = shopService.retrieveShops(null, Shop.NEW, "商铺10", 1, 10).getList();
        assertThat(list.size()).isEqualTo(1);
        SimpleShopDto bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(10L);
        assertThat(bo.getName()).isEqualTo("商铺10");
        assertThat(bo.getType()).isEqualTo(Shop.RETAILER);
    }

    @Test
    public void retrieveShops4(){
        List<SimpleShopDto> list = shopService.retrieveShops(null, Shop.OFFLINE, "商铺10", 1, 10).getList();
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    public void findShopById1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        ShopDto retShop =  shopService.findShopById(45L);

        assertThat(retShop.getId()).isEqualTo(Long.valueOf(45));
        assertThat(retShop.getName()).isEqualTo("服务商1");
        assertThat(retShop.getStatus()).isEqualTo(Shop.OFFLINE);
        assertThat(retShop.getConsignee().getRegionId()).isEqualTo(367413L);
        assertThat(retShop.getConsignee().getAddress()).isEqualTo("郑州日报社");
        assertThat(retShop.getType()).isEqualTo(Shop.SERVICE);
    }

    @Test
    public void findShopById2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        assertThrows(BusinessException.class, ()-> shopService.findShopById(100L));
    }

    @Test
    public void updateShop1(){
        assertThrows(BusinessException.class, ()-> shopService.updateShop(100L, "John", "123", 10L, "address", 0, user));
    }

    @Test
    public void updateShop2(){
        ReturnObject ret = shopService.updateShop(1L, "John", "123", 10L, "address", 0, user);
        Assertions.assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }

    @Test
    public void updateShopStatus1(){
        assertThrows(BusinessException.class, ()-> shopService.updateShopStatus(8L, Shop.OFFLINE, user));
    }

    @Test
    public void updateShopStatus2(){
        ReturnObject ret = shopService.updateShopStatus(5L, Shop.ONLINE, user);
        Assertions.assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }

    @Test
    public void updateShopStatus3(){
        assertThrows(BusinessException.class, ()-> shopService.updateShopStatus(8L, Shop.ONLINE, user));
    }

    @Test
    public void updateShopStatus4(){
        assertThrows(BusinessException.class, ()-> shopService.updateShopStatus(50L, Shop.ONLINE, user));
    }

    @Test
    public void deleteShop1(){
        ReturnObject ret = shopService.deleteShop(5L, user);
        Assertions.assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }

    @Test
    public void deleteShop2(){
        ReturnObject ret = shopService.deleteShop(45L, user);
        Assertions.assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }

    @Test
    public void deleteShop3(){
        assertThrows(BusinessException.class, ()-> shopService.deleteShop(1L, user));
    }

    @Test
    public void createProductService1(){
        InternalReturnObject<Product> tmp=new InternalReturnObject<>(new Product(){{setId(5107L);}});
        tmp.setErrno(ReturnNo.OK.getErrNo());
        tmp.setErrmsg(ReturnNo.OK.getMessage());
        Mockito.when(goodsDao.retrieveProductById(5107L)).thenReturn(tmp);

        Mockito.when(freightDao.findRegionById(10L)).thenReturn(new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region(10L,"xxx")));
        ProductServiceDto serviceDto = shopService.createProductService(45L, 5107L, 45L, 10L, LocalDateTime.now(), LocalDateTime.now(), null, user);
        assertThat(serviceDto.getInvalid()).isEqualTo(ShopServiceProduct.INVALID);
        assertThat(serviceDto.getPriority()).isEqualTo(1000);
        assertThat(serviceDto.getRegion().getId()).isEqualTo(10L);
        assertThat(serviceDto.getProduct().getId()).isEqualTo(5107L);
        assertThat(serviceDto.getMaintainer().getId()).isEqualTo(45L);
    }

    @Test
    public void createProductService2(){
        assertThrows(BusinessException.class, ()-> shopService.createProductService(50L, 5107L, 50L, 10L, LocalDateTime.now(), LocalDateTime.now(), null, user));
    }

    @Test
    public void createProductService3(){
        assertThrows(BusinessException.class, ()-> shopService.createProductService(1L, 5107L, 1L, 10L, LocalDateTime.now(), LocalDateTime.now(), null, user));
    }

    @Test
    public void createProductService4(){
        InternalReturnObject<Region> tmp1=new InternalReturnObject<>(null);
        tmp1.setErrno(ReturnNo.OK.getErrNo());
        tmp1.setErrmsg(ReturnNo.OK.getMessage());
        InternalReturnObject<Product> tmp2=new InternalReturnObject<>(null);
        tmp2.setErrno(ReturnNo.OK.getErrNo());
        tmp2.setErrmsg(ReturnNo.OK.getMessage());
        Mockito.when(freightDao.findRegionById(10000L)).thenReturn(tmp1);
        Mockito.when(goodsDao.retrieveProductById(80000L)).thenReturn(tmp2);
        assertThrows(BusinessException.class, ()-> shopService.createProductService(45L, 80000L, 45L, 10000L, LocalDateTime.now(), LocalDateTime.now(), null, user));
    }

    @Test
    public void updateProductService1(){
        ReturnObject ret = shopService.updateProductService(45L, 1L, LocalDateTime.now(), LocalDateTime.now(), ShopServiceProduct.VALID, 1, user);
        Assertions.assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }

    @Test
    public void updateProductService2(){
        assertThrows(BusinessException.class, ()-> shopService.updateProductService(45L, 10L, LocalDateTime.now(), LocalDateTime.now(), ShopServiceProduct.VALID, 1, user));
    }

    @Test
    public void updateProductService3(){
        assertThrows(BusinessException.class, ()-> shopService.updateProductService(40L, 3L, LocalDateTime.now(), LocalDateTime.now(), ShopServiceProduct.VALID, 1, user));
    }

    @Test
    public void deleteProductService1(){
        ReturnObject ret = shopService.deleteProductService(45L, 1L, user);
        Assertions.assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }

    @Test
    public void deleteProductService2(){
        assertThrows(BusinessException.class, ()-> shopService.deleteProductService(45L, 10L, user));
    }

    @Test
    public void deleteProductService3(){
        assertThrows(BusinessException.class, ()-> shopService.deleteProductService(50L, 3L, user));
    }

    @Test
    public void retrieveProductServiceByProductIdAndRegionId1(){
        PageDto<SimpleShopDto> shopDtoPageDto =  shopService.retrieveProductServiceByProductIdAndRegionId(45L, 5107L, 152L, (byte) 1, user , 1, 10);
        SimpleShopDto shopDto = shopDtoPageDto.getList().get(0);
        assertThat(shopDto.getId()).isEqualTo(45L);
        assertThat(shopDto.getName()).isEqualTo("服务商1");
        assertThat(shopDto.getType()).isEqualTo(Shop.SERVICE);
    }

    @Test
    public void retrieveProductServiceByProductIdAndRegionId2(){
        PageDto<SimpleShopDto> shopDtoPageDto =  shopService.retrieveProductServiceByProductIdAndRegionId(45L, 5107L, 1L, (byte) 1, user , 1, 10);
        assertThat(shopDtoPageDto.getList().isEmpty()).isTrue();
    }
}
