package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.ShopApplication;
import cn.edu.xmu.oomall.shop.ShopTestApplication;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Product;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
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

/**
 * @author chenyz
 * @date 2022-11-29 16:01
 */
@SpringBootTest(classes = ShopTestApplication.class)
@AutoConfigureMockMvc
@Transactional
public class ShopControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    RocketMQTemplate rocketMQTemplate;

    @MockBean
    RedisUtil redisUtil;

    @MockBean
    GoodsDao goodsDao;

    @MockBean
    FreightDao freightDao;

    static String adminToken;

    static String shopToken;

    private static final String SHOP_STATE = "/shops/states";
    private static final String SHOPS = "/shops";
    private static final String SHOP = "/shops/{id}";
    private static final String SHOP_ADMIN = "/shops/{id}/shops";
    private static final String SHOP_AUDIT = "/shops/{shopId}/newshops/{id}/audit";
    private static final String SHOP_ONLINE = "/shops/{id}/online";
    private static final String SHOP_OFFLINE = "/shops/{id}/offline";
    private static final String PRODUCT_SERVICE_DEFINE = "/shops/{did}/products/{id}/maintainers/{mid}/regions/{rid}";
    private static final String PRODUCT_SERVICE = "/shops/{did}/productservices/{id}";
    private static final String PRODUCT_REGION = "/shops/{did}/products/{pid}/region/{srid}";

    @BeforeAll
    public static void setup() {
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        shopToken = jwtHelper.createToken(15L, "shop1", 1L, 1, 3600);
    }

    @Test
    public void retrieveShopStates() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get(SHOP_STATE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].code", is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name", is("申请")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createShops1() throws Exception{
        String body = "{\"name\":\"test\",\"type\":0,\"consignee\":{\"name\":\"John\",\"mobile\":\"123\",\"regionId\":1,\"address\":\"test\"},\"freeThreshold\":0}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(SHOPS)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("test")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.type", is(0)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void createShops2() throws Exception{
        String body = "{\"name\":\"test\",\"type\":0,\"consignee\":{\"name\":\"John\",\"mobile\":\"123\",\"regionId\":1,\"address\":\"test\"}}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(SHOPS)
                        .header("authorization", shopToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.SHOP_USER_HASSHOP.getErrNo())));
    }

    @Test
    public void createShops3() throws Exception{
        String body = "{\"type\":0,\"consignee\":{\"name\":\"John\",\"mobile\":\"123\",\"regionId\":1,\"address\":\"test\"}}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(SHOPS)
                        .header("authorization", shopToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.FIELD_NOTVALID.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveShops1() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get(SHOPS)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("type", "0")
                        .param("name", "OOMALL自营商铺")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("OOMALL自营商铺")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].type", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveShops2() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get(SHOPS)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("type", "1")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(45)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("服务商1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].type", is(1)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveShops3() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get(SHOPS)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[6].id", is(7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[6].name", is("商铺7")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[6].type", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateShop1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"consignee\":{\"name\":\"John\",\"mobile\":\"123\",\"regionId\":1,\"address\":\"test\"},\"freeThreshold\":10}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(SHOP, 1)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void updateShop2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"consignee\":{\"name\":\"John\",\"mobile\":\"123\",\"regionId\":1,\"address\":\"test\"},\"freeThreshold\":10}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(SHOP, 2)
                        .header("authorization", shopToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void deleteShop1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(SHOP, 5)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void deleteShop2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(SHOP, 1)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.STATENOTALLOW.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void deleteShop3() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(SHOP, 5)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void findShopById() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(SHOP, 1)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("OOMALL自营商铺")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.type", is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.consignee.address", is("黄图岗南街112")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveAllShops1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(SHOP_ADMIN, 0)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("status", "0")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(8)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("商铺8")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].type", is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveAllShops2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(SHOP_ADMIN, 1)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("status", "0")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateShopAudit1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"conclusion\":true}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(SHOP_AUDIT, 0, 8)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void updateShopAudit2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"conclusion\":true}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(SHOP_AUDIT, 1, 8)
                        .header("authorization", shopToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void updateShopAudit3() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"conclusion\":null}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(SHOP_AUDIT, 0, 8)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.FIELD_NOTVALID.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateShopOnline1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.put(SHOP_ONLINE, 5)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void updateShopOnline2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.put(SHOP_ONLINE, 1)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void updateShopOffline1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.put(SHOP_OFFLINE, 1)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void updateShopOffline2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.put(SHOP_OFFLINE, 1)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void createProductService1() throws Exception{
        InternalReturnObject<Product> tmp=new InternalReturnObject<>(new Product(){{setId(5107L);}});
        tmp.setErrno(ReturnNo.OK.getErrNo());
        tmp.setErrmsg(ReturnNo.OK.getMessage());
        Mockito.when(goodsDao.retrieveProductById(5107L)).thenReturn(tmp);
        Mockito.when(freightDao.findRegionById(10L)).thenReturn(new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region(10L,"xxx")));
        String body = "{\"invalid\":1,\"priority\":1000,\"beginTime\":\"2000-12-12T23:59:59\",\"endTime\":\"2099-12-12T23:59:59\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_SERVICE_DEFINE, 45, 5107, 45, 10)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.product.id", is(5107)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.maintainer.id", is(45)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void createProductService2() throws Exception{
        String body = "{\"invalid\":1,\"priority\":-1,\"beginTime\":\"2000-12-12T23:59:59\",\"endTime\":\"2099-12-12T23:59:59\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_SERVICE_DEFINE, 45, 5107, 45, 10)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.FIELD_NOTVALID.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createProductService3() throws Exception{
        String body = "{\"invalid\":1,\"priority\":1000,\"beginTime\":\"2000-12-12T23:59:59\",\"endTime\":\"2099-12-12T23:59:59\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_SERVICE_DEFINE, 40, 5107, 45, 10)
                        .header("authorization", shopToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateProductService() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"invalid\":0,\"priority\":1,\"beginTime\":\"2000-12-12T23:59:59\",\"endTime\":\"2099-12-12T23:59:59\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PRODUCT_SERVICE, 45, 1)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void deleteProductService() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(PRODUCT_SERVICE, 45, 1)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void retrieveProductServiceByProductIdAndRegionId() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_REGION, 45, 5107, 152)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(45)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("服务商1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].type", is(1)))
                .andDo(MockMvcResultHandlers.print());
    }
}
