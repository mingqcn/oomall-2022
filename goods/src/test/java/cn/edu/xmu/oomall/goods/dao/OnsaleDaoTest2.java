package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.javaee.core.util.RedisUtil;

import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import cn.edu.xmu.oomall.goods.dao.bo.Onsale;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * @author Liang Nan
 */
@SpringBootTest(classes = GoodsTestApplication.class)
@Transactional
public class OnsaleDaoTest2 {

    @Autowired
    private OnsaleDao onsaleDao;

    @MockBean
    private RedisUtil redisUtil;


    @Test
    public void retrieveByActIdTest1() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        List<Onsale> ret = onsaleDao.retrieveByActId(11L,0,10);
        assertThat(ret.size()).isEqualTo(2);
    }

    @Test
    public void retrieveByActIdTest2() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        List<Onsale> ret = onsaleDao.retrieveByActId(15L,0,10);
        assertThat(ret.size()).isEqualTo(0);
    }
}