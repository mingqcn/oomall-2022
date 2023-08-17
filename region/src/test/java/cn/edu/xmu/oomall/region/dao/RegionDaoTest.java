package cn.edu.xmu.oomall.region.dao;

import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.region.RegionApplication;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = RegionApplication.class)
class RegionDaoTest {
    @Autowired
    private RegionDao regionDao;
    @MockBean
    private RedisUtil redisUtil;

    @Test
    void findById() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        assertNull(regionDao.findById(null));
    }

    @Test
    void retrieveSubRegionsById() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.get(Mockito.anyString())).thenReturn(null);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        assertNull(regionDao.retrieveSubRegionsById(null, 1, 10));
    }

    @Test
    void retrieveParentsRegionsById() {
        assertEquals(this.regionDao.retrieveParentsRegionsById(0L), new ArrayList<>());
    }
}