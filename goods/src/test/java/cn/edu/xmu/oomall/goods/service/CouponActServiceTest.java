package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;

import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import cn.edu.xmu.oomall.goods.controller.vo.CouponactivityVo;
import cn.edu.xmu.oomall.goods.dao.bo.CouponAct;
import cn.edu.xmu.oomall.goods.dao.bo.Shop;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;

import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponDiscount;
import cn.edu.xmu.oomall.goods.service.CouponActService;
import cn.edu.xmu.oomall.goods.service.dto.CouponActivityDto;
import cn.edu.xmu.oomall.goods.service.dto.SimpleCouponActivityDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Liang Nan
 */
@SpringBootTest(classes = GoodsTestApplication.class)
@Transactional
public class CouponActServiceTest {
    @Autowired
    private CouponActService couponActService;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    @Autowired
    private ShopDao shopDao;

    @Test
    public void addCouponactivityTest() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        CouponAct bo = new CouponAct();
        bo.setName("优惠活动3");
        bo.setQuantity(0);
        bo.setQuantityType(1);
        bo.setCouponTime(LocalDateTime.now());
        bo.setValidTerm(0);
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);
        SimpleCouponActivityDto dto = couponActService.addCouponactivity(1L, bo, user);
        assertThat(dto.getName()).isEqualTo("优惠活动3");
    }

    @Test
    public void retrieveByShopIdAndProductIdAndOnsaleIdTest() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        PageDto<SimpleCouponActivityDto> pageDto = couponActService.retrieveByShopIdAndProductIdAndOnsaleId(11L, 1559L, 10L, 1, 10);
        assertThat(pageDto.getList().size()).isEqualTo(1);
    }


    @Test
    public void findCouponActivityByIdTest1() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Shop shop = Shop.builder().id(10L).name("kp小屋").type((byte) 1).build();
        InternalReturnObject<Shop> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setErrno(0);
        internalReturnObject.setErrmsg("成功");
        internalReturnObject.setData(shop);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(internalReturnObject);
        CouponActivityDto dto = couponActService.findCouponActivityById(10L, 12L);
        assertThat(dto.getName()).isEqualTo("优惠活动2");
    }

    @Test
    public void findCouponActivityByIdTest2() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        assertThrows(BusinessException.class, () -> couponActService.findCouponActivityById(10L, 13L));
    }

    @Test
    public void findCouponActivityByIdTest3() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        assertThrows(BusinessException.class, () -> couponActService.findCouponActivityById(11L, 12L));
    }

    @Test
    public void updateCouponActivityByIdTest1() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        CouponAct bo = new CouponAct();
        bo.setName("优惠活动3");
        bo.setQuantity(0);
        bo.setQuantityType(1);
        bo.setCouponTime(LocalDateTime.now());
        bo.setValidTerm(0);
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);
        couponActService.updateCouponActivityById(10L, 11L, bo, user);
    }

    @Test
    public void updateCouponActivityByIdTest2() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        CouponAct bo = new CouponAct();
        bo.setName("优惠活动3");
        bo.setQuantity(0);
        bo.setQuantityType(1);
        bo.setCouponTime(LocalDateTime.now());
        bo.setValidTerm(0);
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);
        assertThrows(BusinessException.class, () -> couponActService.updateCouponActivityById(10L, 13L, bo, user));
    }

    @Test
    public void updateCouponActivityByIdTest3() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        CouponAct bo = new CouponAct();
        bo.setName("优惠活动3");
        bo.setQuantity(0);
        bo.setQuantityType(1);
        bo.setCouponTime(LocalDateTime.now());
        bo.setValidTerm(0);
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);
        assertThrows(BusinessException.class, () -> couponActService.updateCouponActivityById(11L, 11L, bo, user));
    }

    @Test
    public void deleteCouponActivityByIdTest1() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        couponActService.deleteCouponActivityById(10L, 11L);
    }

    @Test
    public void deleteCouponActivityByIdTest2() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        assertThrows(BusinessException.class, () -> couponActService.deleteCouponActivityById(11L, 11L)).getErrno().equals(ReturnNo.RESOURCE_ID_NOTEXIST);
    }

    @Test
    public void deleteCouponActivityByIdTest3() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        assertThrows(BusinessException.class, () -> couponActService.deleteCouponActivityById(10L, 13L)).getErrno().equals(ReturnNo.RESOURCE_ID_NOTEXIST);
    }
}