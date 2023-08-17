package cn.edu.xmu.oomall.payment.controller;//School of Informatics Xiamen University, GPL-3.0 license

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.service.PaymentService;
import cn.edu.xmu.oomall.payment.service.openfeign.WeParam.WePostTransRetObj;
import cn.edu.xmu.oomall.payment.service.openfeign.WePayService;

import org.hamcrest.Matchers;
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

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
@Transactional
public class InternalPaymentController1Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    private WePayService wePayService;
    @MockBean(name = "cn.edu.xmu.oomall.payment.service.PaymentService")
    private PaymentService paymentService;
    private static String adminToken;

    private static final String INTERNAL_PAYMENTS = "/internal/payments";
    private static final String FIND_REFUND = "/internal/shops/{shopId}/refunds/{id}";
    private static final String INTERNALREFUNDS = "/internal/shops/{shopId}/payments/{id}/refunds";
    private JwtHelper jwtHelper = new JwtHelper();

    @BeforeAll
    public static void setup() {
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    public void createPayment1() throws Exception {
        WePostTransRetObj retObj = new WePostTransRetObj();
        retObj.setPrepayId("111111");
        InternalReturnObject<WePostTransRetObj> returnObject = new InternalReturnObject<>();
        returnObject.setData(retObj);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(wePayService.postTransaction(Mockito.any())).thenReturn(returnObject);

        String body = "{\"spOpenid\":\"10000\",\"amount\":100,\"shopChannelId\":501,\"beginTime\":\"2022-01-01T00:00:00\", \"divAmount\":10}";
        String ret = this.mockMvc.perform(MockMvcRequestBuilders.post(INTERNAL_PAYMENTS, 100)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.prepayId", is("111111")))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

        Long payTransId = JacksonUtil.parseObject(ret, "data.id", Long.class);

    }

    @Test
    public void createPayment2() throws Exception {
        String body = "{\"spOpenid\":\"10000\",\"amount\":-1,\"shopChannelId\":501}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(INTERNAL_PAYMENTS, 100)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.FIELD_NOTVALID.getErrNo())));
    }

/*    @Test
    public void createRefund2() throws Exception {
        LocalDateTime successTime = LocalDateTime.parse("2022-11-04T18:37:00", DATE_TIME_FORMATTER);
        PostRefundAdaptorDto dto = new PostRefundAdaptorDto();
        dto.setAmount(1L);
        dto.setStatus(RefundTrans.NEW);
        dto.setSuccessTime(successTime);
        dto.setTransNo("123456789");
        dto.setUserReceivedAccount("hello");


        Mockito.when(payAdaptor.createRefund(Mockito.any())).thenReturn(dto);

        this.mockMvc.perform(MockMvcRequestBuilders.post(INTERNALREFUNDS, 1, 551)
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8")
                        .content("{\"amount\": 1}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andDo(MockMvcResultHandlers.print());

    }*/

    @Test
    public void createRefund3() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(INTERNALREFUNDS, 2, 551)
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8")
                        .content("{\"amount\": 2, \"divAmount\": 1}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())));
        //.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createRefund4() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(INTERNALREFUNDS, 2, 551)
                        .header("authorization", adminToken)
                        .content("{\"amount111\": 1}")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.FIELD_NOTVALID.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createRefund5() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(INTERNALREFUNDS, 2, 551)
                        .header("authorization", adminToken)
                        .content("{\"amount\": -1}")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.FIELD_NOTVALID.getErrNo())));
        //.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createRefund1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(INTERNALREFUNDS, 1, 1)
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8")
                        .content("{\"amount\": 1}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        //.andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void findRefundById1() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.get(FIND_REFUND, 1, 501)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", is(501)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.outNo", is("501")));
        //.andDo(MockMvcResultHandlers.print());
    }
}