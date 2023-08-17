//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.controller;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.bo.RefundTrans;
import cn.edu.xmu.oomall.payment.service.channel.PayAdaptor;
import cn.edu.xmu.oomall.payment.service.channel.WePayAdaptor;
import cn.edu.xmu.oomall.payment.service.channel.dto.PostRefundAdaptorDto;
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

import java.time.LocalDateTime;

import static cn.edu.xmu.javaee.core.model.Constants.DATE_TIME_FORMATTER;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
@Transactional
public class InternalPaymentControllerAdapterTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    private WePayAdaptor wePayChannel;

    private static String adminToken;

    private static final String FIND_REFUND = "/internal/shops/{shopId}/refunds/{id}";
    private static final String INTERNALREFUNDS = "/internal/shops/{shopId}/payments/{id}/refunds";
    @BeforeAll
    public static void setup(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    public void createRefund2() throws Exception {
        LocalDateTime successTime = LocalDateTime.parse("2022-11-04T18:37:00", DATE_TIME_FORMATTER);
        PostRefundAdaptorDto dto = new PostRefundAdaptorDto();
        dto.setAmount(1L);
        dto.setStatus(RefundTrans.NEW);
        dto.setSuccessTime(successTime);
        dto.setTransNo("123456789");
        dto.setUserReceivedAccount("hello");

        Mockito.when(wePayChannel.createRefund(Mockito.any())).thenReturn(dto);

        this.mockMvc.perform(MockMvcRequestBuilders.post(INTERNALREFUNDS, 1, 551)
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8")
                        .content("{\"amount\": 2, \"divAmount\": 1 }"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.PAY_REFUND_MORE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void createRefund3() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(INTERNALREFUNDS,2, 551)
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8")
                        .content("{\"amount\": 2, \"divAmount\": 1 }"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())));
        //.andDo(MockMvcResultHandlers.print());
    }



    @Test
    public void createRefund4() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(INTERNALREFUNDS,2, 551)
                        .header("authorization", adminToken)
                        .content("{\"amount11\":1, \"divAmount\": 1 }")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.FIELD_NOTVALID.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }



    @Test
    public void createRefund5() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(INTERNALREFUNDS,2,551)
                        .header("authorization", adminToken)
                        .content("{\"amount\": -1}")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.FIELD_NOTVALID.getErrNo())));
        //.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createRefund1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(INTERNALREFUNDS,1,1)
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8")
                        .content("{\"amount\": 3, \"divAmount\": 2 }"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())));
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
                .andDo(MockMvcResultHandlers.print());
    }
}
