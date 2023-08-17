package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.GoodsApplication;
import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import cn.edu.xmu.oomall.goods.controller.vo.ShareActVo;
import cn.edu.xmu.oomall.goods.controller.vo.ThresholdVo;
import cn.edu.xmu.oomall.goods.dao.bo.Shop;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import cn.edu.xmu.oomall.goods.service.dto.FullShareActDto;
import cn.edu.xmu.oomall.goods.service.dto.ShareActDto;
import cn.edu.xmu.oomall.goods.service.dto.SimpleOnsaleDto;
import cn.edu.xmu.oomall.goods.service.dto.SimpleShareActDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author 黄坤鹏
 * @date 2022/12/1 12:22
 */
@SpringBootTest(classes = GoodsTestApplication.class)
@Transactional
public class ShareActServiceTest {

    @Autowired
    private ShareActService shareActService;

    @MockBean
    private ShopDao shopDao;

    @MockBean
    private RedisUtil redisUtil;

    @Test
    public void retrieveByShopIdAndProductIdOrOnsaleIdTest(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        PageDto<SimpleShareActDto> pageDto = shareActService.retrieveByShopIdAndOnsaleIdAndProductId(4L, 1552L, 3L, 1, 10);

        assertThat(pageDto.getList().size()).isEqualTo(2);
        assertThat(pageDto.getList().get(0).getName()).isEqualTo("青春飞扬");
        assertThat(pageDto.getList().get(1).getName()).isEqualTo("分享冲冲冲");
    }

    @Test
    public void createShareActTest(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("test1");
        userDto.setUserLevel(1);
        List<ThresholdVo> thresholds = new ArrayList<>(){
            {
                add(new ThresholdVo(12, 135L));
            }
        };
        ShareActVo shareActVo = new ShareActVo("分享共赢", thresholds);
        SimpleShareActDto shareAct = shareActService.createShareAct(3L, shareActVo, userDto);

        assertThat(shareAct.getName()).isEqualTo("分享共赢");
        assertThat(shareAct.getThresholds().get(0).getQuantity()).isEqualTo(12);
    }

    @Test
    public void retrieveByShopIdAndProductIdTest(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        PageDto<SimpleShareActDto> simpleShareActDtoPageDto = shareActService.retrieveByShopIdAndProductIdAndInvalidEquals(8L, 5461L, 1, 10);
        assertThat(simpleShareActDtoPageDto.getList().get(0).getName()).isEqualTo("与你有约");
    }

    @Test
    public void findByIdTest1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Shop shop = Shop.builder().id(4L).name("kp小屋").type((byte) 1).build();
        InternalReturnObject<Shop> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setErrno(0);
        internalReturnObject.setErrmsg("成功");
        internalReturnObject.setData(shop);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(internalReturnObject);

        ShareActDto shareActDto = shareActService.findById(1L);
        assertThat(shareActDto.getShop().getName()).isEqualTo("kp小屋");
        assertThat(shareActDto.getOnsaleList().size()).isEqualTo(2);
        assertThat(shareActDto.getThresholds().size()).isEqualTo(1);
    }

    @Test
    public void findByIdTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Shop shop = Shop.builder().id(4L).name("kp小屋").type((byte) 1).build();
        InternalReturnObject<Shop> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setErrno(3);
        internalReturnObject.setErrmsg("成功");
        internalReturnObject.setData(shop);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(internalReturnObject);

        assertThrows(BusinessException.class, () -> shareActService.findById(1L));
    }

    @Test
    public void createActivityOnsaleTest1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Shop shop = Shop.builder().id(4L).name("kp小屋").type((byte) 1).build();
        InternalReturnObject<Shop> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setErrno(0);
        internalReturnObject.setErrmsg("成功");
        internalReturnObject.setData(shop);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(internalReturnObject);
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("test1");
        userDto.setUserLevel(1);

        SimpleOnsaleDto activityOnsale = shareActService.createActivityOnsale(2L, 2L, userDto);
        assertThat(activityOnsale.getProduct().getName()).isEqualTo("欢乐家杨梅罐头");
    }
    

    @Test
    public void delActivityOnsaleTest1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        assertThrows(BusinessException.class, () -> shareActService.cancelActivityOnsale(8L, 1L, 8L));
    }

    @Test
    public void delActivityOnsaleTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        assertThrows(BusinessException.class, () -> shareActService.cancelActivityOnsale(4L, 1L, 1L));
    }

    @Test
    public void findByShopIdAndActIdTest1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Shop shop = Shop.builder().id(4L).name("kp小屋").type((byte) 1).build();
        InternalReturnObject<Shop> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setErrno(0);
        internalReturnObject.setErrmsg("成功");
        internalReturnObject.setData(shop);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(internalReturnObject);

        FullShareActDto shareActDto = shareActService.findByShopIdAndActId(4L, 2L);
        assertThat(shareActDto.getName()).isEqualTo("青春飞扬");
    }

    @Test
    public void findByShopIdAndActIdTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Shop shop = Shop.builder().id(4L).name("kp小屋").type((byte) 1).build();
        InternalReturnObject<Shop> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setErrno(0);
        internalReturnObject.setErrmsg("成功");
        internalReturnObject.setData(shop);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(internalReturnObject);

        assertThrows(BusinessException.class, () -> shareActService.findByShopIdAndActId(5L, 2L));
    }

    @Test
    public void findByShopIdAndActIdTest3(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Shop shop = Shop.builder().id(4L).name("kp小屋").type((byte) 1).build();
        InternalReturnObject<Shop> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setErrno(3);
        internalReturnObject.setErrmsg("成功");
        internalReturnObject.setData(shop);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(internalReturnObject);

        assertThrows(BusinessException.class, () -> shareActService.findByShopIdAndActId(4L, 2L));
    }


    @Test
    public void updateByActIdTest1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("test1");
        userDto.setUserLevel(1);
        List<ThresholdVo> thresholds = new ArrayList<>(){
            {
                add(new ThresholdVo(12, 135L));
            }
        };
        ShareActVo shareActVo = new ShareActVo("分享共赢", thresholds);
        shareActService.updateByActId(8L, 1L, shareActVo, userDto);

    }

    @Test
    public void updateByActIdTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setName("test1");
        userDto.setUserLevel(1);
        List<ThresholdVo> thresholds = new ArrayList<>(){
            {
                add(new ThresholdVo(12, 135L));
            }
        };
        ShareActVo shareActVo = new ShareActVo("分享共赢", thresholds);
        assertThrows(BusinessException.class, () -> shareActService.updateByActId(3L, 1L, shareActVo, userDto));
    }


}
