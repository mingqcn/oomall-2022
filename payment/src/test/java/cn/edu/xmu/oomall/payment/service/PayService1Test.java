//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.service.dto.PayTransDto;
import cn.edu.xmu.oomall.payment.service.dto.SimpleRefundDto;
import cn.edu.xmu.oomall.payment.dao.bo.ShopChannel;
import cn.edu.xmu.oomall.payment.service.dto.*;
import cn.edu.xmu.oomall.payment.service.openfeign.WePayService;
import cn.edu.xmu.oomall.payment.service.openfeign.WeParam.WePostTransRetObj;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static cn.edu.xmu.javaee.core.model.Constants.DATE_TIME_FORMATTER;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class PayService1Test {
    @MockBean
    private RedisUtil redisUtil;
    @MockBean
    private WePayService wePayService;
    @Autowired
    private PaymentService paymentService;


    @Test
    public void createPayment() {
        WePostTransRetObj retObj = new WePostTransRetObj();
        retObj.setPrepayId("2311111");
        InternalReturnObject<WePostTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.postTransaction(Mockito.any())).thenReturn(returnObject);

        LocalDateTime begin = LocalDateTime.parse("2022-01-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse("2032-01-01T12:12:12", DATE_TIME_FORMATTER);

        PayTransDto obj = paymentService.createPayment(begin, end,  "11111",  501L, 100L, 10L, user);
        assertEquals("2311111", obj.getPrepayId());

    }

    @Test
    public void retrieveRefunds() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        PageDto<SimpleRefundDto> refunds = paymentService.retrieveRefunds(1L, 551L, 1, 10);
        assertEquals(1, refunds.getList().size());
    }

    @Test
    public void retrieveShopChannelTest1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        ShopChannelDto ret = paymentService.retrieveShopChannel(1L, 1, 10).getList().get(0);
        assertThat(ret.getId()).isEqualTo(501L);
        assertThat(ret.getStatus()).isEqualTo(ShopChannel.VALID);
        assertThat(ret.getSubMchid()).isEqualTo("1900008XXX");
    }
    @Test
    public void retrieveShopChannelTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        PageDto<ShopChannelDto> ret = paymentService.retrieveShopChannel(0L, 1, 10);
        assertThat(ret.getList().size()).isEqualTo(0);
    }

    @Test
    public void createShopChannelTest(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);

        assertThrows(BusinessException.class, () -> paymentService.createShopChannel(1L, 501L, "1900009XXX", user));
    }

    @Test
    public void findShopChannelTest1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        FullShopChannelDto ret = paymentService.findShopChannel(1L, 501L);
        assertThat(ret.getId()).isEqualTo(501L);
        assertThat(ret.getStatus()).isEqualTo(ShopChannel.VALID);
        assertThat(ret.getSubMchid()).isEqualTo("1900008XXX");
        assertThat(ret.getChannel().getName()).isEqualTo("微信支付");
        assertThat(ret.getChannel().getId()).isEqualTo(501L);
        assertThat(ret.getChannel().getEndTime()).isEqualTo(LocalDateTime.parse("2099-11-02 18:49:56", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        assertThat(ret.getCreator().getName()).isEqualTo("admin111");
        assertThat(ret.getGmtCreate()).isEqualTo(LocalDateTime.parse("2022-11-02 10:53:41", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    //查找一个不存在的商家支付渠道
    @Test
    public void findShopChannelTest2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        assertThrows(BusinessException.class, () -> paymentService.findShopChannel(1L, 500L));
    }

    //不允许删除有效的支付渠道
    @Test
    public void delShopChannelTest1(){
        assertThrows(BusinessException.class, () -> paymentService.delShopChannel(1L, 501L));
    }

    //删除一个不存在的支付渠道
    @Test
    public void delShopChannelTest2(){
        assertThrows(BusinessException.class, () -> paymentService.delShopChannel(1L, 503L));
    }

    @Test
    public void updateShopChannelSubMchIdTest(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);

        assertThrows(BusinessException.class, () -> paymentService.updateShopChannelSubMchId(1L, 501L, "1900001XXX", user));
    }

    @Test
    public void updateShopChannelValidTest(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);

        ReturnObject ret = paymentService.updateShopChannelStatus(1L, 501L, ShopChannel.VALID, user);
        assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }

    @Test
    public void updateShopChannelInvalidTest(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);

        ReturnObject ret = paymentService.updateShopChannelStatus(1L, 501L, ShopChannel.INVALID, user);
        assertThat(ret.getCode()).isEqualTo(ReturnNo.OK);
    }

}
