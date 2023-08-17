//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.bo.Channel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static cn.edu.xmu.javaee.core.model.Constants.DATE_TIME_FORMATTER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class ChannelDaoTest {

    @MockBean
    private RedisUtil redisUtil;

    @Autowired
    private ChannelDao channelDao;

    @Test
    public void findById1(){
        Channel channel = new Channel();
        channel.setId(Long.valueOf(501));
        channel.setBeanName("wePayChannel");
        channel.setAppid("1");
        channel.setBeginTime(LocalDateTime.parse("2021-10-01T12:00:00", DATE_TIME_FORMATTER));
        channel.setEndTime(LocalDateTime.parse("2023-10-01T12:00:00", DATE_TIME_FORMATTER));
        channel.setName("微信支付");
        channel.setSpMchid("10000");
        channel.setStatus(Channel.VALID);
        channel.setNotifyUrl("http://111");

        Mockito.when(redisUtil.hasKey(String.format(ChannelDao.KEY, 501))).thenReturn(true);
        Mockito.when(redisUtil.get(String.format(ChannelDao.KEY, 501))).thenReturn(channel);

        Channel retChannel = channelDao.findById(Long.valueOf(501));

        assertThat(retChannel.getName()).isEqualTo(channel.getName());
        assertThat(retChannel.getId()).isEqualTo(Long.valueOf(501));
        assertThat(retChannel.getAppid()).isEqualTo(channel.getAppid());
    }

    @Test
    public void findById2(){
        Mockito.when(redisUtil.hasKey(String.format(ChannelDao.KEY, 501))).thenReturn(false);

        Channel retChannel = channelDao.findById(Long.valueOf(501));

        assertThat(retChannel.getName()).isEqualTo("微信支付");
        assertThat(retChannel.getId()).isEqualTo(Long.valueOf(501));
        assertThat(retChannel.getAppid()).isEqualTo("100001");
    }

    @Test
    public void findById3(){
        Mockito.when(redisUtil.hasKey(String.format(ChannelDao.KEY, 5))).thenReturn(false);
        assertThrows(BusinessException.class, ()-> channelDao.findById(Long.valueOf(5)));
    }

    @Test
    public void retrieveValidChannelsTest(){
        List<Channel> allValidChannels = channelDao.retrieveValid(1, 10).getList();
        Channel channel = allValidChannels.get(0);
        assertThat(channel.getId()).isEqualTo(501L);
        assertThat(channel.getAppid()).isEqualTo("100001");
        assertThat(channel.getName()).isEqualTo("微信支付");
        assertThat(channel.getSpMchid()).isEqualTo("1900007XXX");
        assertThat(channel.getStatus()).isEqualTo(Channel.VALID);
    }

    //修改存在的支付渠道状态
    @Test
    public void updateChannelStatusTest1(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);

        Channel channel = new Channel();
        channel.setId(501L);
        channel.setStatus(Channel.VALID);

        ReturnObject ret = channelDao.saveById(channel, user);
        assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }

    @Test
    public void updateChannelStatusTest2(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);

        Channel channel = new Channel();
        channel.setId(501L);
        channel.setStatus(Channel.INVALID);

        ReturnObject ret = channelDao.saveById(channel, user);
        assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }

    @Test
    public void updateChannelStatusTest3(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);

        Channel channel = new Channel();
        channel.setId(400L);
        channel.setStatus(Channel.INVALID);

        assertThrows(BusinessException.class, () -> channelDao.saveById(channel, user));
    }



}
