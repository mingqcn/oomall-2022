package cn.edu.xmu.oomall.payment.service;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.DivPayTransDao;
import cn.edu.xmu.oomall.payment.dao.PayTransDao;
import cn.edu.xmu.oomall.payment.dao.RefundTransDao;
import cn.edu.xmu.oomall.payment.dao.ShopChannelDao;
import cn.edu.xmu.oomall.payment.dao.bo.*;
import cn.edu.xmu.oomall.payment.service.channel.AliPayAdaptor;
import cn.edu.xmu.oomall.payment.service.channel.WePayAdaptor;
import cn.edu.xmu.oomall.payment.service.channel.dto.*;
import cn.edu.xmu.oomall.payment.service.openfeign.AliPayService;
import cn.edu.xmu.oomall.payment.service.openfeign.AliParam.*;
import cn.edu.xmu.oomall.payment.service.openfeign.WeParam.*;
import cn.edu.xmu.oomall.payment.service.openfeign.WePayService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class AliPayAdaptorTest {
    @Autowired
    AliPayAdaptor aliPayAdaptor;
    @MockBean
    AliPayService aliPayService;
    @MockBean
    WePayService wePayService;
    @Autowired
    ShopChannelDao shopChannelDao;
    @Autowired
    DivPayTransDao divPayTransDao;
    @Autowired
    PayTransDao payTransDao;
    @Autowired
    RefundTransDao refundTransDao;
    @MockBean
    RedisUtil redisUtil;

    @Test
    public void createPayment() {
        PayTrans payTrans = new PayTrans();
        payTrans.setId(551L);
        payTrans.setOutNo("551");
        payTrans.setTransNo("12222");
        payTrans.setAmount(100L);
        payTrans.setShopChannelId(501L);
        payTrans.setShopChannelDao(shopChannelDao);

        AliPostTransRetObj retObj = new AliPostTransRetObj();
        InternalReturnObject<AliPostTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.postTransaction(Mockito.any())).thenReturn(returnObject);

        PostPayTransAdaptorDto ret = aliPayAdaptor.createPayment(payTrans);
        /*check param*/
        assertNull(ret.getPrepayId());
        // Mockito.verify(aliPayService, Mockito.times(1)).postTransaction(Mockito.any());
    }

    @Test
    public void getOrderByOutNo1() {
        PayTrans payTrans = new PayTrans();
        payTrans.setOutNo("1217752501201407033233368018");
        payTrans.setAmount(1L);
        payTrans.setSpOpenid("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
        payTrans.setShopChannelId(501L);
        payTrans.setShopChannelDao(shopChannelDao);

        AliGetTransRetObj retObj = new AliGetTransRetObj();
        retObj.setTrade_no("1");
        InternalReturnObject<AliGetTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.getOrderByOutNo(Mockito.any())).thenReturn(returnObject);

        GetPayTransAdaptorDto ret = aliPayAdaptor.returnOrderByOutNo(payTrans);
        /*check param*/
        Mockito.verify(aliPayService, Mockito.times(1)).getOrderByOutNo(Mockito.any());
    }

    @Test
    public void getOrderByOutNo2() {
        PayTrans payTrans = new PayTrans();
        payTrans.setAmount(1L);
        payTrans.setSpOpenid("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
        payTrans.setShopChannelId(501L);
        payTrans.setShopChannelDao(shopChannelDao);

        AliGetTransRetObj retObj = new AliGetTransRetObj();
        retObj.setTrade_no("1");
        InternalReturnObject<AliGetTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.getOrderByOutNo(Mockito.any())).thenReturn(returnObject);

        /*check param*/
        assertThrows(AssertionError.class, () -> aliPayAdaptor.returnOrderByOutNo(payTrans));
    }

    @Test
    public void getOrderByTransId() {
        PayTrans payTrans = new PayTrans();
        payTrans.setShopChannelId(501L);
        payTrans.setShopChannelDao(shopChannelDao);
        payTrans.setTransNo("111");
        payTrans.setAmount(1000L);
        AliGetTransRetObj retObj = new AliGetTransRetObj();
        retObj.setOut_trade_no("1");
        retObj.setTrade_no("2");
        retObj.setTotal_amount((double) (payTrans.getAmount() / 100));
        InternalReturnObject<AliGetTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.getOrderByTransId(Mockito.any())).thenReturn(returnObject);

        GetPayTransAdaptorDto ret = aliPayAdaptor.returnOrderByTransId(payTrans);
        /*check param*/
        Mockito.verify(aliPayService, Mockito.times(1)).getOrderByTransId(Mockito.any());
    }

    @Test
    public void cancelOrder1() {
        PayTrans payTrans = new PayTrans();
        payTrans.setShopChannelId(501L);
        payTrans.setShopChannelDao(shopChannelDao);
        payTrans.setOutNo("111");
        AliCancelOrderRetObj retObj = new AliCancelOrderRetObj();
        InternalReturnObject<AliCancelOrderRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.cancelOrder(Mockito.any())).thenReturn(returnObject);

        CancelOrderAdaptorDto ret = aliPayAdaptor.cancelOrder(payTrans);
        /*check param*/
        Mockito.verify(aliPayService, Mockito.times(1)).cancelOrder(Mockito.any());
    }

    @Test
    public void cancelOrder2() {
        PayTrans payTrans = new PayTrans();
        payTrans.setShopChannelId(501L);
        payTrans.setShopChannelDao(shopChannelDao);
        AliCancelOrderRetObj retObj = new AliCancelOrderRetObj();
        InternalReturnObject<AliCancelOrderRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.cancelOrder(Mockito.any())).thenReturn(returnObject);

        /*check param*/
        assertThrows(AssertionError.class, () -> aliPayAdaptor.cancelOrder(payTrans));
    }

    @Test
    public void createRefund1() {
        RefundTrans refundTrans = new RefundTrans();

        AliPostRefundRetObj retObj = new AliPostRefundRetObj();
        /*set param*/
        refundTrans.setTransNo("111");
        refundTrans.setPayTransId(551L);
        refundTrans.setPayTransDao(payTransDao);
        refundTrans.setShopChannelId(501L);
        refundTrans.setShopChannelDao(shopChannelDao);
        refundTrans.setOutNo("123");
        refundTrans.setAmount(1000L);
        retObj.setBuyer_user_id("11223344");
        retObj.setTrade_no(refundTrans.getTransNo());
        retObj.setRefund_fee((double) (refundTrans.getAmount() / 100));
        InternalReturnObject<AliPostRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.postRefundTransaction(Mockito.any())).thenReturn(returnObject);

        PostRefundAdaptorDto ret = aliPayAdaptor.createRefund(refundTrans);
        /*check param*/
        assertEquals(ret.getAmount(), refundTrans.getAmount());
        assertEquals(ret.getTransNo(), refundTrans.getTransNo());
        assertEquals(ret.getUserReceivedAccount(), "11223344");
        assertNull(ret.getStatus());
        // Mockito.verify(aliPayService,Mockito.times(1)).postRefundTransaction(Mockito.any());
    }

    @Test
    public void createRefund2() {
        RefundTrans refundTrans1 = new RefundTrans();
        RefundTrans refundTrans2 = new RefundTrans();

        AliPostRefundRetObj retObj = new AliPostRefundRetObj();
        /*set param*/
        refundTrans1.setTransNo("111");
        refundTrans1.setShopChannelId(501L);
        refundTrans1.setShopChannelDao(shopChannelDao);
        refundTrans1.setOutNo("123");
        refundTrans1.setAmount(1000L);
        refundTrans2.setTransNo("111");
        refundTrans2.setPayTransId(551L);
        refundTrans2.setPayTransDao(payTransDao);
        refundTrans2.getPayTrans().setTransNo(null);
        refundTrans2.getPayTrans().setOutNo(null);
        refundTrans2.setShopChannelId(501L);
        refundTrans2.setShopChannelDao(shopChannelDao);
        refundTrans2.setOutNo("123");
        refundTrans2.setAmount(1000L);
        InternalReturnObject<AliPostRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.postRefundTransaction(Mockito.any())).thenReturn(returnObject);

        /*check param*/
        assertThrows(AssertionError.class, () -> aliPayAdaptor.createRefund(refundTrans1));
        assertThrows(AssertionError.class, () -> aliPayAdaptor.createRefund(refundTrans2));
    }

    @Test
    public void getRefund() {
        RefundTrans refundTrans = new RefundTrans();
        AliGetRefundRetObj retObj = new AliGetRefundRetObj();
        /*set param*/
        refundTrans.setPayTransId(551L);
        refundTrans.setPayTransDao(payTransDao);
        refundTrans.setShopChannelId(501L);
        refundTrans.setShopChannelDao(shopChannelDao);
        refundTrans.setAmount(10L);
        refundTrans.setOutNo("123");
        refundTrans.setTransNo("111");
        retObj.setRefund_amount((double) (refundTrans.getAmount() / 100));
        InternalReturnObject<AliGetRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.getRefund(Mockito.any())).thenReturn(returnObject);

        GetRefundAdaptorDto ret = aliPayAdaptor.returnRefund(refundTrans);
        /*check param*/
        Mockito.verify(aliPayService, Mockito.times(1)).getRefund(Mockito.any());
    }

    @Test
    public void createDivPay() {
        DivPayTrans divPayTrans = new DivPayTrans();
        AliPostDivPayRetObj retObj = new AliPostDivPayRetObj();
        /*set param*/
        divPayTrans.setOutNo("1217752501201407033233368018");
        divPayTrans.setPayTransId(551L);
        divPayTrans.setPayTransDao(payTransDao);
        divPayTrans.setOutNo("123");
        divPayTrans.setShopChannelId(501L);
        divPayTrans.setShopChannelDao(shopChannelDao);
        divPayTrans.setAmount(10L);
        InternalReturnObject<AliPostDivPayRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.postDivPay(Mockito.any())).thenReturn(returnObject);

        PostDivPayAdaptorDto ret = aliPayAdaptor.createDivPay(divPayTrans);
        /*check param*/
        Mockito.verify(aliPayService, Mockito.times(1)).postDivPay(Mockito.any());
    }

    @Test
    public void getDivPay1() {
        AliGetDivPayRetObj retObj = new AliGetDivPayRetObj();
        DivPayTrans divPayTrans = new DivPayTrans();
        List<AliGetDivPayRetObj.RoyaltyDetail> details = new ArrayList<>();
        AliGetDivPayRetObj.RoyaltyDetail detail = retObj.new RoyaltyDetail();
        /*set param*/
        divPayTrans.setOutNo("1217752501201407033233368018");
        divPayTrans.setPayTransId(551L);
        divPayTrans.setPayTransDao(payTransDao);
        divPayTrans.setOutNo("123");
        divPayTrans.setShopChannelId(501L);
        divPayTrans.setShopChannelDao(shopChannelDao);
        divPayTrans.setAmount(10L);
        detail.setAmount((divPayTrans.getAmount() / 100.0));
        details.add(detail);
        retObj.setRoyalty_detail_list(details);
        InternalReturnObject<AliGetDivPayRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.getDivPay(Mockito.any())).thenReturn(returnObject);

        GetDivPayAdaptorDto ret = aliPayAdaptor.returnDivPay(divPayTrans);
        /*check param*/
        Mockito.verify(aliPayService, Mockito.times(1)).getDivPay(Mockito.any());
    }

    @Test
    public void getDivPay2() {
        AliGetDivPayRetObj retObj = new AliGetDivPayRetObj();
        DivPayTrans divPayTrans = new DivPayTrans();
        List<AliGetDivPayRetObj.RoyaltyDetail> details = new ArrayList<>();
        AliGetDivPayRetObj.RoyaltyDetail detail = retObj.new RoyaltyDetail();
        /*set param*/
        divPayTrans.setOutNo("1217752501201407033233368018");
        divPayTrans.setPayTransId(551L);
        divPayTrans.setPayTransDao(payTransDao);
        divPayTrans.getTrans().setTransNo(null);
        divPayTrans.setOutNo("123");
        divPayTrans.setShopChannelId(501L);
        divPayTrans.setShopChannelDao(shopChannelDao);
        divPayTrans.setAmount(10L);
        InternalReturnObject<AliGetDivPayRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.getDivPay(Mockito.any())).thenReturn(returnObject);

        /*check param*/
        assertThrows(AssertionError.class, ()->aliPayAdaptor.returnDivPay(divPayTrans));
    }

    @Test
    public void createDivRefund1() {
        DivRefundTrans divRefundTrans = new DivRefundTrans();
        AliPostRefundRetObj retObj = new AliPostRefundRetObj();
        /*set param*/
        divRefundTrans.setDivPayTransId(1L);
        divRefundTrans.setDivPayTransDao(divPayTransDao);
        divRefundTrans.setRefundTransId(501L);
        divRefundTrans.setRefundTransDao(refundTransDao);
        divRefundTrans.setShopChannelId(501L);
        divRefundTrans.setShopChannelDao(shopChannelDao);
        divRefundTrans.setAmount(1000L);
        InternalReturnObject<AliPostRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.postRefundTransaction(Mockito.any())).thenReturn(returnObject);

        PostDivRefundAdaptorDto ret = aliPayAdaptor.createDivRefund(divRefundTrans);
        /*check param*/
        Mockito.verify(aliPayService, Mockito.times(1)).postRefundTransaction(Mockito.any());
    }

    @Test
    public void createDivRefund2() {
        DivRefundTrans divRefundTrans1 = new DivRefundTrans();
        DivRefundTrans divRefundTrans2 = new DivRefundTrans();
        AliPostRefundRetObj retObj = new AliPostRefundRetObj();
        /*set param*/
        divRefundTrans1.setDivPayTransId(1L);
        divRefundTrans1.setDivPayTransDao(divPayTransDao);
        divRefundTrans1.setRefundTransId(501L);
        divRefundTrans1.setRefundTransDao(refundTransDao);
        divRefundTrans1.getRefundTrans().setPayTransId(null);
        divRefundTrans1.setShopChannelId(501L);
        divRefundTrans1.setShopChannelDao(shopChannelDao);
        divRefundTrans1.setAmount(1000L);
        divRefundTrans2.setDivPayTransId(1L);
        divRefundTrans2.setDivPayTransDao(divPayTransDao);
        divRefundTrans2.setShopChannelId(501L);
        divRefundTrans2.setShopChannelDao(shopChannelDao);
        divRefundTrans2.setAmount(1000L);
        InternalReturnObject<AliPostRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.postRefundTransaction(Mockito.any())).thenReturn(returnObject);

        /*check param*/
        assertThrows(AssertionError.class, ()->aliPayAdaptor.createDivRefund(divRefundTrans1));
        assertThrows(AssertionError.class, ()->aliPayAdaptor.createDivRefund(divRefundTrans2));
    }

    @Test
    public void getDivRefund1() {
        AliGetRefundRetObj retObj = new AliGetRefundRetObj();
        DivRefundTrans divRefundTrans = new DivRefundTrans();
        /*set param*/
        divRefundTrans.setDivPayTransId(1L);
        divRefundTrans.setDivPayTransDao(divPayTransDao);
        divRefundTrans.setRefundTransId(501L);
        divRefundTrans.setRefundTransDao(refundTransDao);
        divRefundTrans.setShopChannelId(501L);
        divRefundTrans.setShopChannelDao(shopChannelDao);
        divRefundTrans.setOutNo("11111");
        divRefundTrans.setAmount(10L);

        retObj.setRefund_amount(divRefundTrans.getAmount() / 100.0);
        retObj.setOut_trade_no(divRefundTrans.getOutNo());
        retObj.setTrade_no(divRefundTrans.getOutNo());
        InternalReturnObject<AliGetRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.getRefund(Mockito.any())).thenReturn(returnObject);

        GetDivRefundAdaptorDto ret = aliPayAdaptor.returnDivRefund(divRefundTrans);
        /*check param*/
        // Mockito.verify(aliPayService,Mockito.times(1)).getDivRefund(Mockito.any());
        assertEquals(ret.getAmount(), divRefundTrans.getAmount());
        assertEquals(ret.getStatus(), RefundTrans.SUCCESS);
        assertEquals(ret.getOutNo(), divRefundTrans.getOutNo());
        assertEquals(ret.getTransNo(), divRefundTrans.getOutNo());
    }

    @Test
    public void getDivRefund2() {
        AliGetRefundRetObj retObj = new AliGetRefundRetObj();
        DivRefundTrans divRefundTrans = new DivRefundTrans();
        /*set param*/
        divRefundTrans.setDivPayTransId(1L);
        divRefundTrans.setDivPayTransDao(divPayTransDao);
        divRefundTrans.setRefundTransId(501L);
        divRefundTrans.setRefundTransDao(refundTransDao);
        divRefundTrans.setShopChannelId(501L);
        divRefundTrans.setShopChannelDao(shopChannelDao);
        divRefundTrans.setOutNo("11111");
        divRefundTrans.setTransNo("222");
        divRefundTrans.setAmount(10L);

        retObj.setRefund_amount(divRefundTrans.getAmount() / 100.0);
        retObj.setOut_request_no(divRefundTrans.getTransNo());
        retObj.setTrade_no(divRefundTrans.getOutNo());
        InternalReturnObject<AliGetRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.getRefund(Mockito.any())).thenReturn(returnObject);

        GetDivRefundAdaptorDto ret = aliPayAdaptor.returnDivRefund(divRefundTrans);
        /*check param*/
        // Mockito.verify(aliPayService,Mockito.times(1)).getDivRefund(Mockito.any());
        assertEquals(ret.getAmount(), divRefundTrans.getAmount());
        assertEquals(ret.getStatus(), RefundTrans.SUCCESS);
        assertEquals(ret.getTransNo(), divRefundTrans.getOutNo());
    }

    @Test
    public void CheckTrans1() {
        List<PayTrans> trans1 = new ArrayList<>();
        List<PayTrans> trans2 = new ArrayList<>();
        PayTrans payTrans1 = new PayTrans(); // SUCCESS
        PayTrans payTrans2 = new PayTrans(); // id == null
        PayTrans payTrans3 = new PayTrans(); // Amount != 0
        AliGetTransRetObj retObj = new AliGetTransRetObj();
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
        retObj.setTotal_amount(0.1);
        InternalReturnObject<AliGetTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.getOrderByTransId(Mockito.any())).thenReturn(returnObject);

        List<CheckResultDto> ret1 = aliPayAdaptor.checkTransaction(trans1);
        List<CheckResultDto> ret2 = aliPayAdaptor.checkTransaction(trans2);
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
        AliGetRefundRetObj retObj = new AliGetRefundRetObj();
        /*set param*/
        refundTrans1.setId(111L);
        refundTrans1.setTransNo("111");
        refundTrans1.setAmount(10L);
        refundTrans1.setShopChannelId(501L);
        refundTrans1.setShopChannelDao(shopChannelDao);
        refundTrans1.setPayTransId(551L);
        refundTrans1.setPayTransDao(payTransDao);
        refundTrans2.setAmount(10L);
        refundTrans2.setTransNo("222");
        refundTrans2.setShopChannelId(501L);
        refundTrans2.setShopChannelDao(shopChannelDao);
        refundTrans2.setPayTransId(551L);
        refundTrans2.setPayTransDao(payTransDao);
        refundTrans3.setId(333L);
        refundTrans3.setTransNo("333");
        refundTrans3.setAmount(15L);
        refundTrans3.setShopChannelId(501L);
        refundTrans3.setShopChannelDao(shopChannelDao);
        refundTrans3.setPayTransId(551L);
        refundTrans3.setPayTransDao(payTransDao);
        trans1.add(refundTrans1);
        trans1.add(refundTrans2);
        trans2.add(refundTrans3);
        retObj.setRefund_amount(0.1);
        InternalReturnObject<AliGetRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.getRefund(Mockito.any())).thenReturn(returnObject);

        List<CheckResultDto> ret1 = aliPayAdaptor.checkTransaction(trans1);
        List<CheckResultDto> ret2 = aliPayAdaptor.checkTransaction(trans2);
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
        AliGetDivPayRetObj retObj = new AliGetDivPayRetObj();
        List<AliGetDivPayRetObj.RoyaltyDetail> details = new ArrayList<>();
        AliGetDivPayRetObj.RoyaltyDetail detail = retObj.new RoyaltyDetail();

        /*set param*/
        divPayTrans1.setId(111L);
        divPayTrans1.setTransNo("111");
        divPayTrans1.setAmount(10L);
        divPayTrans1.setShopChannelId(501L);
        divPayTrans1.setShopChannelDao(shopChannelDao);
        divPayTrans1.setPayTransId(551L);
        divPayTrans1.setPayTransDao(payTransDao);
        divPayTrans2.setAmount(10L);
        divPayTrans2.setTransNo("222");
        divPayTrans2.setShopChannelId(501L);
        divPayTrans2.setShopChannelDao(shopChannelDao);
        divPayTrans2.setPayTransId(551L);
        divPayTrans2.setPayTransDao(payTransDao);
        divPayTrans3.setId(333L);
        divPayTrans3.setTransNo("333");
        divPayTrans3.setAmount(15L);
        divPayTrans3.setShopChannelId(501L);
        divPayTrans3.setShopChannelDao(shopChannelDao);
        divPayTrans3.setPayTransId(551L);
        divPayTrans3.setPayTransDao(payTransDao);
        trans1.add(divPayTrans1);
        trans1.add(divPayTrans2);
        trans2.add(divPayTrans3);
        detail.setAmount((divPayTrans1.getAmount() / 100.0));
        details.add(detail);
        retObj.setRoyalty_detail_list(details);
        InternalReturnObject<AliGetDivPayRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.getDivPay(Mockito.any())).thenReturn(returnObject);

        List<CheckResultDto> ret1 = aliPayAdaptor.checkTransaction(trans1);
        List<CheckResultDto> ret2 = aliPayAdaptor.checkTransaction(trans2);
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
        AliGetRefundRetObj retObj = new AliGetRefundRetObj();
        /*set param*/
        divRefundTrans1.setId(111L);
        divRefundTrans1.setTransNo("111");
        divRefundTrans1.setAmount(10L);
        divRefundTrans1.setShopChannelId(501L);
        divRefundTrans1.setShopChannelDao(shopChannelDao);
        divRefundTrans1.setRefundTransId(501L);
        divRefundTrans1.setRefundTransDao(refundTransDao);
        divRefundTrans2.setAmount(10L);
        divRefundTrans2.setTransNo("222");
        divRefundTrans2.setShopChannelId(501L);
        divRefundTrans2.setShopChannelDao(shopChannelDao);
        divRefundTrans2.setRefundTransId(501L);
        divRefundTrans2.setRefundTransDao(refundTransDao);
        divRefundTrans3.setId(333L);
        divRefundTrans3.setTransNo("333");
        divRefundTrans3.setAmount(15L);
        divRefundTrans3.setShopChannelId(501L);
        divRefundTrans3.setShopChannelDao(shopChannelDao);
        divRefundTrans3.setRefundTransId(501L);
        divRefundTrans3.setRefundTransDao(refundTransDao);
        trans1.add(divRefundTrans1);
        trans1.add(divRefundTrans2);
        trans2.add(divRefundTrans3);
        retObj.setRefund_amount(0.1);
        InternalReturnObject<AliGetRefundRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(aliPayService.getRefund(Mockito.any())).thenReturn(returnObject);

        List<CheckResultDto> ret1 = aliPayAdaptor.checkTransaction(trans1);
        List<CheckResultDto> ret2 = aliPayAdaptor.checkTransaction(trans2);
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
