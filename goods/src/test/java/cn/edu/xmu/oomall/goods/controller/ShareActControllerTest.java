package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.GoodsApplication;
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

/**
 * @author 黄坤鹏
 * @date 2022/12/1 20:00
 */
@SpringBootTest(classes = GoodsTestApplication.class)
@AutoConfigureMockMvc
@Transactional
public class ShareActControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    private ShopDao shopDao;

    private static String adminToken;

    @BeforeAll
    public static void setup(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    public void createShareActTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        String requestJson="{\"name\": \"分享共赢\", \"strategy\": {\"quantity\": 12, \"percentage\": 135 }}";
        this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/{shopId}/shareactivities", 3L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("分享共赢")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.thresholds[0].quantity", is(12)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createActivityOnsaleTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Shop shop = Shop.builder().id(4L).name("kp小屋").type((byte) 1).build();
        InternalReturnObject<Shop> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setErrno(0);
        internalReturnObject.setErrmsg("成功");
        internalReturnObject.setData(shop);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(internalReturnObject);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/shops/{shopId}/onsale/{id}/shareact/{sid}", 0L, 2L, 2L)
                        .header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.product.name", is("欢乐家杨梅罐头")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delActivityOnsaleTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/shops/{shopId}/onsale/{id}/shareact/{sid}", 8L, 5L, 1L)
                        .header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateShareActByShopIdAndActIdTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        String requestJson="{\"name\": \"分享共赢\", \"strategy\": {\"quantity\": 12, \"percentage\": 135 }}";
        this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/{shopId}/shareactivities/{id}", 8L, 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveByShopIdTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Shop shop = Shop.builder().id(4L).name("kp小屋").type((byte) 1).build();
        InternalReturnObject<Shop> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setErrno(0);
        internalReturnObject.setErrmsg("成功");
        internalReturnObject.setData(shop);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(internalReturnObject);


        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/shareactivities", 4L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("productId", "1552")
                        .param("onsaleId", "3")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("青春飞扬")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[1].name", is("分享冲冲冲")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findByIdTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Shop shop = Shop.builder().id(4L).name("kp小屋").type((byte) 1).build();
        InternalReturnObject<Shop> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setErrno(0);
        internalReturnObject.setErrmsg("成功");
        internalReturnObject.setData(shop);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(internalReturnObject);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/{shopId}/shareactivities/{id}", 4L, 2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("青春飞扬")))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void retrieveShareActTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/shareactivities")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("shopId", "8")
                        .param("productId", "5461")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("与你有约")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findShareActByIdTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Shop shop = Shop.builder().id(4L).name("kp小屋").type((byte) 1).build();
        InternalReturnObject<Shop> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setErrno(0);
        internalReturnObject.setErrmsg("成功");
        internalReturnObject.setData(shop);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(internalReturnObject);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/shareactivities/{id}", 1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.shop.name", is("kp小屋")))
                .andDo(MockMvcResultHandlers.print());
    }

}
