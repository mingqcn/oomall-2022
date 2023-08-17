//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.GoodsTestApplication;
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
public class GrouponActControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    private ShopDao shopDao;

    private static String adminToken;

    @BeforeAll
    public static void setup() {
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 3600);
    }

    @Test
    public void createGrouponActTest()throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        String requestJson="{\"name\": \"团购活动1\", \"strategy\": {\"quantity\": 13, \"percentage\": 135 }}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/{shopId}/onsales/{pid}/groupons", 4L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("团购活动1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.thresholds[0].quantity", is(13)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delGrouponActTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{shopId}/groupons/{id}", 8L, 1L)
                        .header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void updateGrouponActByShopIdAndActIdTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        String requestJson="{\"name\": \"团购活动1\", \"strategy\": {\"quantity\": 13, \"percentage\": 135 }}";
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/groupons/{id}", 8L, 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveGrouponActTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/groupons")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("shopId", "4")
                        .param("productId", "1576")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("团购活动2")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveByShopIdTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        InternalReturnObject<Shop> retObj = new InternalReturnObject<>();
        retObj.setErrno(0);
        retObj.setErrmsg("成功");
        retObj.setData(Shop.builder().id(10L).name("商铺10").type((byte)0).build());
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(retObj);



        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/groupons", 4L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("productId", "1576")
                        .param("onsaleId", "2")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("团购活动2")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].name", is("团购活动3")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findByIdTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        InternalReturnObject<Shop> retObj = new InternalReturnObject<>();
        retObj.setErrno(0);
        retObj.setErrmsg("成功");
        retObj.setData(Shop.builder().id(10L).name("商铺10").type((byte)0).build());
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(retObj);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/groupons/{id}", 4L, 2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("团购活动2")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findGrouponActByIdTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        InternalReturnObject<Shop> retObj = new InternalReturnObject<>();
        retObj.setErrno(0);
        retObj.setErrmsg("成功");
        retObj.setData(Shop.builder().id(10L).name("商铺10").type((byte)0).build());
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(retObj);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/groupons/{id}", 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.shop.name", is("商铺10")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name",is("团购活动1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.thresholds[0].quantity",is(13)))
                .andDo(MockMvcResultHandlers.print());
    }
}
