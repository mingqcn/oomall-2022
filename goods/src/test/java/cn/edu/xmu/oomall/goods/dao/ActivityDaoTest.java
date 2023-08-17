package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.GoodsApplication;
import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import cn.edu.xmu.oomall.goods.dao.activity.ActivityDao;
import cn.edu.xmu.oomall.goods.dao.bo.Onsale;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Liang Nan
 */
@SpringBootTest(classes = GoodsTestApplication.class)
@Transactional
public class ActivityDaoTest {
    @Autowired
    ActivityDao activityDao;

    @Autowired
    OnsaleDao onsaleDao;

    @MockBean
    private RedisUtil redisUtil;

    @Test
    public void delActivityOnsaleByActIdTest1() {
        activityDao.delActivityOnsaleByActId(12L);
    }

    @Test
    public void delActivityOnsaleByActIdTest2() {
        assertThrows(BusinessException.class, () -> activityDao.delActivityOnsaleByActId(18L));
    }

    @Test
    public void addActivityOnsaleTest() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);
        Onsale onsale = this.onsaleDao.findById(10L);
        activityDao.addActivityOnsale(11L,onsale, user);
    }

    //预售与优惠和团购活动不能并存，出215错误
    @Test
    public void addActivityOnsaleTest2() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);
        Onsale onsale = this.onsaleDao.findById(29L);
        assertThrows(BusinessException.class, () -> activityDao.addActivityOnsale(11L, onsale, user)).getErrno().equals(ReturnNo.ADVSALE_NOTCOEXIST);
    }

    //团购与优惠和预售活动不并存 出216错误
    @Test
    public void addActivityOnsaleTest3() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);
        Onsale onsale = this.onsaleDao.findById(3L);
        assertThrows(BusinessException.class, () -> activityDao.addActivityOnsale(11L, onsale, user)).getErrno().equals(ReturnNo.GROUPON_NOTCOEXIST);
    }

}