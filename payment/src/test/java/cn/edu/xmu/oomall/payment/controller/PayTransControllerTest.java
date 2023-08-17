package cn.edu.xmu.oomall.payment.controller;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
@Transactional
public class PayTransControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisUtil redisUtil;

    private static String adminToken;


    @BeforeAll
    public static void setup(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    public void retrievePaymentStates() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/payments/states")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].code", is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name", is("待支付")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].code", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].name", is("已支付")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void alipayNotify1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        String requestJson="{\"app_id\": \"111\",\"trade_no\": \"222\",\"out_trade_no\": \"551\",\"gmt_payment\": \"2022-11-30 20:19:00\",\"trade_status\": \"TRADE_SUCCESS\",\"receipt_amount\": 100}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/notify/payments/alipay")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", CoreMatchers.is(ReturnNo.OK.getErrNo())));
    }

    @Test
    public void alipayNotify2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        String requestJson="{\"app_id\": \"111\",\"trade_no\": \"222\",\"out_trade_no\": \"551\",\"gmt_payment\": \"2022-11-30 20:19:00\",\"trade_status\": \"TRADE_CLOSED\",\"receipt_amount\": 100}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/notify/payments/alipay")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", CoreMatchers.is(ReturnNo.OK.getErrNo())));
    }

    @Test
    public void wePayNotify1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        String requestJson="{\"id\": \"111\",\"create_time\": \"2020-11-30T13:29:35+08:00\",\"resource\": {\"sp_appid\":\"111\",\"sp_mchid\":\"222\",\"sub_mchid\":\"333\",\"out_trade_no\":\"0\",\"transaction_id\":\"2\",\"trade_state\":\"SUCCESS\",\"success_time\":\"2020-11-30T13:30:35+08:00\",\"amount\":{\"total\":100,\"payer_total\":100},\"payer\":{\"sp_openid\":\"100\"}}}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/notify/payments/wepay")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", CoreMatchers.is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));
    }

    @Test
    public void wePayNotify2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        String requestJson="{\"id\": \"111\",\"create_time\": \"2020-11-30T13:29:35+08:00\",\"resource\": {\"sp_appid\":\"111\",\"sp_mchid\":\"222\",\"sub_mchid\":\"333\",\"out_trade_no\":\"551\",\"transaction_id\":\"2\",\"trade_state\":\"SUCCESS\",\"success_time\":\"2020-11-30T13:30:35+08:00\",\"amount\":{\"total\":100,\"payer_total\":100},\"payer\":{\"sp_openid\":\"100\"}}}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/notify/payments/wepay")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", CoreMatchers.is(ReturnNo.OK.getErrNo())));
    }
}
