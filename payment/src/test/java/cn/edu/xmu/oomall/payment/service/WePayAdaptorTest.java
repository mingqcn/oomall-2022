package cn.edu.xmu.oomall.payment.service;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.DivPayTransDao;
import cn.edu.xmu.oomall.payment.dao.PayTransDao;
import cn.edu.xmu.oomall.payment.dao.ShopChannelDao;
import cn.edu.xmu.oomall.payment.dao.bo.*;
import cn.edu.xmu.oomall.payment.service.channel.WePayAdaptor;
import cn.edu.xmu.oomall.payment.service.channel.dto.*;
import cn.edu.xmu.oomall.payment.service.openfeign.AliPayService;
import cn.edu.xmu.oomall.payment.service.openfeign.WePayService;
import cn.edu.xmu.oomall.payment.service.openfeign.WeParam.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class WePayAdaptorTest {
    @Autowired
    WePayAdaptor wePayChannel;
    @MockBean
    WePayService wePayService;

    @MockBean
    AliPayService aliPayService;
    @Autowired
    ShopChannelDao shopChannelDao;
    @Autowired
    DivPayTransDao divPayTransDao;
    @Autowired
    PayTransDao payTransDao;
    @MockBean
    RedisUtil redisUtil;

    @Test
    public void createPayment1() {
        PayTrans payTrans = new PayTrans();
        payTrans.setId(551L);
        payTrans.setOutNo("551");
        payTrans.setTransNo("12222");
        payTrans.setAmount(100L);
        payTrans.setShopChannelId(501L);
        payTrans.setShopChannelDao(shopChannelDao);

        WePostTransRetObj retObj = new WePostTransRetObj();
        retObj.setPrepayId("1");
        InternalReturnObject<WePostTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.postTransaction(Mockito.any())).thenReturn(returnObject);

        PostPayTransAdaptorDto ret = wePayChannel.createPayment(payTrans);
        /*check param*/
        assertEquals(ret.getPrepayId(), "1");
        // Mockito.verify(wePayService, Mockito.times(1)).postTransaction(Mockito.any());
    }

    @Test
    public void createPayment2() {
        PayTrans payTrans = new PayTrans();
        payTrans.setId(551L);
        //payTrans.setOutNo("551");
        payTrans.setTransNo("12222");
        payTrans.setAmount(100L);

        WePostTransRetObj retObj = new WePostTransRetObj();
        retObj.setPrepayId("1");
        InternalReturnObject<WePostTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.postTransaction(Mockito.any())).thenReturn(returnObject);

        /*check param*/
        assertThrows(AssertionError.class, () -> wePayChannel.createPayment(payTrans));
    }

    @Test
    public void getOrderByOutNo1() {
        PayTrans payTrans = new PayTrans();
        payTrans.setOutNo("1217752501201407033233368018");
        payTrans.setAmount(1L);
        payTrans.setSpOpenid("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
        payTrans.setShopChannelId(501L);
        payTrans.setShopChannelDao(shopChannelDao);

        WeGetTransRetObj retObj = new WeGetTransRetObj();
        retObj.setTransaction_id("1");
        InternalReturnObject<WeGetTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.getOrderByOutNo(Mockito.any())).thenReturn(returnObject);

        GetPayTransAdaptorDto ret = wePayChannel.returnOrderByOutNo(payTrans);
        /*check param*/
        Mockito.verify(wePayService, Mockito.times(1)).getOrderByOutNo(Mockito.any());
    }

    @Test
    public void getOrderByOutNo2() {
        PayTrans payTrans1 = new PayTrans();
        PayTrans payTrans2 = new PayTrans();
        payTrans1.setAmount(1L);
        payTrans1.setSpOpenid("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
        payTrans1.setShopChannelId(501L);
        payTrans1.setShopChannelDao(shopChannelDao);

        payTrans2.setOutNo("1217752501201407033233368018");
        payTrans2.setAmount(1L);
        payTrans2.setSpOpenid("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");

        WeGetTransRetObj retObj = new WeGetTransRetObj();
        InternalReturnObject<WeGetTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.getOrderByOutNo(Mockito.any())).thenReturn(returnObject);

        /*check param*/
        assertThrows(AssertionError.class, () -> wePayChannel.returnOrderByOutNo(payTrans1));
        assertThrows(AssertionError.class, () -> wePayChannel.returnOrderByOutNo(payTrans2));
    }

    @Test
    public void getOrderByTransId1() {
        PayTrans payTrans = new PayTrans();
        payTrans.setShopChannelId(501L);
        payTrans.setShopChannelDao(shopChannelDao);
        payTrans.setTransNo("111");
        WeGetTransRetObj retObj = new WeGetTransRetObj();
        retObj.setOut_trade_no("1");
        retObj.setTransaction_id("2");
        WeGetTransRetObj.Payer payer = retObj.new Payer();
        payer.setOpenid("3");
        retObj.setPayer(payer);
        InternalReturnObject<WeGetTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.getOrderByTransId(Mockito.any())).thenReturn(returnObject);

        GetPayTransAdaptorDto ret = wePayChannel.returnOrderByTransId(payTrans);
        /*check param*/
        Mockito.verify(wePayService, Mockito.times(1)).getOrderByTransId(Mockito.any());
    }

    @Test
    public void getOrderByTransId2() {
        PayTrans payTrans1 = new PayTrans();
        payTrans1.setShopChannelId(501L);
        payTrans1.setShopChannelDao(shopChannelDao);
        PayTrans payTrans2 = new PayTrans();
        payTrans2.setTransNo("111");

        WeGetTransRetObj retObj = new WeGetTransRetObj();
        InternalReturnObject<WeGetTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.getOrderByTransId(Mockito.any())).thenReturn(returnObject);

        /*check param*/
        assertThrows(AssertionError.class, () -> wePayChannel.returnOrderByTransId(payTrans1));
        assertThrows(AssertionError.class, () -> wePayChannel.returnOrderByTransId(payTrans2));
    }

    @Test
    public void cancelOrder() {
        PayTrans payTrans = new PayTrans();
        payTrans.setShopChannelId(501L);
        payTrans.setShopChannelDao(shopChannelDao);
        payTrans.setOutNo("111");
        WeCancelOrderRetObj retObj = new WeCancelOrderRetObj();
        InternalReturnObject<WeCancelOrderRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.cancelOrder(Mockito.any())).thenReturn(returnObject);

        CancelOrderAdaptorDto ret = wePayChannel.cancelOrder(payTrans);
        /*check param*/
        Mockito.verify(wePayService, Mockito.times(1)).cancelOrder(Mockito.any());
    }

    @Test
    public void createRefund1() {
        RefundTrans refundTrans1 = new RefundTrans();
        RefundTrans refundTrans2 = new RefundTrans();

        WePostRefundRetObj retObj = new WePostRefundRetObj();
        WePostRefundRetObj.Amount amount = retObj.new Amount();
        /*set param*/
        refundTrans1.setOutNo("123");
        refundTrans1.setAmount(10L);
        refundTrans2.setOutNo("123");
        refundTrans2.setPayTransId(551L);
        refundTrans2.setPayTransDao(payTransDao);
        refundTrans2.getPayTrans().setTransNo(null);
        refundTrans2.getPayTrans().setOutNo(null);
        refundTrans2.setAmount(10L);
        amount.setRefund(10L);
        retObj.setAmount(amount);
        retObj.setUser_received_account("11223344");
        retObj.setSuccess_time(LocalDateTime.of(2022, 10, 30, 10, 30, 59, 3));
        retObj.setStatus("SUCCESS");
        retObj.setTransaction_id("15");
        InternalReturnObject<WePostRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.postRefundTransaction(Mockito.any())).thenReturn(returnObject);

        assertThrows(AssertionError.class, () -> wePayChannel.createRefund(refundTrans1));
        assertThrows(AssertionError.class, () -> wePayChannel.createRefund(refundTrans2));
        /*check param*/
    }

    @Test
    public void createRefund2() {
        RefundTrans refundTrans = new RefundTrans();

        WePostRefundRetObj retObj = new WePostRefundRetObj();
        WePostRefundRetObj.Amount amount = retObj.new Amount();
        /*set param*/
        refundTrans.setPayTransId(551L);
        refundTrans.setPayTransDao(payTransDao);
        refundTrans.setOutNo("123");
        refundTrans.setAmount(10L);
        amount.setRefund(10L);
        retObj.setAmount(amount);
        retObj.setUser_received_account("11223344");
        retObj.setSuccess_time(LocalDateTime.of(2022, 10, 30, 10, 30, 59, 3));
        retObj.setStatus("SUCCESS");
        retObj.setTransaction_id("15");
        InternalReturnObject<WePostRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.postRefundTransaction(Mockito.any())).thenReturn(returnObject);

        PostRefundAdaptorDto ret = wePayChannel.createRefund(refundTrans);
        /*check param*/
    }

    @Test
    public void createRefund3() {
        RefundTrans refundTrans1 = new RefundTrans();
        RefundTrans refundTrans2 = new RefundTrans();

        WePostRefundRetObj retObj = new WePostRefundRetObj();
        WePostRefundRetObj.Amount amount = retObj.new Amount();
        /*set param*/
        refundTrans1.setOutNo("123");
        refundTrans1.setAmount(10L);
        refundTrans2.setOutNo("123");
        refundTrans2.setAmount(10L);
        refundTrans2.setPayTransId(551L);
        refundTrans2.setPayTransDao(payTransDao);
        refundTrans2.getPayTrans().setTransNo(null);
        refundTrans2.getPayTrans().setOutNo(null);
        amount.setRefund(10L);
        retObj.setAmount(amount);
        retObj.setUser_received_account("11223344");
        retObj.setSuccess_time(LocalDateTime.of(2022, 10, 30, 10, 30, 59, 3));
        retObj.setStatus("SUCCESS");
        retObj.setTransaction_id("15");
        InternalReturnObject<WePostRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.postRefundTransaction(Mockito.any())).thenReturn(returnObject);

        /*check param*/
        assertThrows(AssertionError.class, () -> wePayChannel.createRefund(refundTrans1));
        assertThrows(AssertionError.class, () -> wePayChannel.createRefund(refundTrans2));
    }

    @Test
    public void getRefund() {
        RefundTrans refundTrans = new RefundTrans();
        WeGetRefundRetObj retObj = new WeGetRefundRetObj();
        WeGetRefundRetObj.Amount amount = retObj.new Amount();
        /*set param*/
        refundTrans.setPayTransId(551L);
        refundTrans.setPayTransDao(payTransDao);
        refundTrans.setAmount(10L);
        refundTrans.setOutNo("123");
        amount.setRefund(10L);
        retObj.setTransaction_id("111");
        retObj.setRefund_id("222");
        retObj.setUser_received_account("333");
        retObj.setAmount(amount);
        InternalReturnObject<WeGetRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.getRefund(Mockito.any())).thenReturn(returnObject);

        GetRefundAdaptorDto ret = wePayChannel.returnRefund(refundTrans);
        /*check param*/
        Mockito.verify(wePayService, Mockito.times(1)).getRefund(Mockito.any());
    }

    @Test
    public void createDivPay() {
        DivPayTrans divPayTrans = new DivPayTrans();
        WePostDivPayRetObj retObj = new WePostDivPayRetObj();
        /*set param*/
        divPayTrans.setOutNo("1217752501201407033233368018");
        divPayTrans.setPayTransId(551L);
        divPayTrans.setPayTransDao(payTransDao);
        divPayTrans.setOutNo("123");
        divPayTrans.setShopChannelId(501L);
        divPayTrans.setShopChannelDao(shopChannelDao);
        divPayTrans.setAmount(10L);
        InternalReturnObject<WePostDivPayRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.postDivPay(Mockito.any())).thenReturn(returnObject);

        PostDivPayAdaptorDto ret = wePayChannel.createDivPay(divPayTrans);
        /*check param*/
        Mockito.verify(wePayService, Mockito.times(1)).postDivPay(Mockito.any());
    }

    @Test
    public void getDivPay() {
        WeGetDivPayRetObj retObj = new WeGetDivPayRetObj();
        DivPayTrans divPayTrans = new DivPayTrans();

        WeGetDivPayRetObj.Receivers receivers = retObj.new Receivers();
        /*set param*/
        divPayTrans.setOutNo("1217752501201407033233368018");
        divPayTrans.setPayTransId(551L);
        divPayTrans.setPayTransDao(payTransDao);
        divPayTrans.setOutNo("123");
        divPayTrans.setShopChannelId(501L);
        divPayTrans.setShopChannelDao(shopChannelDao);
        divPayTrans.setAmount(10L);
        retObj.setReceivers(receivers);
        InternalReturnObject<WeGetDivPayRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.getDivPay(Mockito.any())).thenReturn(returnObject);

        GetDivPayAdaptorDto ret = wePayChannel.returnDivPay(divPayTrans);
        /*check param*/
        Mockito.verify(wePayService, Mockito.times(1)).getDivPay(Mockito.any());
    }

    @Test
    public void createDivRefund1() {
        DivRefundTrans divRefundTrans = new DivRefundTrans();
        WePostDivRefundRetObj retObj = new WePostDivRefundRetObj();
        /*set param*/
        divRefundTrans.setShopChannelId(501L);
        divRefundTrans.setShopChannelDao(shopChannelDao);
        divRefundTrans.setDivPayTransId(1L);
        divRefundTrans.setDivPayTransDao(divPayTransDao);
        divRefundTrans.setTransNo("111");
        divRefundTrans.setOutNo("222");
        divRefundTrans.setAmount(10L);
        InternalReturnObject<WePostDivRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.postDivRefund(Mockito.any())).thenReturn(returnObject);

        PostDivRefundAdaptorDto ret = wePayChannel.createDivRefund(divRefundTrans);
        /*check param*/
        Mockito.verify(wePayService, Mockito.times(1)).postDivRefund(Mockito.any());
    }

    @Test
    public void createDivRefund2() {
        DivRefundTrans divRefundTrans1 = new DivRefundTrans();
        DivRefundTrans divRefundTrans2 = new DivRefundTrans();
        WePostDivRefundRetObj retObj = new WePostDivRefundRetObj();
        /*set param*/
        divRefundTrans1.setShopChannelId(501L);
        divRefundTrans1.setShopChannelDao(shopChannelDao);
        divRefundTrans1.setTransNo("111");
        divRefundTrans1.setOutNo("222");
        divRefundTrans1.setAmount(10L);
        divRefundTrans2.setShopChannelId(501L);
        divRefundTrans2.setShopChannelDao(shopChannelDao);
        divRefundTrans2.setDivPayTransId(1L);
        divRefundTrans2.setDivPayTransDao(divPayTransDao);
        divRefundTrans2.getDivPayTrans().setTransNo(null);
        divRefundTrans2.getDivPayTrans().setOutNo(null);
        divRefundTrans2.setTransNo("111");
        divRefundTrans2.setOutNo("222");
        divRefundTrans2.setAmount(10L);
        InternalReturnObject<WePostDivRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.postDivRefund(Mockito.any())).thenReturn(returnObject);

        /*check param*/
        assertThrows(AssertionError.class, ()-> wePayChannel.createDivRefund(divRefundTrans1));
        assertThrows(AssertionError.class, ()-> wePayChannel.createDivRefund(divRefundTrans2));
    }

    @Test
    public void getDivRefund1() {
        WeGetDivRefundRetObj retObj = new WeGetDivRefundRetObj();
        DivRefundTrans divRefundTrans = new DivRefundTrans();
        /*set param*/
        divRefundTrans.setDivPayTransId(1L);
        divRefundTrans.setDivPayTransDao(divPayTransDao);
        divRefundTrans.setOutNo("11111");

        retObj.setAmount(10L);
        retObj.setResult("SUCCESS");
        retObj.setOut_return_no("11111");
        retObj.setReturn_id("123456");
        retObj.setFinish_time(LocalDateTime.of(2022, 1, 11, 22, 59, 37, 123));
        InternalReturnObject<WeGetDivRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.getDivRefund(Mockito.any())).thenReturn(returnObject);

        GetDivRefundAdaptorDto ret = wePayChannel.returnDivRefund(divRefundTrans);
        /*check param*/
        // Mockito.verify(wePayService,Mockito.times(1)).getDivRefund(Mockito.any());
        assertEquals(ret.getAmount(), 10L);
        assertEquals(ret.getStatus(), DivRefundTrans.SUCCESS);
        assertEquals(ret.getOutNo(), "11111");
        assertEquals(ret.getSuccessTime(), LocalDateTime.of(2022, 1, 11, 22, 59, 37, 123));
        assertEquals(ret.getTransNo(), "123456");
    }

    @Test
    public void getDivRefund2() {
        WeGetDivRefundRetObj retObj = new WeGetDivRefundRetObj();
        DivRefundTrans divRefundTrans = new DivRefundTrans();
        /*set param*/
        divRefundTrans.setOutNo("11111");

        retObj.setAmount(10L);
        retObj.setResult("SUCCESS");
        retObj.setOut_return_no("11111");
        retObj.setReturn_id("123456");
        retObj.setFinish_time(LocalDateTime.of(2022, 1, 11, 22, 59, 37, 123));
        InternalReturnObject<WeGetDivRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.getDivRefund(Mockito.any())).thenReturn(returnObject);

        /*check param*/
        assertThrows(AssertionError.class,()-> wePayChannel.returnDivRefund(divRefundTrans));
    }

    @Test
    public void CheckTrans1() {
        List<PayTrans> trans1 = new ArrayList<>();
        List<PayTrans> trans2 = new ArrayList<>();
        PayTrans payTrans1 = new PayTrans(); // SUCCESS
        PayTrans payTrans2 = new PayTrans(); // id == null
        PayTrans payTrans3 = new PayTrans(); // Amount != 0
        WeGetTransRetObj retObj = new WeGetTransRetObj();
        /*set param*/
        payTrans1.setId(111L);
        payTrans1.setTransNo("111");
        payTrans1.setAmount(10L);
        payTrans1.setShopChannelId(501L);
        payTrans1.setShopChannelDao(shopChannelDao);
        payTrans2.setAmount(10L);
        payTrans2.setTransNo("222");
        payTrans2.setShopChannelId(501L);
        payTrans2.setShopChannelDao(shopChannelDao);
        payTrans3.setId(333L);
        payTrans3.setTransNo("333");
        payTrans3.setAmount(15L);
        payTrans3.setShopChannelId(501L);
        payTrans3.setShopChannelDao(shopChannelDao);
        trans1.add(payTrans1);
        trans1.add(payTrans2);
        trans2.add(payTrans3);
        retObj.getAmount().setTotal(10L);
        InternalReturnObject<WeGetTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.getOrderByTransId(Mockito.any())).thenReturn(returnObject);

        List<CheckResultDto> ret1 = wePayChannel.checkTransaction(trans1);
        List<CheckResultDto> ret2 = wePayChannel.checkTransaction(trans2);
        /*check result*/
        assertEquals(ret1.get(0).getId(), payTrans1.getId());
        assertEquals(ret1.get(0).getCls(), PayTrans.class.getName());
        assertEquals(ret1.get(0).getStatus(), (byte) 2);
        assertNull(ret1.get(1).getId());
        assertEquals(ret1.get(1).getCls(), PayTrans.class.getName());
        assertEquals(ret1.get(1).getStatus(), (byte) 4);
        assertEquals(ret2.get(0).getId(), payTrans3.getId());
        assertEquals(ret2.get(0).getCls(), PayTrans.class.getName());
        assertEquals(ret2.get(0).getStatus(), (byte) 3);
    }

    @Test
    public void CheckTrans2() {
        List<RefundTrans> trans1 = new ArrayList<>();
        List<RefundTrans> trans2 = new ArrayList<>();
        RefundTrans refundTrans1 = new RefundTrans(); // SUCCESS
        RefundTrans refundTrans2 = new RefundTrans(); // id == null
        RefundTrans refundTrans3 = new RefundTrans(); // Amount != 0
        WeGetRefundRetObj retObj = new WeGetRefundRetObj();
        /*set param*/
        refundTrans1.setId(111L);
        refundTrans1.setTransNo("111");
        refundTrans1.setOutNo("1");
        refundTrans1.setAmount(10L);
        refundTrans1.setShopChannelId(501L);
        refundTrans1.setShopChannelDao(shopChannelDao);
        refundTrans2.setAmount(10L);
        refundTrans2.setTransNo("222");
        refundTrans2.setOutNo("2");
        refundTrans2.setShopChannelId(501L);
        refundTrans2.setShopChannelDao(shopChannelDao);
        refundTrans3.setId(333L);
        refundTrans3.setTransNo("333");
        refundTrans3.setOutNo("3");
        refundTrans3.setAmount(15L);
        refundTrans3.setShopChannelId(501L);
        refundTrans3.setShopChannelDao(shopChannelDao);
        trans1.add(refundTrans1);
        trans1.add(refundTrans2);
        trans2.add(refundTrans3);
        retObj.setAmount(retObj.new Amount());
        retObj.getAmount().setRefund(10L);
        InternalReturnObject<WeGetRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.getRefund(Mockito.any())).thenReturn(returnObject);

        List<CheckResultDto> ret1 = wePayChannel.checkTransaction(trans1);
        List<CheckResultDto> ret2 = wePayChannel.checkTransaction(trans2);
        /*check result*/
        assertEquals(ret1.get(0).getId(), refundTrans1.getId());
        assertEquals(ret1.get(0).getCls(), RefundTrans.class.getName());
        assertEquals(ret1.get(0).getStatus(), (byte) 2);
        assertNull(ret1.get(1).getId());
        assertEquals(ret1.get(1).getCls(), RefundTrans.class.getName());
        assertEquals(ret1.get(1).getStatus(), (byte) 4);
        assertEquals(ret2.get(0).getId(), refundTrans3.getId());
        assertEquals(ret2.get(0).getCls(), RefundTrans.class.getName());
        assertEquals(ret2.get(0).getStatus(), (byte) 3);
    }

    @Test
    public void CheckTrans3() {
        List<DivPayTrans> trans1 = new ArrayList<>();
        List<DivPayTrans> trans2 = new ArrayList<>();
        DivPayTrans divPayTrans1 = new DivPayTrans(); // SUCCESS
        DivPayTrans divPayTrans2 = new DivPayTrans(); // id == null
        DivPayTrans divPayTrans3 = new DivPayTrans(); // Amount != 0
        WeGetDivPayRetObj retObj = new WeGetDivPayRetObj();
        /*set param*/
        divPayTrans1.setId(111L);
        divPayTrans1.setTransNo("111");
        divPayTrans1.setOutNo("1");
        divPayTrans1.setAmount(10L);
        divPayTrans1.setShopChannelId(501L);
        divPayTrans1.setShopChannelDao(shopChannelDao);
        divPayTrans1.setPayTransId(551L);
        divPayTrans1.setPayTransDao(payTransDao);
        divPayTrans2.setAmount(10L);
        divPayTrans2.setTransNo("222");
        divPayTrans2.setOutNo("2");
        divPayTrans2.setShopChannelId(501L);
        divPayTrans2.setShopChannelDao(shopChannelDao);
        divPayTrans2.setPayTransId(551L);
        divPayTrans2.setPayTransDao(payTransDao);
        divPayTrans3.setId(333L);
        divPayTrans3.setTransNo("333");
        divPayTrans3.setOutNo("3");
        divPayTrans3.setAmount(15L);
        divPayTrans3.setShopChannelId(501L);
        divPayTrans3.setShopChannelDao(shopChannelDao);
        divPayTrans3.setPayTransId(551L);
        divPayTrans3.setPayTransDao(payTransDao);
        trans1.add(divPayTrans1);
        trans1.add(divPayTrans2);
        trans2.add(divPayTrans3);
        retObj.setReceivers(retObj.new Receivers());
        retObj.getReceivers().setAmount(divPayTrans1.getAmount());
        InternalReturnObject<WeGetDivPayRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.getDivPay(Mockito.any())).thenReturn(returnObject);

        List<CheckResultDto> ret1 = wePayChannel.checkTransaction(trans1);
        List<CheckResultDto> ret2 = wePayChannel.checkTransaction(trans2);
        /*check result*/
        assertEquals(ret1.get(0).getId(), divPayTrans1.getId());
        assertEquals(ret1.get(0).getCls(), DivPayTrans.class.getName());
        assertEquals(ret1.get(0).getStatus(), (byte) 2);
        assertNull(ret1.get(1).getId());
        assertEquals(ret1.get(1).getCls(), DivPayTrans.class.getName());
        assertEquals(ret1.get(1).getStatus(), (byte) 4);
        assertEquals(ret2.get(0).getId(), divPayTrans3.getId());
        assertEquals(ret2.get(0).getCls(), DivPayTrans.class.getName());
        assertEquals(ret2.get(0).getStatus(), (byte) 3);
    }

    @Test
    public void CheckTrans4() {
        List<DivRefundTrans> trans1 = new ArrayList<>();
        List<DivRefundTrans> trans2 = new ArrayList<>();
        DivRefundTrans divRefundTrans1 = new DivRefundTrans(); // SUCCESS
        DivRefundTrans divRefundTrans2 = new DivRefundTrans(); // id == null
        DivRefundTrans divRefundTrans3 = new DivRefundTrans(); // Amount != 0
        WeGetDivRefundRetObj retObj = new WeGetDivRefundRetObj();
        /*set param*/
        divRefundTrans1.setId(111L);
        divRefundTrans1.setTransNo("111");
        divRefundTrans1.setOutNo("1");
        divRefundTrans1.setAmount(10L);
        divRefundTrans1.setShopChannelId(501L);
        divRefundTrans1.setShopChannelDao(shopChannelDao);
        divRefundTrans1.setDivPayTransId(1L);
        divRefundTrans1.setDivPayTransDao(divPayTransDao);
        divRefundTrans2.setAmount(10L);
        divRefundTrans2.setTransNo("222");
        divRefundTrans2.setOutNo("2");
        divRefundTrans2.setShopChannelId(501L);
        divRefundTrans2.setShopChannelDao(shopChannelDao);
        divRefundTrans2.setDivPayTransId(1L);
        divRefundTrans2.setDivPayTransDao(divPayTransDao);
        divRefundTrans3.setId(333L);
        divRefundTrans3.setTransNo("333");
        divRefundTrans3.setOutNo("3");
        divRefundTrans3.setAmount(15L);
        divRefundTrans3.setShopChannelId(501L);
        divRefundTrans3.setShopChannelDao(shopChannelDao);
        divRefundTrans3.setDivPayTransId(1L);
        divRefundTrans3.setDivPayTransDao(divPayTransDao);
        trans1.add(divRefundTrans1);
        trans1.add(divRefundTrans2);
        trans2.add(divRefundTrans3);
        retObj.setAmount(10L);
        retObj.setResult("SUCCESS");
        InternalReturnObject<WeGetDivRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.getDivRefund(Mockito.any())).thenReturn(returnObject);

        List<CheckResultDto> ret1 = wePayChannel.checkTransaction(trans1);
        List<CheckResultDto> ret2 = wePayChannel.checkTransaction(trans2);
        /*check result*/
        assertEquals(ret1.get(0).getId(), divRefundTrans1.getId());
        assertEquals(ret1.get(0).getCls(), DivRefundTrans.class.getName());
        assertEquals(ret1.get(0).getStatus(), (byte) 2);
        assertNull(ret1.get(1).getId());
        assertEquals(ret1.get(1).getCls(), DivRefundTrans.class.getName());
        assertEquals(ret1.get(1).getStatus(), (byte) 4);
        assertEquals(ret2.get(0).getId(), divRefundTrans3.getId());
        assertEquals(ret2.get(0).getCls(), DivRefundTrans.class.getName());
        assertEquals(ret2.get(0).getStatus(), (byte) 3);
    }
}
