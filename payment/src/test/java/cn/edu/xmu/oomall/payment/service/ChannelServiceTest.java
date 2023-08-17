package cn.edu.xmu.oomall.payment.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.bo.Channel;
import cn.edu.xmu.oomall.payment.service.dto.SimpleChannelDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author 黄坤鹏
 * @date 2022/11/15 11:35
 */
@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class ChannelServiceTest {
    @MockBean
    private RedisUtil redisUtil;
    @Autowired
    private ChannelService channelService;

    @Test
    public void retrieveChannelTest1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        SimpleChannelDto dto = channelService.retrieveValidChannel(0L, 1, 10).getList().get(0);
        assertThat(dto.getId()).isEqualTo(501L);
        assertThat(dto.getStatus()).isEqualTo(Channel.VALID);
        assertThat(dto.getName()).isEqualTo("微信支付");
        assertThat(dto.getBeginTime()).isEqualTo(LocalDateTime.parse("2022-05-02 18:49:48", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        assertThat(dto.getEndTime()).isEqualTo(LocalDateTime.parse("2099-11-02 18:49:56", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Test
    public void retrieveChannelTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        SimpleChannelDto dto = channelService.retrieveValidChannel(1L, 1, 10).getList().get(0);
        assertThat(dto.getId()).isEqualTo(501L);
        assertThat(dto.getStatus()).isEqualTo(Channel.VALID);
        assertThat(dto.getName()).isEqualTo("微信支付");
        assertThat(dto.getBeginTime()).isEqualTo(LocalDateTime.parse("2022-05-02 18:49:48", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        assertThat(dto.getEndTime()).isEqualTo(LocalDateTime.parse("2099-11-02 18:49:56", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Test
    public void retrieveChannelTest3(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        List<SimpleChannelDto> list = channelService.retrieveValidChannel(7L, 1, 10).getList();
        assertThat(list.size()).isEqualTo(1);

    }

    //更改的渠道不存在
    @Test
    public void updateChannelStatusTest1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);

        assertThrows(BusinessException.class, () -> channelService.updateChannelStatus(500L, Channel.VALID, user));

    }
    //修改渠道状态有效
    @Test
    public void updateChannelStatusTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);

        ReturnObject ret = channelService.updateChannelStatus(501L, Channel.VALID, user);
        assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }
    //修改渠道状态无效
    @Test
    public void updateChannelStatusTest3(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);

        ReturnObject ret = channelService.updateChannelStatus(501L, Channel.INVALID, user);
        assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }

}
