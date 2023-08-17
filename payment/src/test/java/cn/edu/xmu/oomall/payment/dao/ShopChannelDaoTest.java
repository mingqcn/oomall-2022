package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.bo.Channel;
import cn.edu.xmu.oomall.payment.dao.bo.ShopChannel;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * @author 黄坤鹏
 * @date 2022/11/9 20:32
 */
@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class ShopChannelDaoTest {
    final String KEY ="SC%d";
    @MockBean
    private RedisUtil redisUtil;

    @Autowired
    private ShopChannelDao shopChannelDao;

    @Test
    public void findObjByIdTest1(){
        ShopChannel shopChannel = new ShopChannel();
        shopChannel.setId(Long.valueOf(501));
        shopChannel.setShopId(Long.valueOf(1));
        shopChannel.setSubMchid("1900008XXX");
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(Long.valueOf(501));

        Mockito.when(redisUtil.hasKey(String.format(KEY, 501))).thenReturn(true);
        Mockito.when(redisUtil.get(String.format(KEY, 501))).thenReturn(shopChannel);

        ShopChannel ret = shopChannelDao.findById(Long.valueOf(501));

        assertThat(ret.getId()).isEqualTo(shopChannel.getId());
        assertThat(ret.getShopId()).isEqualTo(shopChannel.getShopId());
        assertThat(ret.getChannelId()).isEqualTo(shopChannel.getChannelId());
        assertThat(ret.getSubMchid()).isEqualTo(shopChannel.getSubMchid());
        assertThat(ret.getStatus()).isEqualTo(shopChannel.getStatus());
    }

    @Test
    public void findObjByIdTest2(){
        Mockito.when(redisUtil.hasKey(String.format(KEY, 501))).thenReturn(false);

        ShopChannel ret = shopChannelDao.findById(Long.valueOf(501));

        assertThat(ret.getId()).isEqualTo(501);
        assertThat(ret.getShopId()).isEqualTo(1);
        assertThat(ret.getChannelId()).isEqualTo(501);
        assertThat(ret.getSubMchid()).isEqualTo("1900008XXX");
        assertThat(ret.getStatus()).isEqualTo(ShopChannel.VALID);
    }

    @Test
    public void updateObjByIdTest(){
        ShopChannel shopChannel = new ShopChannel();
        shopChannel.setId(501L);
        shopChannel.setSubMchid("1900010XXX");
        shopChannel.setChannelId(501L);

        shopChannelDao.saveById(shopChannel);
    }

    @Test
    public void retrieveShopChannelsByIdTest(){
        ShopChannel ret = shopChannelDao.retrieveByShopId(1L, 1, 10, true).getList().get(0);
        assertThat(ret.getId()).isEqualTo(501);
        assertThat(ret.getShopId()).isEqualTo(1);
        assertThat(ret.getChannelId()).isEqualTo(501);
        assertThat(ret.getSubMchid()).isEqualTo("1900008XXX");
        assertThat(ret.getStatus()).isEqualTo(ShopChannel.VALID);
    }

    @Test
    public void insertShopChannelTest(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);

        ShopChannel shopChannel = new ShopChannel(1L, 501L, "1900010XXX");

        ShopChannel ret = shopChannelDao.save(shopChannel, user);

        assertThat(ret.getShopId()).isEqualTo(1L);
        assertThat(ret.getChannelId()).isEqualTo(501L);
        assertThat(ret.getStatus()).isEqualTo(ShopChannel.INVALID);
        assertThat(ret.getSubMchid()).isEqualTo("1900010XXX");
    }

    //查询存在的shopChannel
    @Test
    public void findByShopIdChannelIdTest1(){
        Mockito.when(redisUtil.hasKey(String.format(KEY, 501L))).thenReturn(false);
        ShopChannel ret = shopChannelDao.findByShopIdAndChannelId(1L, 501L);
        assertThat(ret.getId()).isEqualTo(501);
        assertThat(ret.getShopId()).isEqualTo(1);
        assertThat(ret.getChannelId()).isEqualTo(501);
        assertThat(ret.getSubMchid()).isEqualTo("1900008XXX");
        assertThat(ret.getStatus()).isEqualTo(ShopChannel.VALID);
    }

    //查询不存在的shopChannel
    @Test
    public void findByShopIdChannelIdTest2() {
        Mockito.when(redisUtil.hasKey(String.format(KEY, 501L))).thenReturn(false);
        assertThrows(BusinessException.class, () -> shopChannelDao.findByShopIdAndChannelId(1L, 500L));
    }
    //删除不存在的支付渠道
    @Test
    public void delShopChannelTest(){
        Mockito.when(redisUtil.hasKey(String.format(KEY, 502))).thenReturn(false);
        ShopChannel shopChannel = new ShopChannel();
        shopChannel.setId(501L);
        ReturnObject ret = shopChannelDao.delById(shopChannel);
        assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }

    @Test
    public void updateShopChannelTest(){
        ShopChannel shopChannel = new ShopChannel();
        shopChannel.setId(501L);
        shopChannel.setShopId(1L);
        shopChannel.setChannelId(501L);
        shopChannel.setSubMchid("1900008XXX");

        Set<String> delKeys = shopChannelDao.saveById(shopChannel);
        assertThat(delKeys.contains(String.format(KEY, shopChannel.getId()))).isEqualTo(true);
    }

    @Test
    public void updateShopChannelStatusTest(){
        ShopChannel shopChannel = new ShopChannel();
        shopChannel.setId(501L);
        shopChannel.setShopId(1L);
        shopChannel.setChannelId(501L);
        shopChannel.setStatus(ShopChannel.INVALID);
        Mockito.when(redisUtil.hasKey(String.format(KEY, shopChannel.getId()))).thenReturn(false);

        Set<String> delKeys = shopChannelDao.saveById(shopChannel);
        assertThat(delKeys.contains(String.format(KEY, shopChannel.getId()))).isEqualTo(true);
    }
}
