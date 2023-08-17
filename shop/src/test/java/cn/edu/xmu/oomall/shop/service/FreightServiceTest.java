package cn.edu.xmu.oomall.shop.service;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.ShopTestApplication;
import cn.edu.xmu.oomall.shop.controller.vo.FreightPriceVo;
import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
* @author Zhanyu Liu
* @date 2022/12/2 21:12
*/
@SpringBootTest(classes = ShopTestApplication.class)
@Transactional
public class FreightServiceTest {
    @Autowired
    FreightService freightService;

    @MockBean
    RocketMQTemplate rocketMQTemplate;

    @MockBean
    FreightDao freightDao;

    @MockBean
    RedisUtil redisUtil;
    @Test
    public void getFreight1(){
        List<ProductItem> items = new ArrayList<>(){
            {
                add(new ProductItem(1L,1L,100L,1,1L,1));
                add(new ProductItem(2L,2L,100L,2,1L,2));
                add(new ProductItem(3L,3L,100L,3,1L,3));
                add(new ProductItem(4L,4L,100L,4,1L,4));
                add(new ProductItem(5L,5L,100L,5,1L,3));
                add(new ProductItem(6L,6L,100L,6,1L,2));
                add(new ProductItem(7L,7L,100L,7,1L,1));
            }
        };

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        InternalReturnObject<List<Region>> tmp=new InternalReturnObject<>(new ArrayList<>(){
            {
                add(new Region(0L,"cn"));
            }
        });

        Mockito.when(freightDao.retrieveParentRegionsById(248059L)).thenReturn(tmp);

        FreightPriceVo results = freightService.getFreight(items,2L,248059L);
        assertNotNull(results);
        assertEquals(16, results.getPack().stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertEquals(7, results.getPack().size());
        assertEquals(1600, results.getFreightPrice());
    }

    @Test
    public void getFreight2(){
        List<ProductItem> items = new ArrayList<>(){
            {
                add(new ProductItem(1L,1L,100L,1,1L,1));
                add(new ProductItem(2L,2L,100L,2,1L,2));
                add(new ProductItem(3L,3L,100L,3,1L,3));
                add(new ProductItem(4L,4L,100L,4,1L,4));
                add(new ProductItem(5L,5L,100L,5,1L,3));
                add(new ProductItem(6L,6L,100L,6,1L,2));
                add(new ProductItem(7L,7L,100L,7,1L,1));
            }
        };

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        InternalReturnObject<List<Region>> tmp=new InternalReturnObject<>(new ArrayList<>(){
            {
                add(new Region(999L,"xmu"));
                add(new Region(248059L,"xm"));
            }
        });

        Mockito.when(freightDao.retrieveParentRegionsById(9999999L)).thenReturn(tmp);

        FreightPriceVo results = freightService.getFreight(items,2L,9999999L);
        assertNotNull(results);
        assertEquals(16, results.getPack().stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertEquals(7, results.getPack().size());
        assertEquals(1600, results.getFreightPrice());
    }
}
