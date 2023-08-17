package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import cn.edu.xmu.oomall.goods.dao.bo.Shop;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.service.ActivityService;
import cn.edu.xmu.oomall.goods.service.OnsaleService;
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
public class OnsaleServiceTest2 {
    @Autowired
    private OnsaleService onsaleService;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    @Autowired
    private ShopDao shopDao;

    @Test
    public void getCouponActProductTest() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        Shop shop = Shop.builder().id(10L).name("kp小屋").type((byte) 1).build();
        InternalReturnObject<Shop> internalReturnObject = new InternalReturnObject<>();
        internalReturnObject.setErrno(0);
        internalReturnObject.setErrmsg("成功");
        internalReturnObject.setData(shop);
        Mockito.when(shopDao.getShopById(Mockito.anyLong())).thenReturn(internalReturnObject);
        assertThat(onsaleService.getCouponActProduct(11L, 1, 10).getList().size()).isEqualTo(1);
        assertThat(onsaleService.getCouponActProduct(11L, 1, 10).getList().get(0).getId()).isEqualTo(1);
    }
}