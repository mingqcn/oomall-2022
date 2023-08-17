package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.GoodsApplication;
import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import cn.edu.xmu.oomall.goods.controller.vo.OnSaleVo;
import cn.edu.xmu.oomall.goods.dao.bo.Onsale;
import cn.edu.xmu.oomall.goods.dao.bo.Shop;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.service.dto.SimpleOnsaleDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author keyu zhu
 * @date 2022/12/11
 */
@SpringBootTest(classes = GoodsTestApplication.class)
@Transactional
public class OnsaleServiceTest {
    @MockBean
    ShopDao shopDao;
    @Autowired
    private OnsaleService onsaleService;

    @MockBean
    RedisUtil redisUtil;
    @Test
    public void testInsert(){

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        Shop shop = Shop.builder().id(10L).name("测试商铺10").build();
        Mockito.when(shopDao.getShopById(10L)).thenReturn(new InternalReturnObject<>(shop));

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setUserLevel(1);
        userDto.setName("admin2");
        userDto.setDepartId(1L);
        Onsale vo = new Onsale();
        vo.setPrice(1L);
        vo.setBeginTime(LocalDateTime.parse("2030-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2040-11-09T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setType(Byte.valueOf("0"));
        vo.setMaxQuantity(50);
        vo.setQuantity(2000);
        SimpleOnsaleDto onsaleDto = onsaleService.insert(shop.getId(), 1550L, vo, userDto);

        assertEquals(1550L, onsaleDto.getProduct().getId());
        assertEquals("欢乐家久宝桃罐头", onsaleDto.getProduct().getName());
        assertEquals(1L, onsaleDto.getPrice());
    }

    @Test
    public void testRetrieveByProductId(){

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        Shop shop = Shop.builder().id(10L).name("测试商铺10").build();
        Mockito.when(shopDao.getShopById(10L)).thenReturn(new InternalReturnObject<>(shop));
        PageDto<SimpleOnsaleDto> dtoPageDto = onsaleService.retrieveByProductId(10L,1550L, 0, 10);
        List<SimpleOnsaleDto> list = dtoPageDto.getList();
        assertEquals(1, list.size());
        assertEquals(1550L, list.get(0).getProduct().getId());
    }

    @Test
    public void testvalidateOnsale(){

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        Shop shop = Shop.builder().id(10L).name("测试商铺10").build();
        Mockito.when(shopDao.getShopById(10L)).thenReturn(new InternalReturnObject<>(shop));
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setUserLevel(1);
        userDto.setName("admin2");
        userDto.setDepartId(1L);
        onsaleService.validateOnsale(10L,1L,userDto);

    }

    @Test
    public void testinValidateOnsale(){

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        Shop shop = Shop.builder().id(10L).name("测试商铺10").build();
        Mockito.when(shopDao.getShopById(10L)).thenReturn(new InternalReturnObject<>(shop));
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setUserLevel(1);
        userDto.setName("admin2");
        userDto.setDepartId(1L);
        onsaleService.invalidateOnsale(10L,1L,userDto);

        assertEquals(10L, shop.getId());

    }

    @Test
    public void testDelete(){

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        Shop shop = Shop.builder().id(10L).name("测试商铺10").build();
        Mockito.when(shopDao.getShopById(10L)).thenReturn(new InternalReturnObject<>(shop));
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setUserLevel(1);
        userDto.setName("admin2");
        userDto.setDepartId(1L);
        onsaleService.delete(10L,1L,userDto);
    }

    @Test
    public void testSave(){

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        Shop shop = Shop.builder().id(10L).name("测试商铺10").build();
        Mockito.when(shopDao.getShopById(10L)).thenReturn(new InternalReturnObject<>(shop));
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setUserLevel(1);
        userDto.setName("admin2");
        userDto.setDepartId(1L);
        Onsale vo = new Onsale();
        vo.setPrice(1L);
        vo.setBeginTime(LocalDateTime.parse("2030-11-06T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setEndTime(LocalDateTime.parse("2040-11-09T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        vo.setType(Byte.valueOf("0"));
        vo.setMaxQuantity(50);
        vo.setQuantity(2000);
        onsaleService.save(10L,1L,vo,userDto);
    }
}
