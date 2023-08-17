package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.ShopApplication;
import cn.edu.xmu.oomall.shop.ShopTestApplication;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import cn.edu.xmu.oomall.shop.dao.template.RegionTemplateDao;
import cn.edu.xmu.oomall.shop.dao.template.TemplateDao;
import cn.edu.xmu.oomall.shop.mapper.PieceTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.RegionTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.TemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.PieceTemplatePo;
import cn.edu.xmu.oomall.shop.mapper.po.RegionTemplatePo;
import cn.edu.xmu.oomall.shop.mapper.po.TemplatePo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
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

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;

/**
* @author Zhanyu Liu
* @date 2022/12/2 21:10
*/
@SpringBootTest(classes = ShopTestApplication.class)
@AutoConfigureMockMvc
@Transactional
public class TemplateControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    RocketMQTemplate rocketMQTemplate;

    @Autowired
    TemplatePoMapper templatePoMapper;

    @Autowired
    RegionTemplatePoMapper regionTemplatePoMapper;

    @Autowired
    PieceTemplatePoMapper pieceTemplatePoMapper;

    @MockBean
    RedisUtil redisUtil;
    @MockBean
    FreightDao freightDao;

    static String adminToken;

    static String shopToken;

    private static final String TEMPLATES = "/shops/{shopId}/templates";
    private static final String TEMPLATE= "/shops/{shopId}/templates/{id}";
    private static final String TEMPLATE_CLONE = "/shops/{shopId}/templates/{id}/clone";
    private static final String WEIGHT_TEMPLATE = "/shops/{shopId}/templates/{id}/regions/{rid}/weighttemplate";
    private static final String PIECE_TEMPLATE = "/shops/{shopId}/templates/{id}/regions/{rid}/piecetemplates";
    private static final String REGION_TEMPLATE = "/shops/{shopId}/templates/{id}/regions/{rid}";
    private static final String REGION_TEMPLATES = "/shops/{shopId}/templates/{id}/regions";


    @BeforeAll
    public static void setup() {
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
        shopToken = jwtHelper.createToken(15L, "shop1", 1L, 1, 3600);
    }

    @Test
    public void createTemplate1() throws Exception{
        String body = "{\"name\":\"test\",\"defaultModel\":1}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(TEMPLATES,1L)
                    .header("authorization", adminToken)
                    .content(body.getBytes("utf-8"))
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.default", is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("test")))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void createTemplate2() throws Exception {
        String body = "{\"name\":\"test\",\"defaultModel\":0}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(TEMPLATES,1L)
                        .header("authorization", shopToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.CREATED.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("test")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.default", is(false)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }


    @Test
    public void retrieveTemplates1() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get(TEMPLATES,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("name", "计重模板")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("计重模板")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].default", is(true)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveTemplates2() throws Exception{
       this.mockMvc.perform(MockMvcRequestBuilders.get(TEMPLATES,1L)
                        .header("authorization", shopToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "2")
                        .param("pageSize", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("计件模板")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].default", is(false)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void retrieveTemplates3() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get(TEMPLATES,0)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("name", "")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].name", is("计重模板")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].default", is(true)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void cloneTemplate1() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.post(TEMPLATE_CLONE,0L,1L)
                        .header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.default", is(false)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void cloneTemplate2() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.post(TEMPLATE_CLONE,2L,1L)
                        .header("authorization", adminToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findTemplateById1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(TEMPLATE, 1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name", is("计重模板")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.defaultModel", is(1)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findTemplateById2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.get(TEMPLATE, 2L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateTemplateById1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"name\":\"test\",\"defaultModel\":0}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(TEMPLATE, 1L,1L)
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
    public void updateTemplateById2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"name\":\"test\",\"defaultModel\":0}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(TEMPLATE, 2L,1L)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateTemplateById3() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"name\":\"test\",\"defaultModel\":1}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(TEMPLATE, 1L,2L)
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
    public void createWeightTemplate2() throws Exception{
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"unit\":1,\"upperLimit\":4,\"firstWeight\":2, \"firstWeightFreight\":3,\"thresholds\":[{ \"below\": 1,\"price\":1}]}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(WEIGHT_TEMPLATE,1L,1L,247478L)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.FREIGHT_REGIONEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createWeightTemplate3() throws Exception{
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"unit\":1,\"upperLimit\":4,\"firstWeight\":2, \"firstWeightFreight\":3,\"thresholds\":[{ \"below\": 1,\"price\":1}]}";
        this.mockMvc.perform(MockMvcRequestBuilders.post(WEIGHT_TEMPLATE,2L,1L,1L)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateWeightTemplate1() throws Exception{
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"unit\":1,\"upperLimit\":4,\"firstWeight\":500, \"firstWeightFreight\":1000,\"thresholds\":[{\"below\":10000, \"price\":100}, {\"below\": 50000, \"price\": 50}, {\"below\": 100000, \"price\":10}, {\"below\": 300000, \"price\":5}, {\"below\": 500000, \"price\":0}]}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(WEIGHT_TEMPLATE,1L,1L,248059L)
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
    public void updateWeightTemplate2() throws Exception{
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"unit\":1,\"upperLimit\":4,\"firstWeight\":2, \"firstWeightFreight\":3,\"thresholds\":[{ \"below\": 1,\"price\":1}]}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(WEIGHT_TEMPLATE,1L,1L,666L)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateWeightTemplate3() throws Exception{
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"unit\":1,\"upperLimit\":4,\"firstWeight\":2, \"firstWeightFreight\":3,\"thresholds\":[{ \"below\": 1,\"price\":1}]}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(WEIGHT_TEMPLATE,2L,1L,1L)
                        .header("authorization", adminToken)
                        .content(body.getBytes("utf-8"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updatePieceTemplate1() throws Exception{
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        String body = "{\"unit\": 1,\"upperLimit\":4,\"firstItem\": 1,\"firstItemPrice\": 1000,\"additionalItems\": 2,\"additionalItemsPrice\": 100}";
        this.mockMvc.perform(MockMvcRequestBuilders.put(PIECE_TEMPLATE,1L,2L,0L)
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
    public void retrieveRegionTemplateById1() throws Exception{
        InternalReturnObject<Region> tmp=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region(666L,"xm"));
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(tmp);
        this.mockMvc.perform(MockMvcRequestBuilders.get(REGION_TEMPLATES,1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(107)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].creator.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].firstWeight", is(500)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].firstWeightFreight", is(1000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].thresholds[0].below", is(10000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].unit", is(100)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].region.id", is(666)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void retrieveRegionTemplateById2() throws Exception{
        InternalReturnObject<Region> tmp=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region(666L,"xm"));
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(tmp);
        this.mockMvc.perform(MockMvcRequestBuilders.get(REGION_TEMPLATES,1L,2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "2")
                        .param("pageSize", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].id", is(106)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].creator.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].firstItem", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].firstItemPrice", is(1000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].additionalItemsPrice", is(100)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].unit", is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.list[0].region.name", is("xm")))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void retrieveRegionTemplateById3() throws Exception{
        InternalReturnObject<Region> tmp=new InternalReturnObject<>(new Region(666L,"xm"));
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(tmp);
        this.mockMvc.perform(MockMvcRequestBuilders.get(REGION_TEMPLATES,2L,2L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("page", "1")
                        .param("pageSize", "1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteTemplate2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(TEMPLATE, 2L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void deleteRegionTemplate1() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(REGION_TEMPLATE, 1L,1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.RESOURCE_ID_NOTEXIST.getErrNo())))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void deleteRegionTemplate2() throws Exception{
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(REGION_TEMPLATE, 2L,1L,1L)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", Matchers.is(ReturnNo.RESOURCE_ID_OUTSCOPE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

}
