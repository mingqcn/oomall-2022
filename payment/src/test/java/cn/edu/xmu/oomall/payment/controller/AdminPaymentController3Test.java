//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.controller;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.bo.Ledger;
import cn.edu.xmu.oomall.payment.dao.bo.ShopChannel;
import cn.edu.xmu.oomall.payment.mapper.generator.*;
import cn.edu.xmu.oomall.payment.mapper.generator.po.*;
import cn.edu.xmu.oomall.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
/**
 * @author Wanru Zhuang
 * @date 2022/11/27
 */
@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AdminPaymentController3Test {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RedisUtil redisUtil;
    @MockBean(name = "cn.edu.xmu.oomall.payment.service.PaymentService")
    private PaymentService paymentService;
    @MockBean
    private RefundTransPoMapper refundTransPoMapper;
    @MockBean
    private PayTransPoMapper payTransPoMapper;
    @MockBean
    private DivRefundTransPoMapper divRefundTransPoMapper;
    @MockBean
    private DivPayTransPoMapper divPayTransPoMapper;
    @MockBean
    private LedgerPoMapper ledgerPoMapper;
    @MockBean
    private ChannelPoMapper channelPoMapper;
    @MockBean
    private ShopChannelPoMapper shopChannelPoMapper;

    private static String adminToken;
    private static final String LEDGER_BY_ID = "/shops/{shopId}/ledgers/{id}";
    JwtHelper jwtHelper = new JwtHelper();
    @BeforeAll
    public static void setup(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    /**
     * 商户获得自己的分账信息（非正常流程,渠道不合法）,支付
     * author:zwr
     * @throws Exception
     */

    @Test
    @Transactional
    public void queryLedger1() throws Exception {
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.INVALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);
        channelPo.setName("微信");

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setTransId(501L);
        ledgerPo.setType(Ledger.PAY_TYPE);

        PayTransPo payTransPo = new PayTransPo();
        payTransPo.setId(501L);
        payTransPo.setShopChannelId(1L);


        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(payTransPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(payTransPo);

        this.mockMvc.perform(MockMvcRequestBuilders.get(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.PAY_CHANNEL_INVALID.getErrNo())));
                //.andDo(MockMvcResultHandlers.print());
    }

    /**
     * 商户获得自己的分账信息（非正常流程,查不到对应交易）,支付
     * author:zwr
     * @throws Exception
     */
    @Test
    @Transactional
    public void queryLedger11() throws Exception {
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setTransId(501L);
        ledgerPo.setType(Ledger.PAY_TYPE);


        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(payTransPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg", is("支付交易对象(id=501)不存在")));
                //.andDo(MockMvcResultHandlers.print());
    }

    /**
     * 商户获得自己的分账信息（正常流程),退款
     * author:zwr
     * @throws Exception
     */
    @Test
    @Transactional
    public void queryLedger2() throws Exception {
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setTransId(501L);
        ledgerPo.setType(Ledger.REFUND_TYPE);

        RefundTransPo refundTransPo = new RefundTransPo();
        refundTransPo.setId(501L);
        refundTransPo.setShopChannelId(1L);


        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(refundTransPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(refundTransPo);

        this.mockMvc.perform(MockMvcRequestBuilders.get(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.trans.id").value("501"));
    }

    /**
     * 商户获得自己的分账信息（非正常流程，查不到对应交易）,退款
     * author:zwr
     * @throws Exception
     */
    @Test
    @Transactional
    public void queryLedger22() throws Exception {
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setTransId(501L);
        ledgerPo.setType(Ledger.REFUND_TYPE);


        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(refundTransPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get(LEDGER_BY_ID, 1, 501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg", is("退款交易对象(id=501)不存在")));
    }
    /**
     * 商户获得自己的分账信息（正常流程),分账支付
     * author:zwr
     * @throws Exception
     */
    @Test
    @Transactional
    public void queryLedger3() throws Exception {
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setTransId(501L);
        ledgerPo.setType(Ledger.DIVPAY_TYPE);

        DivPayTransPo div = new DivPayTransPo();
        div.setId(501L);
        div.setShopChannelId(1L);
        div.setPayTransId(1L);

        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(divPayTransPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(div);

        this.mockMvc.perform(MockMvcRequestBuilders.get(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.trans.id").value("501"))
                .andDo(MockMvcResultHandlers.print());
    }
    /**
     * 商户获得自己的分账信息（非正常流程，查不到对应交易）,分账支付
     * author:zwr
     * @throws Exception
     */
    @Test
    @Transactional
    public void queryLedger33() throws Exception {
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setTransId(501L);
        ledgerPo.setType(Ledger.DIVPAY_TYPE);


        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(divPayTransPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg", is("分账交易对象(id=501)不存在")));
    }

    /**
     * 商户获得自己的分账信息（正常流程),分账退款
     * author:zwr
     * @throws Exception
     */
    @Test
    @Transactional
    public void queryLedger4() throws Exception {
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setTransId(501L);
        ledgerPo.setType(Ledger.DIVREFUND_TYPE);

        DivRefundTransPo refundTransPo = new DivRefundTransPo();
        refundTransPo.setId(501L);
        refundTransPo.setShopChannelId(1L);


        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(divRefundTransPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(refundTransPo);

        this.mockMvc.perform(MockMvcRequestBuilders.get(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.trans.id").value("501"))
                .andDo(MockMvcResultHandlers.print());
    }
    /**
     * 商户获得自己的分账信息（非正常流程，查不到对应交易）,分账退款
     * author:zwr
     * @throws Exception
     */
    @Test
    @Transactional
    public void queryLedger44() throws Exception {
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setTransId(501L);
        ledgerPo.setType(Ledger.DIVREFUND_TYPE);


        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(divRefundTransPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg", is("分账回退对象(id=501)不存在")));
    }
    /**
     * 调账（正常流程。台账的交易是支付交易）
     * author:zwr
     */
    @Test
    @Transactional
    public void modifyLedgeStatus1() throws Exception {
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);

        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setStatus(Ledger.UNSETTLE);

        PayTransPo payTransPo = new PayTransPo();
        payTransPo.setId(501L);

        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(ledgerPoMapper.updateByPrimaryKeySelective(Mockito.any())).thenReturn(1);
        Mockito.when(payTransPoMapper.updateByPrimaryKeySelective(Mockito.any())).thenReturn(1);

        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/ledgers/{id}", 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }
    /**
     * 调账（非正常流程。找不到台账）
     * author:zwr
     */
    @Test
    @Transactional
    public void modifyLedgeStatus11() throws Exception {
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        Mockito.when(ledgerPoMapper.selectByPrimaryKey(Mockito.any())).thenReturn(null);

        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/ledgers/{id}", 1,503)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }
    /**
     * 调账（非正常流程。台账的交易是支付交易，店铺渠道不合法）
     * author:zwr
     */
    @Test
    @Transactional
    public void modifyLedgeStatus111() throws Exception {
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.INVALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);

        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.put(LEDGER_BY_ID, 1,503)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.PAY_CHANNEL_INVALID.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 调账（非正常流程。台账的交易是支付交易，该台账已经被调账处理,状态下不可处理）
     * author:zwr
     */
    @Test
    @Transactional
    public void modifyLedgeStatus1111() throws Exception {
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setStatus(Ledger.SETTLE);

        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);

        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.put(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.STATENOTALLOW.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 调账（非正常流程。台账的交易是支付交易，找不到该笔交易,业务回滚）
     * author:zwr
     */
    @Test
    @Transactional
    public void modifyLedgeStatus111111() throws Exception {
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setTransId(501L);
        ledgerPo.setType(Ledger.PAY_TYPE);


        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(payTransPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg", is("支付交易对象(id=501)不存在")));
    }
    /**
     * 调账（正常流程。台账的交易是退款交易）
     * author:zwr
     */
    @Test
    @Transactional
    public void modifyLedgeStatus2() throws Exception {
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);

        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setStatus(Ledger.UNSETTLE);

        RefundTransPo payTransPo = new RefundTransPo();
        payTransPo.setId(501L);

        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(ledgerPoMapper.updateByPrimaryKeySelective(Mockito.any())).thenReturn(1);
        Mockito.when(refundTransPoMapper.updateByPrimaryKeySelective(Mockito.any())).thenReturn(1);

        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/ledgers/{id}", 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 调账（非正常流程。台账的交易是退款交易，未查询到交易，业务回滚）
     * author:zwr
     */
    @Test
    @Transactional
    public void modifyLedgeStatus22() throws Exception {
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setTransId(501L);
        ledgerPo.setType(Ledger.REFUND_TYPE);


        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(refundTransPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg", is("退款交易对象(id=501)不存在")));
    }

    /**
     * 调账（正常流程。台账的交易是分账支付交易）
     * author:zwr
     */
    @Test
    @Transactional
    public void modifyLedgeStatus3() throws Exception {
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);

        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setStatus(Ledger.UNSETTLE);

        DivPayTransPo payTransPo = new DivPayTransPo();
        payTransPo.setId(501L);
        payTransPo.setShopChannelId(1L);

        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(ledgerPoMapper.updateByPrimaryKeySelective(Mockito.any())).thenReturn(1);
        Mockito.when(divPayTransPoMapper.updateByPrimaryKeySelective(Mockito.any())).thenReturn(1);

        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/ledgers/{id}", 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 调账（非正常流程。台账的交易是分账支付交易，找不到该笔交易）
     * author:zwr
     */
    @Test
    @Transactional
    public void modifyLedgeStatus33() throws Exception {
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setTransId(501L);
        ledgerPo.setType(Ledger.DIVPAY_TYPE);


        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(divPayTransPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg", is("分账交易对象(id=501)不存在")));
    }
    /**
     * 调账（正常流程。台账的交易是分账退款交易）
     * author:zwr
     */
    @Test
    @Transactional
    public void modifyLedgeStatus4() throws Exception {
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);

        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setStatus(Ledger.UNSETTLE);

        DivRefundTransPo payTransPo = new DivRefundTransPo();
        payTransPo.setId(501L);

        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(ledgerPoMapper.updateByPrimaryKeySelective(Mockito.any())).thenReturn(1);
        Mockito.when(divRefundTransPoMapper.updateByPrimaryKeySelective(Mockito.any())).thenReturn(1);

        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.put(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 调账（非正常流程。台账的交易是分账退款交易，找不到该笔交易）
     * author:zwr
     */
    @Test
    @Transactional
    public void modifyLedgeStatus44() throws Exception {
        ShopChannelPo shopChannel = new ShopChannelPo();
        shopChannel.setStatus(ShopChannel.VALID);
        shopChannel.setChannelId(1L);
        shopChannel.setId(1L);
        shopChannel.setShopId(1L);

        ChannelPo channelPo = new ChannelPo();
        channelPo.setId(1L);

        LedgerPo ledgerPo = new LedgerPo();
        ledgerPo.setId(1L);
        ledgerPo.setShopChannelId(1L);
        ledgerPo.setTransId(501L);
        ledgerPo.setType(Ledger.DIVREFUND_TYPE);


        Mockito.when(ledgerPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(ledgerPo);
        Mockito.when(channelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(channelPo);
        Mockito.when(shopChannelPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(shopChannel);
        Mockito.when(divRefundTransPoMapper.selectByPrimaryKey((Mockito.any()))).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errmsg", is("分账回退对象(id=501)不存在")));
    }

    @Test
    @Transactional
    public void modifyLedgeStatus5() throws Exception {
        adminToken = jwtHelper.createToken(null, "13088admin", 0L, 1, 3600);
        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.put(LEDGER_BY_ID, 1,501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.AUTH_NEED_LOGIN.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

}
