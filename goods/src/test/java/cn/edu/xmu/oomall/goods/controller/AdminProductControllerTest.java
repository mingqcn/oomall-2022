//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.GoodsApplication;
import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import cn.edu.xmu.oomall.goods.dao.bo.Shop;
import cn.edu.xmu.oomall.goods.dao.bo.Template;
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
public class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopDao shopDao;

    @MockBean
    private RedisUtil redisUtil;

    JwtHelper jwtHelper = new JwtHelper();

    private static String adminToken;

    private static final String DRAFTPRODUCT="/shops/{shopId}/draftproducts";

    private static final String MODPRODUCT="/shops/{shopId}/draftproducts/{id}";

    private static final String PRODUCT="/shops/{shopId}/products/{id}";

    private static final String PRODUCT_TEMPLATE="/shops/{shopId}/products/{id}/templates";

    private static final String TEMPLATE_PRODUCT="/shops/{shopId}/templates/{fid}/products";

    private static final String PUBLISH_PRODUCT="/shops/{shopId}/draftproducts/{id}/publish";


    private static final String ONSALE_ID = "/shops/{shopId}/onsales/{id}";

    @BeforeAll
    public static void setup(){
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    public void retrieveChannelTest() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        InternalReturnObject<Shop> retObj = new InternalReturnObject<>();
        retObj.setErrno(0);
        retObj.setErrmsg("成功");
        retObj.setData(Shop.builder().id(10L).name("商铺10").type((byte)0).build());

        Mockito.when(shopDao.getShopById(10L)).thenReturn(retObj);

        this.mockMvc.perform(MockMvcRequestBuilders.get(ONSALE_ID, 10, 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.price", is(53295)))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.product.id", is(1550)))
                .andDo(MockMvcResultHandlers.print());
    }

    //可以创建，所属分类为二级分类
    @Test
    public void createSkuTest1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        String request="{\"name\": \"test_product\", \"originalPrice\": \"12345\", \"categoryId\": \"186\", \"originPlace\": \"china\"}";
        String newRequest="{\"skuSn\": \"test11\",\"name\": \"湘妹子剁辣椒\",\"originalPrice\": 100000,\"weight\": 1000,\"categoryId\": 257,  \"originPlace\": \"长沙\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(DRAFTPRODUCT, "1")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(newRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //不能创建，所属分类为一级分类
    @Test
    public void createSkuTest2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        String request="{\"name\": \"test_product\", \"originalPrice\": \"12345\", \"categoryId\": \"1\", \"originPlace\": \"china\"}";

        this.mockMvc.perform(MockMvcRequestBuilders.post(DRAFTPRODUCT, "10")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(203)))
                .andDo(MockMvcResultHandlers.print());
    }

    //不能创建，分类不存在
    @Test
    public void createSkuTest3() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        String request="{\"name\": \"test_product\", \"originalPrice\": \"12345\", \"categoryId\": \"0\", \"originPlace\": \"china\"}";

        this.mockMvc.perform(MockMvcRequestBuilders.post(DRAFTPRODUCT, "10")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(203)))
                .andDo(MockMvcResultHandlers.print());
    }

    //删除草稿商品
    @Test
    public void delProdTest1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        this.mockMvc.perform(MockMvcRequestBuilders.delete(MODPRODUCT, "10", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //删除草稿商品失败，商品不存在
    @Test
    public void delProdTest2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        this.mockMvc.perform(MockMvcRequestBuilders.delete(MODPRODUCT, "10", "111")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(4)))
                .andDo(MockMvcResultHandlers.print());
    }

    //删除草稿商品失败，无权限访问
    @Test
    public void delProdTest3() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        this.mockMvc.perform(MockMvcRequestBuilders.delete(MODPRODUCT, "11", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(17)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品成功
    @Test
    public void modDrfProdTest1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "10", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品成功
    @Test
    public void modDrfProdTest6() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);
        String request="{\"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "10", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品成功
    @Test
    public void modDrfProdTest7() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Mockito.when(redisUtil.bfExist(Mockito.anyString(), (Long) Mockito.any())).thenReturn(false);
        Mockito.when(redisUtil.bfAdd(Mockito.anyString(), Mockito.any())).thenReturn(true);

        String request="{\"skuSn\": \"123456\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "10", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品成功
    @Test
    public void modDrfProdTest8() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "10", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品成功
    @Test
    public void modDrfProdTest9() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "10", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品成功
    @Test
    public void modDrfProdTest10() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "10", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品成功
    @Test
    public void modDrfProdTest11() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"unit\": \"kg\", \"orignPlace\": \"china\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "10", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品成功
    @Test
    public void modDrfProdTest12() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"orignPlace\": \"china\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "10", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品成功
    @Test
    public void modDrfProdTest13() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "10", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品失败，不是二级分类
    @Test
    public void modDrfProdTest2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"1\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "10", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(203)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品失败，分类不存在
    @Test
    public void modDrfProdTest3() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"600\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "10", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(4)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品失败，商品不存在
    @Test
    public void modDrfProdTest4() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"0\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "10", "77777")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(4)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改草稿商品失败，无权限访问
    @Test
    public void modDrfProdTest5() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"0\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(MODPRODUCT, "1", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(17)))
                .andDo(MockMvcResultHandlers.print());
    }

    //查询商品成功
    @Test
    public void getProductIdTest1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        Shop shop = Shop.builder().id(2L).name("测试商铺10").build();
        Mockito.when(shopDao.getShopById(2L)).thenReturn(new InternalReturnObject<>(shop));

        Mockito.when(shopDao.getTemplateById(2L, 1L)).thenReturn(new InternalReturnObject<>(new Template()));
        this.mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //查询商品不存在
    @Test
    public void getProductIdTest2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT, "2", "54611111")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(4)))
                .andDo(MockMvcResultHandlers.print());
    }

    //查询商品无权限
    @Test
    public void getProductIdTest3() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT, "12", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(17)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品成功
    @Test
    public void putProductIdTest1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品失败，不允许加入一级分类
    @Test
    public void putProductIdTest2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"1\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(203)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品失败，没有这个商品
    @Test
    public void putProductIdTest3() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\", \"commissionRatio\": \"10\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461555")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(4)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品失败，无权限
    @Test
    public void putProductIdTest4() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\", \"commissionRatio\": \"10\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "1", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(17)))
                .andDo(MockMvcResultHandlers.print());
    }

    //分类不存在
    @Test
    public void putProductIdTest5() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"1866\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(4)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品成功
    @Test
    public void putProductIdTest6() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品成功
    @Test
    public void putProductIdTest7() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品成功
    @Test
    public void putProductIdTest8() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品成功
    @Test
    public void putProductIdTest9() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品成功
    @Test
    public void putProductIdTest10() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品成功
    @Test
    public void putProductIdTest11() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品成功
    @Test
    public void putProductIdTest12() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品成功
    @Test
    public void putProductIdTest13() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品成功
    @Test
    public void putProductIdTest14() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品成功
    @Test
    public void putProductIdTest15() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //修改商品成功
    @Test
    public void putProductIdTest16() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        String request="{\"skuSn\": \"123456\", \"name\": \"test_product\", \"originalPrice\": \"123\", \"categoryId\": \"186\", \"weight\": \"12\", \"barCode\": \"1222abcd\", \"unit\": \"kg\", \"orignPlace\": \"china\", \"shopLogisticsId\": \"123\", \"templateId\": \"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //查询商品模板
    @Test
    public void getProductTemplateTest1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_TEMPLATE, "2", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //查询商品模板失败，商品不存在
    @Test
    public void getProductTemplateTest2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_TEMPLATE, "2", "5461333")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(4)))
                .andDo(MockMvcResultHandlers.print());
    }

    //查询商品模板失败，无权限
    @Test
    public void getProductTemplateTest3() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_TEMPLATE, "1", "5461")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(17)))
                .andDo(MockMvcResultHandlers.print());
    }

    //查询模板对应的商品成功
    @Test
    public void getTemplateProductTest1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.get(TEMPLATE_PRODUCT, "0", "1")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //查询模板对应的商品失败
    @Test
    public void getTemplateProductTest2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.get(TEMPLATE_PRODUCT, "1", "1")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(17)))
                .andDo(MockMvcResultHandlers.print());
    }

    //发布商品成功
    @Test
    public void putGoodsTest1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.put(PUBLISH_PRODUCT, "0", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    //发布商品失败，权限不足
    @Test
    public void putGoodsTest2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.put(PUBLISH_PRODUCT, "1", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(17)))
                .andDo(MockMvcResultHandlers.print());
    }

    //发布商品成功
    @Test
    public void putGoodsTest3() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);

        this.mockMvc.perform(MockMvcRequestBuilders.put(PUBLISH_PRODUCT, "0", "70")
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }
}
