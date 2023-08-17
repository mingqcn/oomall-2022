package cn.edu.xmu.oomall.region.controller;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.javaee.core.util.JwtHelper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.region.RegionApplication;
import cn.edu.xmu.oomall.region.controller.vo.RegionVo;
import cn.edu.xmu.oomall.region.dao.RegionDao;
import cn.edu.xmu.oomall.region.dao.bo.Region;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

import javax.transaction.Transactional;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;

/**
 * 由于数据库的数据不满足所有代码覆盖的测试需要，
 * 这部分测试使用MockBean完成
 */
@SpringBootTest(classes = RegionApplication.class)
@AutoConfigureMockMvc
public class AdminRegionControllerMockTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RedisUtil redisUtil;
    @MockBean
    private RegionDao regionDao;
    private static String adminToken;
    private final String ADMIN_SUB_REGIONS = "/shops/{did}/regions/{id}/subregions";
    private final String ADMIN_REGIONS_ID = "/shops/{did}/regions/{id}";
    private final String ADMIN_REGIONS_ID_SUSPEND = "/shops/{did}/regions/{id}/suspend";
    private final String ADMIN_REGIONS_ID_RESUME = "/shops/{did}/regions/{id}/resume";

    @BeforeAll
    static void setUp() {
        JwtHelper jwtHelper = new JwtHelper();
        adminToken = jwtHelper.createToken(1L, "13088admin", 0L, 1, 3600);
    }

    @Test
    @Transactional
    void deleteRegionById2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        Region bo = new Region();
        bo.setId(4L);
        bo.setStatus(Region.ABANDONED);
        Mockito.when(regionDao.findById(Mockito.any())).thenReturn(bo);
        this.mockMvc.perform(MockMvcRequestBuilders.delete(ADMIN_REGIONS_ID, 0, 4)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.STATENOTALLOW.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void suspendRegionById2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        Region bo = new Region();
        bo.setId(4L);
        bo.setStatus(Region.ABANDONED);
        Mockito.when(regionDao.findById(Mockito.any())).thenReturn(bo);
        this.mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_REGIONS_ID_SUSPEND, 0, 4)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.STATENOTALLOW.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void resumeRegionById2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        Region bo = new Region();
        bo.setId(4L);
        bo.setStatus(Region.SUSPENDED);
        Mockito.when(regionDao.findById(Mockito.any())).thenReturn(bo);
        this.mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_REGIONS_ID_RESUME, 0, 4)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    void updateRegionById2() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        Region bo = new Region();
        bo.setId(4L);
        bo.setStatus(Region.ABANDONED);
        Mockito.when(regionDao.findById(Mockito.any())).thenReturn(bo);
        RegionVo vo = new RegionVo("东城区风云再起", "风云再起", "北京，东城，风云再起",
                "FengYunZaiQi", "116.416357", "39.928353", "110101000000", "00100000", "010");

        this.mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_REGIONS_ID, 0, 4)
                        .header("authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(Objects.requireNonNull(JacksonUtil.toJson(vo))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.REGION_ABANDONE.getErrNo())))
                .andDo(MockMvcResultHandlers.print());
    }
}
