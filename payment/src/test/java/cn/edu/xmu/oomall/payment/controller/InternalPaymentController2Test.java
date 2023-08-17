package cn.edu.xmu.oomall.payment.controller;//School of Informatics Xiamen University, GPL-3.0 license

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.TransactionDao;
import cn.edu.xmu.oomall.payment.dao.bo.*;
import cn.edu.xmu.oomall.payment.mapper.generator.*;
import cn.edu.xmu.oomall.payment.mapper.generator.po.*;
import cn.edu.xmu.oomall.payment.service.PaymentService;

import cn.edu.xmu.oomall.payment.service.channel.PayAdaptor;
import cn.edu.xmu.oomall.payment.service.channel.dto.CheckResultDto;
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

import java.util.*;

import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
@Transactional
/**
 * @author Wanru Zhuang
 * @date 2022/11/27
 */
public class InternalPaymentController2Test {

    @Autowired
    private MockMvc mockMvc;
    private static String adminToken;
    @MockBean
    private RedisUtil redisUtil;
    @MockBean
    private RefundTransPoMapper refundTransPoMapper;

    @MockBean
    private PayTransPoMapper payTransPoMapper;

    @MockBean
    private DivRefundTransPoMapper divRefundTransPoMapper;

    @MockBean
    private DivPayTransPoMapper divPayTransPoMapper;

    @MockBean
    private TransactionDao transactionDao;

    @MockBean(name = "wePayChannel")
    private PayAdaptor payAdaptor;

    @MockBean(name = "aliPayChannel")
    private PayAdaptor payAdaptor2;

    private static final String CHECK = "/internal/shops/{shopId}/ledgers/check";
    private JwtHelper jwtHelper = new JwtHelper();

