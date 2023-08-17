//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.goods.GoodsApplication;
import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import cn.edu.xmu.oomall.goods.dao.bo.Product;
import cn.edu.xmu.oomall.goods.dao.bo.Shop;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = GoodsTestApplication.class)
@Transactional
public class ProductDaoTest {

    @Autowired
    private ProductDao productDao;

    @MockBean
    ShopDao shopDao;

    @Test
    public void findProbuctByBeginEnd1(){

        Shop shop = Shop.builder().id(1L).name("测试商铺1").build();
        Mockito.when(shopDao.getShopById(1L)).thenReturn(new InternalReturnObject<>(shop));


        Product product = productDao.findProductByBeginEnd(1561L, LocalDateTime.parse("2021-10-10T12:12:12"),LocalDateTime.parse("2021-10-11T12:12:12") );
        assertNull(product.getValidOnsale());

        product = productDao.findProductByBeginEnd(1561L, LocalDateTime.parse("2021-10-10T12:12:12"),LocalDateTime.parse("2021-12-11T12:12:12") );
        assertNotNull(product.getValidOnsale());
        assertEquals(12L, product.getValidOnsale().getId());

        product = productDao.findProductByBeginEnd(1561L, LocalDateTime.parse("2021-12-10T12:12:12"),LocalDateTime.parse("2022-12-11T12:12:12") );
        assertNotNull(product.getValidOnsale());
        assertEquals(12L, product.getValidOnsale().getId());

        product = productDao.findProductByBeginEnd(1561L, LocalDateTime.parse("2021-12-10T12:12:12"),LocalDateTime.parse("2021-12-11T12:12:12") );
        assertNotNull(product.getValidOnsale());
        assertEquals(12L, product.getValidOnsale().getId());

        product = productDao.findProductByBeginEnd(1561L, LocalDateTime.parse("2022-12-10T12:12:12"),LocalDateTime.parse("2022-12-11T12:12:12") );
        assertNull(product.getValidOnsale());
    }
}
