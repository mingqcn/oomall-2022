package cn.edu.xmu.oomall.wechatpay.controller;

import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.wechatpay.WeChatPayApplication;
//import cn.edu.xmu.oomall.wechatpay.microservice.WeChatPayNotifyService;
import cn.edu.xmu.oomall.wechatpay.microservice.WeChatPayNotifyService;
import cn.edu.xmu.oomall.wechatpay.model.vo.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author ziyi guo
 * @date 2021/12/2
 */
@SpringBootTest(classes = WeChatPayApplication.class)
@AutoConfigureMockMvc
@Transactional
public class WeChatPayControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WeChatPayNotifyService weChatPayNotifyService;

    @Test
    @Transactional
    public void createReceiverTest1() throws Exception{
        WeChatPayReceiverVo weChatPayReceiverVo = new WeChatPayReceiverVo();
        weChatPayReceiverVo.setAppid("wx8888888888888888");
        weChatPayReceiverVo.setType("MERCHANT_ID");
        weChatPayReceiverVo.setName("hu89ohu89ohu89o");
        weChatPayReceiverVo.setCustomRelation("代理商");
        weChatPayReceiverVo.setRelationType("STORE");
        weChatPayReceiverVo.setAccount("86693853");
        String responseString = this.mvc.perform(post("/internal/wechat/profitsharing/receivers/add")
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(weChatPayReceiverVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":{\"type\":\"MERCHANT_ID\",\"account\":\"86693853\"," +
                "\"name\":\"hu89ohu89ohu89o\",\"relationType\":\"STORE\"}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void createReceiverTest2() throws Exception{
        WeChatPayReceiverVo weChatPayReceiverVo = new WeChatPayReceiverVo();
        weChatPayReceiverVo.setAppid("wx8888888888888888");
        weChatPayReceiverVo.setType("MERCHANT_ID");
        weChatPayReceiverVo.setName("hu89ohu89ohu89o");
        weChatPayReceiverVo.setCustomRelation("代理商");
        weChatPayReceiverVo.setRelationType("STORE");
        weChatPayReceiverVo.setAccount("86693852");
        String responseString = this.mvc.perform(post("/internal/wechat/profitsharing/receivers/add")
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(weChatPayReceiverVo)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"商户回退单号重复\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    @Test
    @Transactional
    public void deleteReceiverTest1() throws Exception{
        String responseString = this.mvc.perform(delete("/internal/wechat/profitsharing/receivers/delete")
                        .contentType("application/json;charset=UTF-8")
                        .param("appid", "wx8888888888888888")
                        .param("type", "MERCHANT_ID")
                        .param("account", "86693852"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":{\"type\":\"MERCHANT_ID\",\"account\":\"86693852\"}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void deleteReceiverTest2() throws Exception{
        String responseString = this.mvc.perform(delete("/internal/wechat/profitsharing/receivers/delete")
                        .contentType("application/json;charset=UTF-8")
                        .param("appid", "wx8888888888888888")
                        .param("type", "MERCHANT_ID")
                        .param("account", "86693853"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"请求参数符合参数格式，但不符合业务规则\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    @Test
    @Transactional
    public void createDivRefundTransTest1() throws Exception{
        WeChatPayDivRefundTransVo weChatPayDivRefundTransVo = new WeChatPayDivRefundTransVo();
        weChatPayDivRefundTransVo.setOutOrderNo("P20150806125346");
        weChatPayDivRefundTransVo.setOutReturnNo("R20190516002");
        weChatPayDivRefundTransVo.setAmount(10);
        weChatPayDivRefundTransVo.setReturnMchid("86693852");
        weChatPayDivRefundTransVo.setDescription("用户退款");
        String responseString = this.mvc.perform(post("/internal/wechat/profitsharing/return-orders").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(weChatPayDivRefundTransVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":{\n" +
                "  \"orderId\": \"3008450740201411110007820472\",\n" +
                "  \"outOrderNo\": \"P20150806125346\",\n" +
                "  \"outReturnNo\": \"R20190516002\",\n" +
                "  \"returnId\": \"3008450740201411110007820472\",\n" +
                "  \"returnMchid\": \"86693852\",\n" +
                "  \"amount\": 10,\n" +
                "  \"description\": \"用户退款\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"createTime\": \"2015-05-20T13:29:35+08:00\",\n" +
                "  \"finishTime\": \"2015-05-20T13:29:35+08:00\"\n" +
                "}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void createDivRefundTransTest2() throws Exception{
        WeChatPayDivRefundTransVo weChatPayDivRefundTransVo = new WeChatPayDivRefundTransVo();
        weChatPayDivRefundTransVo.setOutOrderNo("P20150806125346");
        weChatPayDivRefundTransVo.setOutReturnNo("R20190516001");
        weChatPayDivRefundTransVo.setAmount(10);
        weChatPayDivRefundTransVo.setReturnMchid("86693852");
        weChatPayDivRefundTransVo.setDescription("用户退款");
        String responseString = this.mvc.perform(post("/internal/wechat/profitsharing/return-orders").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(weChatPayDivRefundTransVo)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"商户回退单号重复\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void createDivRefundTransTest3() throws Exception{
        WeChatPayDivRefundTransVo weChatPayDivRefundTransVo = new WeChatPayDivRefundTransVo();
        weChatPayDivRefundTransVo.setOutOrderNo("P20150806125347");
        weChatPayDivRefundTransVo.setOutReturnNo("R20190516002");
        weChatPayDivRefundTransVo.setAmount(10);
        weChatPayDivRefundTransVo.setReturnMchid("86693852");
        weChatPayDivRefundTransVo.setDescription("用户退款");
        String responseString = this.mvc.perform(post("/internal/wechat/profitsharing/return-orders").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(weChatPayDivRefundTransVo)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"查询的资源不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    @Test
    @Transactional
    public void getDivRefundTransTest1() throws Exception{
        String responseString = this.mvc.perform(get("/internal/wechat/profitsharing/return-orders/R20190516001")
                        .contentType("application/json;charset=UTF-8")
                        .param("out_order_no", "P20190806125346"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":{\n" +
                "  \"orderId\": \"3008450740201411110007820472\",\n" +
                "  \"outOrderNo\": \"P20150806125346\",\n" +
                "  \"outReturnNo\": \"R20190516001\",\n" +
                "  \"returnId\": \"3008450740201411110007820472\",\n" +
                "  \"returnMchid\": \"86693852\",\n" +
                "  \"amount\": 100,\n" +
                "  \"description\": \"用户退款\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"createTime\": \"2015-05-20T13:29:35+08:00\",\n" +
                "  \"finishTime\": \"2015-05-20T13:29:35+08:00\"\n" +
                "}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    @Test
    @Transactional
    public void createDivPayTransTest1() throws Exception{
        WeChatPayDivPayTransVo weChatPayDivPayTransVo = new WeChatPayDivPayTransVo();
        weChatPayDivPayTransVo.setAppid("wx8888888888888888");
        weChatPayDivPayTransVo.setTransactionId("2");
        WeChatPayDivReceiverVo weChatPayDivReceiverVo = new WeChatPayDivReceiverVo("MERCHANT_ID","86693852","hu89ohu89ohu89o",30,"分给商户A");
        Collection<WeChatPayDivReceiverVo> receivers = new ArrayList<>();
        receivers.add(weChatPayDivReceiverVo);
        weChatPayDivPayTransVo.setReceivers(receivers);
        weChatPayDivPayTransVo.setOutOrderNo("P20150806125347");
        weChatPayDivPayTransVo.setUnfreezeUnsplit(Boolean.TRUE);

        String responseString = this.mvc.perform(post("/internal/wechat/profitsharing/orders")
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(weChatPayDivPayTransVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":{" +
                "  \"transactionId\": \"2\",\n" +
                "  \"outOrderNo\": \"P20150806125347\",\n" +
                "  \"orderId\": \"3008450740201411110007820472\",\n" +
                "  \"state\": \"FINISHED\",\n" +
                "  \"receivers\": [\n" +
                "    {\n" +
                "      \"amount\": 30,\n" +
                "      \"description\": \"分给商户A\",\n" +
                "      \"type\": \"MERCHANT_ID\",\n" +
                "      \"account\": \"86693852\",\n" +
                "      \"result\": \"SUCCESS\",\n" +
                "      \"failReason\": \"NULL\",\n" +
                "      \"detailId\": \"202211292022236EQ5\",\n" +
                "      \"createTime\": \"2015-05-20T13:29:35+08:00\",\n" +
                "      \"finishTime\": \"2015-05-20T13:29:35+08:00\"\n" +
                "    }\n" +
                "  ]\n" +
                "}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void createDivPayTransTest2() throws Exception {
        WeChatPayDivPayTransVo weChatPayDivPayTransVo = new WeChatPayDivPayTransVo();
        weChatPayDivPayTransVo.setAppid("wx8888888888888888");
        weChatPayDivPayTransVo.setTransactionId("11");
        WeChatPayDivReceiverVo weChatPayDivReceiverVo = new WeChatPayDivReceiverVo("MERCHANT_ID", "86693852", "hu89ohu89ohu89o", 30, "分给商户A");
        Collection<WeChatPayDivReceiverVo> receivers = new ArrayList<>();
        receivers.add(weChatPayDivReceiverVo);
        weChatPayDivPayTransVo.setReceivers(receivers);
        weChatPayDivPayTransVo.setOutOrderNo("P20150806125347");
        weChatPayDivPayTransVo.setUnfreezeUnsplit(Boolean.TRUE);

        String responseString = this.mvc.perform(post("/internal/wechat/profitsharing/orders")
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(weChatPayDivPayTransVo)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"查询的资源不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void createDivPayTransTest3() throws Exception {
        WeChatPayDivPayTransVo weChatPayDivPayTransVo = new WeChatPayDivPayTransVo();
        weChatPayDivPayTransVo.setAppid("wx8888888888888888");
        weChatPayDivPayTransVo.setTransactionId("2");
        WeChatPayDivReceiverVo weChatPayDivReceiverVo = new WeChatPayDivReceiverVo("MERCHANT_ID", "86693852", "hu89ohu89ohu89o", 30, "分给商户A");
        Collection<WeChatPayDivReceiverVo> receivers = new ArrayList<>();
        receivers.add(weChatPayDivReceiverVo);
        weChatPayDivPayTransVo.setReceivers(receivers);
        weChatPayDivPayTransVo.setOutOrderNo("P20150806125346");
        weChatPayDivPayTransVo.setUnfreezeUnsplit(Boolean.TRUE);

        String responseString = this.mvc.perform(post("/internal/wechat/profitsharing/orders")
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(weChatPayDivPayTransVo)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"商户分账单号重复\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void createDivPayTransTest4() throws Exception {
        WeChatPayDivPayTransVo weChatPayDivPayTransVo = new WeChatPayDivPayTransVo();
        weChatPayDivPayTransVo.setAppid("wx8888888888888888");
        weChatPayDivPayTransVo.setTransactionId("2");
        WeChatPayDivReceiverVo weChatPayDivReceiverVo = new WeChatPayDivReceiverVo("MERCHANT_ID", "86693852", "hu89ohu89ohu89o", 35, "分给商户A");
        Collection<WeChatPayDivReceiverVo> receivers = new ArrayList<>();
        receivers.add(weChatPayDivReceiverVo);
        weChatPayDivPayTransVo.setReceivers(receivers);
        weChatPayDivPayTransVo.setOutOrderNo("P20150806125347");
        weChatPayDivPayTransVo.setUnfreezeUnsplit(Boolean.TRUE);

        String responseString = this.mvc.perform(post("/internal/wechat/profitsharing/orders")
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(weChatPayDivPayTransVo)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"分账金额不足\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void createDivPayTransTest5() throws Exception{
        WeChatPayDivPayTransVo weChatPayDivPayTransVo = new WeChatPayDivPayTransVo();
        weChatPayDivPayTransVo.setAppid("wx8888888888888888");
        weChatPayDivPayTransVo.setTransactionId("2");
        WeChatPayDivReceiverVo weChatPayDivReceiverVo = new WeChatPayDivReceiverVo("MERCHANT_ID","86693853","hu89ohu89ohu89o",30,"分给商户A");
        Collection<WeChatPayDivReceiverVo> receivers = new ArrayList<>();
        receivers.add(weChatPayDivReceiverVo);
        weChatPayDivPayTransVo.setReceivers(receivers);
        weChatPayDivPayTransVo.setOutOrderNo("P20150806125347");
        weChatPayDivPayTransVo.setUnfreezeUnsplit(Boolean.TRUE);

        String responseString = this.mvc.perform(post("/internal/wechat/profitsharing/orders")
                        .contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(weChatPayDivPayTransVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":{" +
                "  \"transactionId\": \"2\",\n" +
                "  \"outOrderNo\": \"P20150806125347\",\n" +
                "  \"orderId\": \"3008450740201411110007820472\",\n" +
                "  \"state\": \"PROCESSING\",\n" +
                "  \"receivers\": [\n" +
                "    {\n" +
                "      \"amount\": 30,\n" +
                "      \"description\": \"分给商户A\",\n" +
                "      \"type\": \"MERCHANT_ID\",\n" +
                "      \"account\": \"86693853\",\n" +
                "      \"result\": \"PENDING\",\n" +
                "      \"failReason\": \"NO_RELATION\",\n" +
                "      \"detailId\": \"202211292022236EQ5\",\n" +
                "      \"createTime\": \"2015-05-20T13:29:35+08:00\",\n" +
                "      \"finishTime\": \"2015-05-20T13:29:35+08:00\"\n" +
                "    }\n" +
                "  ]\n" +
                "}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void getDivPayTransTest1() throws Exception {
        String responseString = this.mvc.perform(get("/internal/wechat/profitsharing/orders/P20150806125346")
                        .contentType("application/json;charset=UTF-8")
                        .param("transaction_id", "4208450740201411110007820472"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":{ \n" +
                "  \"transactionId\": \"4208450740201411110007820472\",\n" +
                "  \"outOrderNo\": \"P20150806125346\",\n" +
                "  \"orderId\": \"3008450740201411110007820472\",\n" +
                "  \"state\": \"FINISHED\",\n" +
                "  \"receivers\": [\n" +
                "    {\n" +
                "      \"amount\": 100,\n" +
                "      \"description\": \"分给商户1900000110\",\n" +
                "      \"type\": \"MERCHANT_ID\",\n" +
                "      \"account\": \"1900000109\",\n" +
                "      \"result\": \"SUCCESS\",\n" +
                "      \"failReason\": \"NULL\",\n" +
                "      \"detailId\": \"36011111111111111111111\",\n" +
                "      \"createTime\": \"2015-05-20T13:29:35+08:00\",\n" +
                "      \"finishTime\": \"2015-05-20T13:29:35+08:00\"\n" +
                "    }\n" +
                "  ]\n" +
                "}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void createTransactionTest1() throws Exception{

        Mockito.when(weChatPayNotifyService.paymentNotify(Mockito.any())).thenReturn(null);

        WeChatPayTransactionVo vo = new WeChatPayTransactionVo();
        vo.setAppid("wxd678efh567hg6787");
        vo.setMchid("1230000109");
        vo.setDescription("pay");
        vo.setAmount(new WeChatPayTransactionVo.TransactionAmountVo(100,"CNY"));
        vo.setPayer(new WeChatPayTransactionVo.PayerVo("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o"));
        vo.setNotifyUrl("/wechat/payment/notify");

        String responseString;
        String expectedResponse;

        for(int i=11; i<=18; i++){
            vo.setOutTradeNo(String.valueOf(i));
            responseString = this.mvc.perform(post("/internal/wechat/pay/transactions/jsapi").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            expectedResponse = "{\"errmsg\":\"成功\",\"data\":{\"prepayId\":\"wx26112221580621e9b071c00d9e093b0000\"}}";
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        }

    }

    @Test
    @Transactional
    public void createTransactionTest2() throws Exception {

        WeChatPayTransactionVo vo = new WeChatPayTransactionVo();
        vo.setAppid("wxd678efh567hg6787");
        vo.setMchid("1230000109");
        vo.setDescription("pay");
        vo.setOutTradeNo("1");
        vo.setAmount(new WeChatPayTransactionVo.TransactionAmountVo(100,"CNY"));
        vo.setPayer(new WeChatPayTransactionVo.PayerVo("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o"));
        vo.setNotifyUrl("/wechat/payment/notify");

        String responseString = this.mvc.perform(post("/internal/wechat/pay/transactions/jsapi").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"订单已支付\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void createTransactionTest3() throws Exception {

        WeChatPayTransactionVo vo = new WeChatPayTransactionVo();
        vo.setAppid("wxd678efh567hg6787");
        vo.setMchid("1230000109");
        vo.setDescription("pay");
        vo.setOutTradeNo("");
        vo.setAmount(new WeChatPayTransactionVo.TransactionAmountVo(100,"CNY"));
        vo.setPayer(new WeChatPayTransactionVo.PayerVo("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o"));
        vo.setNotifyUrl("/wechat/payment/notify");

        String responseString = this.mvc.perform(post("/internal/wechat/pay/transactions/jsapi").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"参数错误\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void getTransactionTest1() throws Exception {

        String responseString = this.mvc.perform(get("/internal/wechat/pay/transactions/out-trade-no/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":{\"appid\":\"wxd678efh567hg6787\",\"mchid\":\"1230000109\",\"outTradeNo\":\"1\",\"transactionId\":\"1\",\"tradeType\":\"JSAPI\",\"tradeState\":\"SUCCESS\",\"tradeStateDesc\":null,\"amount\":{\"total\":100,\"payerTotal\":100,\"currency\":\"CNY\",\"payerCurrency\":\"CNY\"},\"payer\":{\"openid\":\"oUpF8uMuAJO_M2pxb1Q9zNjWeS6o\"},\"successTime\":\"2021-12-02T13:02:47.000\"}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void getTransactionTest2() throws Exception {

        String responseString = this.mvc.perform(get("/internal/wechat/pay/transactions/out-trade-no/0"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"查询的资源不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void closeTransactionTest1() throws Exception {

        String responseString = this.mvc.perform(post("/internal/wechat/pay/transactions/out-trade-no/1/close"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void closeTransactionTest2() throws Exception {

        String responseString = this.mvc.perform(post("/internal/wechat/pay/transactions/out-trade-no/0/close"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"查询的资源不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void createRefundTest1() throws Exception{

        Mockito.when(weChatPayNotifyService.refundNotify(Mockito.any())).thenReturn(null);

        WeChatPayRefundVo vo = new WeChatPayRefundVo();
        vo.setNotifyUrl("/wechat/refund/notify");
        vo.setAmount(new WeChatPayRefundVo.RefundAmountVo(100,100,"CNY"));

        String responseString;
        String expectedResponse;

        for(int i=1; i<=8; i++){
            vo.setOutTradeNo(String.valueOf(i));
            vo.setOutRefundNo(String.valueOf(i));
            responseString = this.mvc.perform(post("/internal/wechat/refund/domestic/refunds").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
            expectedResponse = "{\"data\":{\"outRefundNo\":\""+i+"\",\"transactionId\":\"1217752501201407033233368018\",\"outTradeNo\":\""+i+"\",\"channel\":\"ORIGINAL\",\"userReceivedAccount\":\"招商银行信用卡0403\",\"amount\":{\"total\":null,\"refund\":100,\"payerTotal\":100,\"settlementRefund\":null,\"settlementTotal\":null,\"discountRefund\":null,\"currency\":null}}}";
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        }

    }

    @Test
    @Transactional
    public void createRefundTest2() throws Exception{

        Mockito.when(weChatPayNotifyService.refundNotify(Mockito.any())).thenReturn(null);

        WeChatPayRefundVo vo = new WeChatPayRefundVo();
        vo.setNotifyUrl("/wechat/refund/notify");
        vo.setAmount(new WeChatPayRefundVo.RefundAmountVo(120,100,"CNY"));
        vo.setOutTradeNo("1");
        vo.setOutRefundNo("1");

        String responseString = this.mvc.perform(post("/internal/wechat/refund/domestic/refunds").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"退款金额错误\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    @Test
    @Transactional
    public void createRefundTest3() throws Exception{

        Mockito.when(weChatPayNotifyService.refundNotify(Mockito.any())).thenReturn(null);

        WeChatPayRefundVo vo = new WeChatPayRefundVo();
        vo.setNotifyUrl("/wechat/refund/notify");
        vo.setAmount(new WeChatPayRefundVo.RefundAmountVo(100,100,"CNY"));
        vo.setOutTradeNo("9");
        vo.setOutRefundNo("1");

        String responseString = this.mvc.perform(post("/internal/wechat/refund/domestic/refunds").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"对应支付单未成功支付\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    @Test
    @Transactional
    public void createRefundTest4() throws Exception{

        Mockito.when(weChatPayNotifyService.refundNotify(Mockito.any())).thenReturn(null);

        WeChatPayRefundVo vo = new WeChatPayRefundVo();
        vo.setNotifyUrl("/wechat/refund/notify");
        vo.setAmount(new WeChatPayRefundVo.RefundAmountVo(100,100,"CNY"));
        vo.setOutTradeNo("");
        vo.setOutRefundNo("1");

        String responseString = this.mvc.perform(post("/internal/wechat/refund/domestic/refunds").contentType("application/json;charset=UTF-8").content(JacksonUtil.toJson(vo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"参数错误\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    @Test
    @Transactional
    public void getRefundTest1() throws Exception {

        String responseString = this.mvc.perform(get("/internal/wechat/refund/domestic/refunds/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"data\":{\"refundId\":\"1\",\"outRefundNo\":\"10\",\"transactionId\":\"1217752501201407033233368018\",\"outTradeNo\":\"10\",\"channel\":\"ORIGINAL\",\"userReceivedAccount\":\"招商银行信用卡0403\",\"successTime\":\"2021-12-02T15:08:42.000\",\"status\":\"SUCCESS\",\"amount\":{\"total\":null,\"refund\":100,\"payerTotal\":100,\"payerRefund\":100,\"settlementRefund\":null,\"settlementTotal\":null,\"discountRefund\":null,\"currency\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void getRefundTest2() throws Exception {

        String responseString = this.mvc.perform(get("/internal/wechat/refund/domestic/refunds/0"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"查询的资源不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Transactional
    public void getFundFlowBillTest1() throws Exception {

        String responseString = this.mvc.perform(get("/internal/wechat/bill/fundflowbill").queryParam("bill_date","2021-12-02"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errmsg\":\"成功\",\"data\":{\"hashType\":\"SHA1\",\"hashValue\":\"79bb0f45fc4c42234a918000b2668d689e2bde04\",\"downloadUrl\":\"https://api.mch.weixin.qq.com/v3/billdownload/file?token=xxx\"}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

}
