package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.ShopApplication;
import cn.edu.xmu.oomall.shop.ShopTestApplication;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

/**
* @author Zhanyu Liu
* @date 2022/12/2 21:10
*/
@SpringBootTest(classes = ShopTestApplication.class)
@AutoConfigureMockMvc
@Transactional
public class InternalFreightControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    RocketMQTemplate rocketMQTemplate;

    @MockBean
    FreightDao freightDao;
    @MockBean
    RedisUtil redisUtil;

    static String adminToken;

    static String shopToken;

    private static final String FREIGHT = "/internal/templates/{id}/regions/{rid}/freightprice";

    @BeforeAll
    public static void setup() {
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        shopToken = jwtHelper.createToken(15L, "shop1", 1L, 1, 3600);
    }


    @Test
    public void getFreight1() throws Exception {
        InternalReturnObject<List<Region>> tmp=new InternalReturnObject<>(new ArrayList<>(){
            {
                add(new Region(0L,"cn"));
            }
        });
        Mockito.when(freightDao.retrieveParentRegionsById(248059L)).thenReturn(tmp);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "[{\"orderItemId\": 1,\"productId\": 0,\"quantity\": 0,\"weight\": 0}]";
        this.mockMvc.perform(MockMvcRequestBuilders.post(FREIGHT,2L,248059L)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.freightPrice",is(500)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getFreight2() throws Exception {
        InternalReturnObject<List<Region>> tmp=new InternalReturnObject<>(new ArrayList<>(){
            {
                add(new Region(999999L,"xm"));
                add(new Region(999L,"cn"));
            }
        });
        Mockito.when(freightDao.retrieveParentRegionsById(777777L)).thenReturn(tmp);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "[{\"orderItemId\": 1,\"productId\": 0,\"quantity\": 0,\"weight\": 0}]";
        this.mockMvc.perform(MockMvcRequestBuilders.post(FREIGHT,2L,777777L)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }
}
