//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import cn.edu.xmu.oomall.goods.dao.bo.Product;
import cn.edu.xmu.oomall.goods.dao.bo.Shop;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
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

@SpringBootTest(classes = GoodsTestApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AdvSaleActControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopDao shopDao;

    @MockBean
    private RedisUtil redisUtil;


    private static String adminToken;

    private static final String QUERYVALIDALLAD =    "/advancesales";
    private static final String QUERYVALIDADBYID =   "/advancesales/{id}";

    private static final String QUERYALLAD =    "/shops/{shopId}/advancesales";
    private static final String QUERYADBYID =   "/shops/{shopId}/advancesales/{id}";
    private static final String ADDAD =         "/shops/{shopId}/onsales/{id}/advancesales";
    private static final String UPDATEAD =      "/shops/{shopId}/advancesales/{id}";
    private static final String DELETEAD =      "/shops/{shopId}/advancesales/{id}";

    @BeforeAll
    public static void setup(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }


    @Test
    public void retrieveAllValidAdvanceSaleAct1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(QUERYVALIDALLAD, 1)
                .param("shopId","1")
                .param("productId", "6001")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(21)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("预售活动-测试1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].deposit", is(1000)))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void retrieveAllValidAdvanceSaleAct2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(QUERYVALIDALLAD, 1)
                .param("shopId","1")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(21)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("预售活动-测试1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].deposit", is(1000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].id", is(22)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].name", is("预售活动-测试2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].deposit", is(300)))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void retrieveAllValidAdvanceSaleAct3() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(QUERYVALIDALLAD, 1)
                .param("productId","6002")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(22)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("预售活动-测试2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].deposit", is(300)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveAllValidAdvanceSaleAct4() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(QUERYVALIDALLAD, 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(21)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("预售活动-测试1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].deposit", is(1000)))
                .andDo(MockMvcResultHandlers.print());
    }



    @Test
    public void retrieveValidAdvanceSaleActById1() throws Exception{
        Shop s = new Shop();
        s.setId(1L);
        s.setName("OOMALL自营商铺");
        s.setType((byte) 0);
        InternalReturnObject<Shop> ret = new InternalReturnObject<>(s);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(ret);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(QUERYVALIDADBYID, 22)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", is(22)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("预售活动-测试2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.deposit", is(300)))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void retrieveValidAdvanceSaleActById2() throws Exception{
        Shop s = new Shop();
        s.setId(1L);
        s.setName("OOMALL自营商铺");
        s.setType((byte) 0);
        InternalReturnObject<Shop> ret = new InternalReturnObject<>(s);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(ret);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(QUERYVALIDADBYID, 122)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void retrieveValidAdvanceSaleActById3() throws Exception{
        Shop s = new Shop();
        s.setId(1L);
        s.setName("OOMALL自营商铺");
        s.setType((byte) 0);
        InternalReturnObject<Shop> ret = new InternalReturnObject<>(s);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(ret);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(QUERYVALIDADBYID, 22343)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void retrieveAdvanceActByShopId1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(QUERYALLAD, 1)
                .header("authorization", adminToken)
                .param("productId", "6001")
                .param("onsaleId", "4001")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(21)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("预售活动-测试1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].deposit", is(1000)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveAdvanceActByShopId2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(QUERYALLAD, 1)
                .header("authorization", adminToken)
                .param("onsaleId", "4002")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(22)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("预售活动-测试2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].deposit", is(300)))
                .andDo(MockMvcResultHandlers.print());
    }



    @Test
    public void createAdvanceSaleAct1() throws Exception{

        String body="{\n" +
                "\"name\":\"好好学习\",\n" +
                "\"payTime\":\"2021-11-21T19:34:50.630\",\n" +
                "\"advancePayPrice\":1000\n" +
                "}";

        this.mockMvc.perform(MockMvcRequestBuilders.post(ADDAD,1L,2832L)
                .header("authorization", adminToken)
                .content(body.getBytes("utf-8"))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("好好学习")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.deposit", is(1000)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();

    }


    @Test
    public void retrieveAdvanceSaleActById2() throws Exception{
        Shop s = new Shop();
        s.setId(1L);
        s.setName("OOMALL自营商铺");
        s.setType((byte) 0);
        InternalReturnObject<Shop> ret = new InternalReturnObject<>(s);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(ret);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(QUERYADBYID, 1L,221L)
                .header("authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno",is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void retrieveAdvanceSaleActById3() throws Exception{
        Shop s = new Shop();
        s.setId(1L);
        s.setName("OOMALL自营商铺");
        s.setType((byte) 0);
        InternalReturnObject<Shop> ret = new InternalReturnObject<>(s);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(ret);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(QUERYADBYID, 1L,21L)
                .header("authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", is(21)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("预售活动-测试1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.deposit", is(1000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.shop.name", is("OOMALL自营商铺")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.shop.type", is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.shop.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.creator.name", is("admin")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.creator.id", is(1)))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void updateAdvanceSaleActById1() throws Exception{
        String reqbody = "{\n" +
                "  \"name\":\"好好学习天天向上\",\n" +
                "  \"price\":10000,\n" +
                "  \"advancePayPrice\": 1000,\n" +
                "  \"quantity\": 100,\n" +
                "  \"maxQuantity\": 200,\n" +
                "  \"beginTime\": \"2021-10-21T19:34:50.630\",\n" +
                "  \"endTime\": \"2021-12-21T19:34:50.630\",\n" +
                "  \"payTime\": \"2021-11-21T19:34:50.630\"\n" +
                "}";
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.put(UPDATEAD,1L,21L)
                .header("authorization", adminToken)
                .content(reqbody.getBytes("utf-8"))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void updateAdvanceSaleActById2() throws Exception{
        String reqbody = "{\n" +
                "  \"name\":\"好好学习天天向上\",\n" +
                "  \"price\":10000,\n" +
                "  \"advancePayPrice\": 1000,\n" +
                "  \"quantity\": 100,\n" +
                "  \"maxQuantity\": 200,\n" +
                "  \"beginTime\": \"2021-10-21T19:34:50.630\",\n" +
                "  \"endTime\": \"2021-12-21T19:34:50.630\",\n" +
                "  \"payTime\": \"2021-11-21T19:34:50.630\"\n" +
                "}";
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.put(UPDATEAD,1L,221L)
                .header("authorization", adminToken)
                .content(reqbody.getBytes("utf-8"))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteAdvanceSaleActById1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(DELETEAD,1L,21L)
                .header("authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteAdvanceSaleActById2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(DELETEAD,1L,121L)
                .header("authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

}