    @BeforeAll
    public static void setup(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    /**
     * 对账（正常流程，错帐）
     * author:zwr
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void modifyLedgesCheck1() throws Exception {
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("13088admin");
        LocalDateTime s = LocalDateTime.parse("2022-11-06T23:08:50");
        LocalDateTime e = LocalDateTime.parse("2022-11-08T23:08:50");

        List<PayTransPo>  pays  =new ArrayList<PayTransPo>();
        List<RefundTransPo>  refunds  =new ArrayList<RefundTransPo>();
        List<DivPayTransPo>  divPays  =new ArrayList<DivPayTransPo>();
        List<DivRefundTransPo>  divRefunds  =new ArrayList<DivRefundTransPo>();

        LocalDateTime l = LocalDateTime.parse("2022-11-07T23:08:50");
        PayTransPo p1 = new PayTransPo();
        p1.setId(501L);
        p1.setOutNo("1");
        p1.setStatus(PayTrans.SUCCESS);
        p1.setAmount(100L);
        p1.setTransNo("1");
        p1.setShopChannelId(501L);
        p1.setSuccessTime(l);

        PayTransPo p2 = new PayTransPo();
        p2.setId(502L);
        p2.setOutNo("2");
        p2.setStatus(PayTrans.SUCCESS);
        p2.setAmount(100L);
        p2.setTransNo("2");
        p2.setShopChannelId(501L);
        p2.setSuccessTime(l);

        RefundTransPo r1 = new RefundTransPo();
        r1.setId(501L);
        r1.setStatus(RefundTrans.SUCCESS);
        r1.setAmount(100L);
        r1.setOutNo("3");
        r1.setTransNo("3");
        r1.setShopChannelId(501L);
        r1.setSuccessTime(l);

        RefundTransPo r2 = new RefundTransPo();
        r2.setId(502L);
        r2.setStatus(RefundTrans.SUCCESS);
        r2.setAmount(100L);
        r2.setOutNo("4");
        r2.setTransNo("4");
        r2.setShopChannelId(501L);
        r2.setSuccessTime(l);

        DivPayTransPo dp1 = new DivPayTransPo();
        dp1.setId(501L);
        dp1.setAmount(100L);
        dp1.setOutNo("5");
        dp1.setTransNo("5");
        dp1.setShopChannelId(501L);
        dp1.setSuccessTime(l);

        DivPayTransPo dp2 = new DivPayTransPo();
        dp2.setId(502L);
        dp2.setAmount(100L);
        dp2.setOutNo("6");
        dp2.setTransNo("6");
        dp2.setShopChannelId(501L);
        dp2.setSuccessTime(l);

        DivRefundTransPo dr1 = new DivRefundTransPo();
        dr1.setId(501L);
        dr1.setAmount(100L);
        dr1.setOutNo("7");
        dr1.setTransNo("7");
        dr1.setShopChannelId(501L);
        dr1.setSuccessTime(l);

        DivRefundTransPo dr2 = new DivRefundTransPo();
        dr2.setId(502L);
        dr2.setAmount(100L);
        dr2.setOutNo("8");
        dr2.setTransNo("8");
        dr2.setShopChannelId(501L);
        dr2.setSuccessTime(l);

        pays.add(p1);
        pays.add(p2);
        refunds.add(r1);
        refunds.add(r2);
        divPays.add(dp1);
        divPays.add(dp2);
        divRefunds.add(dr1);
        divRefunds.add(dr2);

        CheckResultDto resultDto_p1 = new CheckResultDto(p1.getId(), PayTrans.class.getName(),100L);
        CheckResultDto resultDto_p2 = new CheckResultDto(p2.getId(), PayTrans.class.getName(),100L);;
        CheckResultDto resultDto_r1= new CheckResultDto(r1.getId(), RefundTrans.class.getName(),100L);;
        CheckResultDto resultDto_r2= new CheckResultDto(r2.getId(), RefundTrans.class.getName(),100L);;
        CheckResultDto resultDto_dp1= new CheckResultDto(dp1.getId(), DivPayTrans.class.getName(),100L);;
        CheckResultDto resultDto_dp2= new CheckResultDto(dp2.getId(), DivPayTrans.class.getName(),100L);;
        CheckResultDto resultDto_dr1= new CheckResultDto(dr1.getId(), DivRefundTrans.class.getName(),100L);;
        CheckResultDto resultDto_dr2= new CheckResultDto(dr2.getId(), DivRefundTrans.class.getName(),100L);;
        List<CheckResultDto> results = new ArrayList<>();
        results.add(resultDto_p1);
        results.add(resultDto_p2);
        results.add(resultDto_r1);
        results.add(resultDto_r2);
        results.add(resultDto_dp1);
        results.add(resultDto_dp2);
        results.add(resultDto_dr1);
        results.add(resultDto_dr2);

        StringBuilder body = new StringBuilder();
        body.append("{\"beginTime\": \"2022-11-06T23:08:50\",");
        body.append("\"endTime\": \"2022-11-08T23:08:50\"}");

        Mockito.when(payTransPoMapper.selectByExample(Mockito.any())).thenReturn(pays);
        Mockito.when(refundTransPoMapper.selectByExample(Mockito.any())).thenReturn(refunds);
        Mockito.when(divPayTransPoMapper.selectByExample(Mockito.any())).thenReturn(divPays);
        Mockito.when(divRefundTransPoMapper.selectByExample(Mockito.any())).thenReturn(divRefunds);
        Mockito.when(payAdaptor.checkTransaction(Mockito.any())).thenReturn(results);
        Mockito.doNothing().when(transactionDao).saveById(Mockito.anyLong(),Mockito.anyString(), Mockito.any(), Mockito.any());
        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.put(CHECK, 1L)
                        .header("authorization", adminToken)
                        .content(body.toString())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ReturnNo.OK.getErrNo()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }


    @Test
    @Transactional
    public void modifyLedgesCheck2() throws Exception {
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("13088admin");
        LocalDateTime s = LocalDateTime.parse("2022-11-06T23:08:50");
        LocalDateTime e = LocalDateTime.parse("2022-11-08T23:08:50");

        StringBuilder body = new StringBuilder();
        body.append("{\"beginTime\": \"2022-11-09T23:09:50\",");
        body.append("\"endTime\": \"2022-11-08T23:08:50\"}");

        this.mockMvc.perform(MockMvcRequestBuilders.put(CHECK, 1L)
                        .header("authorization", adminToken)
                        .content(body.toString())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno").value(ReturnNo.LATE_BEGINTIME.getErrNo()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }
}