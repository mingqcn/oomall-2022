package cn.edu.xmu.oomall.region.controller;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.region.RegionApplication;
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

import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.is;


@SpringBootTest(classes = RegionApplication.class)
@AutoConfigureMockMvc
class InternalRegionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RedisUtil redisUtil;
    private static final String PARENTS = "/internal/regions/{id}/parents";

    @Test
    @Transactional
    void getParentsRegions() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        this.mockMvc.perform(MockMvcRequestBuilders.get(PARENTS, 4)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errno", is(ReturnNo.OK.getErrNo())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id", is(3)))
                .andDo(MockMvcResultHandlers.print());
    }
}