//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.GoodsApplication;
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

public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopDao shopDao;

    @MockBean
    private RedisUtil redisUtil;

    private static final String PRODUCT_ID = "/products/{id}";
    private static final String ONSALE_ID = "/onsales/{id}";


    @Test
    public void findProductById() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        InternalReturnObject<Shop> retObj = new InternalReturnObject<>();
        retObj.setErrno(0);
        retObj.setErrmsg("成功");
        retObj.setData(Shop.builder().id(10L).name("商铺10").type((byte)0).build());

        Mockito.when(shopDao.getShopById(10L)).thenReturn(retObj);

        this.mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_ID, 1550)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", is(1550)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("欢乐家久宝桃罐头")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.price", is(53295)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status", is(Product.ONSHELF.intValue())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getOnsaleById() throws Exception {
        Shop shop = Shop.builder().id(4L).name("测试商铺4").build();
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        Mockito.when(shopDao.getShopById(4L)).thenReturn(new InternalReturnObject<>(shop));


        this.mockMvc.perform(MockMvcRequestBuilders.get(ONSALE_ID, 3)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", is(1552)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("欢乐家蜜桔")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.price", is(12650)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.quantity", is(26)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status", is(Product.OFFSHELF.intValue())))
                .andDo(MockMvcResultHandlers.print());
    }
}
