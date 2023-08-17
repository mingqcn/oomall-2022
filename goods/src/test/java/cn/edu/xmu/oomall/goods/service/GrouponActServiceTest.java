package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import cn.edu.xmu.oomall.goods.controller.vo.GrouponActVo;
import cn.edu.xmu.oomall.goods.dao.bo.Shop;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import cn.edu.xmu.oomall.goods.service.dto.FullGrouponActDto;
import cn.edu.xmu.oomall.goods.service.dto.GrouponActDto;
import cn.edu.xmu.oomall.goods.service.dto.SimpleGrouponActDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author prophesier
 * @create 2022-12-07 8:24
 */
@SpringBootTest(classes = GoodsTestApplication.class)
@Transactional
public class GrouponActServiceTest {
    @Autowired
    private GrouponActService grouponActService;

    @MockBean
    @Autowired
    private ShopDao shopDao;

    @MockBean
    private RedisUtil redisUtil;

    @Test
    public void retrieveByShopIdAndProductIdOrOnsaleIdTest(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        PageDto<SimpleGrouponActDto> pageDto = grouponActService.retrieveByShopIdAndProductIdAndOnsaleId(4L, 1576L, 2L, 1, 10);

        assertThat(pageDto.getList().size()).isEqualTo(3);
        assertThat(pageDto.getList().get(0).getName()).isEqualTo("团购活动2");
        assertThat(pageDto.getList().get(1).getName()).isEqualTo("团购活动3");
    }

    @Test
    public void retrieveByShopIdAndProductIdTest(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        PageDto<SimpleGrouponActDto> simpleGrouponActDtoPageDto = grouponActService.retrieveByShopIdAndProductId(4L, 1576L, 1, 10);
        assertThat(simpleGrouponActDtoPageDto.getList().get(0).getName()).isEqualTo("团购活动2");
    }

    @Test
    public void findByIdTest(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        InternalReturnObject<Shop> retObj = new InternalReturnObject<>();
        retObj.setErrno(0);
        retObj.setErrmsg("成功");
        retObj.setData(Shop.builder().id(10L).name("商铺10").type((byte)0).build());
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(retObj);

        GrouponActDto grouponActDto = grouponActService.findById(1L);
        assertThat(grouponActDto.getShop().getName()).isEqualTo("商铺10");
        assertThat(grouponActDto.getThresholds().size()).isEqualTo(1);
        assertThat(grouponActDto.getThresholds().get(0).getQuantity()).isEqualTo(52);
    }

    @Test
    public void findByIdTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        InternalReturnObject<Shop> retObj = new InternalReturnObject<>();
        retObj.setErrno(0);
        retObj.setErrmsg("成功");
        retObj.setData(Shop.builder().id(10L).name("商铺10").type((byte)0).build());
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(retObj);
        assertThrows(BusinessException.class, () -> grouponActService.findById(6L));
    }

    @Test
    public void findByShopIdAndActIdTest(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        InternalReturnObject<Shop> retObj = new InternalReturnObject<>();
        retObj.setErrno(0);
        retObj.setErrmsg("成功");
        retObj.setData(Shop.builder().id(10L).name("商铺10").type((byte)0).build());
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(retObj);

        assertThrows(BusinessException.class, () -> grouponActService.findByShopIdAndActId(4L, 6L));
    }

    @Test
    public void findByShopIdAndActIdTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        InternalReturnObject<Shop> retObj = new InternalReturnObject<>();
        retObj.setErrno(0);
        retObj.setErrmsg("成功");
        retObj.setData(Shop.builder().id(10L).name("商铺10").type((byte)0).build());
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(retObj);

        FullGrouponActDto fullGrouponActDto = grouponActService.findByShopIdAndActId(1L, 4L);

        assertThat(fullGrouponActDto.getName()).isEqualTo("团购活动4");
        assertThat(fullGrouponActDto.getThresholds().get(0).getPercentage()).isEqualTo(12L);
    }

    @Test
    public void findByShopIdAndActIdTest3(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        InternalReturnObject<Shop> retObj = new InternalReturnObject<>();
        retObj.setErrno(0);
        retObj.setErrmsg("成功");
        retObj.setData(Shop.builder().id(10L).name("商铺10").type((byte)0).build());
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(retObj);

        assertThrows(BusinessException.class, () -> grouponActService.findByShopIdAndActId(3L, 4L));
    }


    @Test
    public void createGrouponActTest(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("test1");
        userDto.setUserLevel(1);
        ThresholdPo strategy = new ThresholdPo(13, 135L);
        String name= "团购活动";
        SimpleGrouponActDto grouponActDto = grouponActService.createGrouponAct(4L,2L, name,strategy, userDto);

        assertThat(grouponActDto.getName()).isEqualTo("团购活动");
        assertThat(grouponActDto.getThresholds().get(0).getQuantity()).isEqualTo(13);
        assertThat(grouponActDto.getThresholds().get(0).getPercentage()).isEqualTo(135);
    }

    @Test
    public void createGrouponActTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("test1");
        userDto.setUserLevel(1);
        ThresholdPo strategy = new ThresholdPo(13, 135L);
        String name= "团购活动";
        SimpleGrouponActDto grouponActDto = grouponActService.createGrouponAct(4L,5L, name,strategy, userDto);

        assertThat(grouponActDto.getName()).isEqualTo("团购活动");
        assertThat(grouponActDto.getThresholds().get(0).getQuantity()).isEqualTo(13);
        assertThat(grouponActDto.getThresholds().get(0).getPercentage()).isEqualTo(135);
    }

    @Test
    public void updateByActIdTest(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("test1");
        userDto.setUserLevel(1);
        ThresholdPo strategy = new ThresholdPo(13, 135L);
        GrouponActVo grouponActVo = new GrouponActVo("团购活动",strategy);
        ReturnObject ret = grouponActService.updateById(8L,1L, grouponActVo.getName(),grouponActVo.getStrategy(), userDto);
        assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);

    }

    @Test
    public void updateByActIdTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("test1");
        userDto.setUserLevel(1);
        ThresholdPo strategy = new ThresholdPo(13, 135L);
        GrouponActVo grouponActVo = new GrouponActVo("团购活动",strategy);
        assertThrows(BusinessException.class, () -> grouponActService.updateById(11L,3L, grouponActVo.getName(),grouponActVo.getStrategy(), userDto));

    }

    @Test
    public void cancelByIdTest1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("test1");
        userDto.setUserLevel(1);
        ReturnNo ret = grouponActService.cancelById(8L,1L, userDto);
        assertThat(ret).isEqualTo(ReturnNo.OK);
    }

    @Test
    public void cancelByIdTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("test1");
        userDto.setUserLevel(1);
        assertThrows(BusinessException.class, () -> grouponActService.cancelById(5L,5L,userDto));
    }

    @Test
    public void cancelByIdTest3(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("test1");
        userDto.setUserLevel(1);
        ReturnNo ret = grouponActService.cancelById(4L,2L,userDto);
        assertThat(ret).isEqualTo(ReturnNo.OK);
    }



}
