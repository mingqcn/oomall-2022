package cn.edu.xmu.oomall.shop.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.ShopApplication;
import cn.edu.xmu.oomall.shop.ShopTestApplication;
import cn.edu.xmu.oomall.shop.dao.bo.Shop;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author chenyz
 * @date 2022-11-27 13:05
 */
@SpringBootTest(classes = ShopTestApplication.class)
@Transactional
public class ShopDaoTest {

    @MockBean
    RedisUtil redisUtil;
    @MockBean
    RocketMQTemplate rocketMQTemplate;
    @Autowired
    ShopDao shopDao;

    @Test
    public void findById1(){
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setName("OOMALL自营商铺");
        shop.setDeposit(5000000L);
        shop.setDepositThreshold(1000000L);
        shop.setStatus(Shop.ONLINE);
        shop.setRegionId(10L);
        shop.setAddress("黄图岗南街112");
        shop.setType(Shop.RETAILER);
        shop.setFreeThreshold(0);

        Mockito.when(redisUtil.hasKey(String.format(ShopDao.KEY, 1))).thenReturn(true);
        Mockito.when(redisUtil.get(String.format(ShopDao.KEY, 1))).thenReturn(shop);

        Optional<Shop> ret = shopDao.findById(1L);
        Shop retShop = ret.orElse(null);

        assertThat(retShop.getId()).isEqualTo(Long.valueOf(1));
        assertThat(retShop.getName()).isEqualTo(shop.getName());
        assertThat(retShop.getStatus()).isEqualTo(shop.getStatus());
        assertThat(retShop.getRegionId()).isEqualTo(shop.getRegionId());
        assertThat(retShop.getAddress()).isEqualTo(shop.getAddress());
        assertThat(retShop.getType()).isEqualTo(shop.getType());
        assertThat(retShop.getFreeThreshold()).isEqualTo(shop.getFreeThreshold());
    }

    @Test
    public void findById2(){
        Mockito.when(redisUtil.hasKey(String.format(ShopDao.KEY, 1))).thenReturn(false);

        Optional<Shop> ret = shopDao.findById(1L);
        Shop retShop = ret.orElse(null);

        assertThat(retShop.getId()).isEqualTo(1L);
        assertThat(retShop.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(retShop.getStatus()).isEqualTo(Shop.ONLINE);
        assertThat(retShop.getRegionId()).isEqualTo(10L);
        assertThat(retShop.getAddress()).isEqualTo("黄图岗南街112");
        assertThat(retShop.getType()).isEqualTo(Shop.RETAILER);
    }

    @Test
    public void findById3(){
        Mockito.when(redisUtil.hasKey(String.format(ShopDao.KEY, 50))).thenReturn(false);
        assertThrows(BusinessException.class, ()-> shopDao.findById(50L));
    }

    @Test
    public void findByCreatorId(){
        PageDto<Shop> shopPageDto = shopDao.retrieveByCreatorId(1L, 0, 10);
        Shop shop1 = shopPageDto.getList().get(0);
        assertThat(shop1.getId()).isEqualTo(1L);
        assertThat(shop1.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(shop1.getRegionId()).isEqualTo(10L);
        assertThat(shop1.getAddress()).isEqualTo("黄图岗南街112");
        Shop shop2 = shopPageDto.getList().get(5);
        assertThat(shop2.getId()).isEqualTo(6L);
        assertThat(shop2.getName()).isEqualTo("一口气");
        assertThat(shop2.getRegionId()).isEqualTo(161817L);
        assertThat(shop2.getAddress()).isEqualTo("北京路100号");
    }

    @Test
    public void insert(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);
        Shop shop = new Shop();
        shop.setName("TestShop");
        shop.setRegionId(10L);
        shop.setAddress("黄图岗南街110");
        shop.setConsignee("test");
        shop.setMobile("123");
        shop.setType(Shop.RETAILER);
        shop.setFreeThreshold(0);

        Shop retShop = shopDao.insert(shop, user);
        assertThat(retShop.getName()).isEqualTo(shop.getName());
        assertThat(retShop.getRegionId()).isEqualTo(shop.getRegionId());
        assertThat(retShop.getAddress()).isEqualTo(shop.getAddress());
        assertThat(retShop.getType()).isEqualTo(shop.getType());
        assertThat(retShop.getFreeThreshold()).isEqualTo(shop.getFreeThreshold());
    }

    @Test
    public void save(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);
        Shop shop = new Shop();
        shop.setId(1L);
        shop.setConsignee("test");
        shop.setMobile("123");
        shop.setFreeThreshold(3);

        Set<String> delKeys = shopDao.save(shop, user);
        assertThat(delKeys.contains(String.format(ShopDao.KEY, shop.getId()))).isTrue();
    }

    @Test
    public void retrieveValidByTypeAndName1(){
        List<Shop> list = shopDao.retrieveValidByTypeAndName(null, null, 1, 10).getList();
        assertThat(list.size()).isEqualTo(10);
        list.forEach(dto -> assertThat(1 == dto.getStatus() || 2 == dto.getStatus()).isTrue());
        Shop bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(bo.getStatus()).isEqualTo(Shop.ONLINE);
        assertThat(bo.getAddress()).isEqualTo("黄图岗南街112");
    }

    @Test
    public void retrieveValidByTypeAndName2(){
        List<Shop> list = shopDao.retrieveValidByTypeAndName(Shop.RETAILER, null, 1, 10).getList();
        assertThat(list.size()).isEqualTo(7);
        list.forEach(dto -> assertThat(dto.getType()).isEqualTo(Shop.RETAILER));
        Shop bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(bo.getAddress()).isEqualTo("黄图岗南街112");
    }

    @Test
    public void retrieveValidByTypeAndName3(){
        List<Shop> list = shopDao.retrieveValidByTypeAndName(Shop.SERVICE, null, 1, 10).getList();
        assertThat(list.size()).isEqualTo(3);
        Shop bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(45L);
        assertThat(bo.getName()).isEqualTo("服务商1");
        assertThat(bo.getAddress()).isEqualTo("郑州日报社");
        assertThat(bo.getConsignee()).isEqualTo("书孤");
        assertThat(bo.getMobile()).isEqualTo("2213233");
    }

    @Test
    public void retrieveValidByTypeAndName4(){
        List<Shop> list = shopDao.retrieveValidByTypeAndName(Shop.RETAILER, "OOMALL自营商铺", 1, 10).getList();
        assertThat(list.size()).isEqualTo(1);
        Shop bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(bo.getType()).isEqualTo(Shop.RETAILER);
        assertThat(bo.getRegionId()).isEqualTo(10L);
        assertThat(bo.getAddress()).isEqualTo("黄图岗南街112");
    }

    @Test
    public void retrieveValidByTypeAndName5(){
        List<Shop> list = shopDao.retrieveValidByTypeAndName(null, "OOMALL自营商铺", 1, 10).getList();
        assertThat(list.size()).isEqualTo(1);
        list.forEach(dto -> assertThat(1 == dto.getStatus() || 2 == dto.getStatus()).isTrue());
        Shop bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(bo.getStatus()).isEqualTo(Shop.ONLINE);
        assertThat(bo.getAddress()).isEqualTo("黄图岗南街112");
    }

    @Test void retrieveByTypeAndStatusAndName1(){
        List<Shop> list = shopDao.retrieveByTypeAndStatusAndName(null, Shop.ONLINE, null, 1, 10).getList();
        assertThat(list.size()).isEqualTo(5);
        Shop bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(bo.getType()).isEqualTo(Shop.RETAILER);
        assertThat(bo.getRegionId()).isEqualTo(10L);
        assertThat(bo.getAddress()).isEqualTo("黄图岗南街112");
    }

    @Test void retrieveByTypeAndStatusAndName2(){
        List<Shop> list = shopDao.retrieveByTypeAndStatusAndName(Shop.SERVICE, null, null, 1, 10).getList();
        assertThat(list.size()).isEqualTo(4);
        Shop bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(45L);
        assertThat(bo.getName()).isEqualTo("服务商1");
        assertThat(bo.getType()).isEqualTo(Shop.SERVICE);
        assertThat(bo.getRegionId()).isEqualTo(367413L);
        assertThat(bo.getAddress()).isEqualTo("郑州日报社");
    }

    @Test void retrieveByTypeAndStatusAndName3(){
        List<Shop> list = shopDao.retrieveByTypeAndStatusAndName(null, null, "OOMALL自营商铺", 1, 10).getList();
        assertThat(list.size()).isEqualTo(1);
        Shop bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(bo.getType()).isEqualTo(Shop.RETAILER);
        assertThat(bo.getRegionId()).isEqualTo(10L);
        assertThat(bo.getAddress()).isEqualTo("黄图岗南街112");
    }

    @Test void retrieveByTypeAndStatusAndName4(){
        List<Shop> list = shopDao.retrieveByTypeAndStatusAndName(Shop.RETAILER, null, "OOMALL自营商铺", 1, 10).getList();
        assertThat(list.size()).isEqualTo(1);
        Shop bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(bo.getType()).isEqualTo(Shop.RETAILER);
        assertThat(bo.getRegionId()).isEqualTo(10L);
        assertThat(bo.getAddress()).isEqualTo("黄图岗南街112");
    }

    @Test void retrieveByTypeAndStatusAndName5(){
        List<Shop> list = shopDao.retrieveByTypeAndStatusAndName(Shop.RETAILER, Shop.ONLINE, null, 1, 10).getList();
        assertThat(list.size()).isEqualTo(4);
        Shop bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(bo.getType()).isEqualTo(Shop.RETAILER);
        assertThat(bo.getRegionId()).isEqualTo(10L);
        assertThat(bo.getAddress()).isEqualTo("黄图岗南街112");
    }

    @Test void retrieveByTypeAndStatusAndName6(){
        List<Shop> list = shopDao.retrieveByTypeAndStatusAndName(null, Shop.ONLINE, "OOMALL自营商铺", 1, 10).getList();
        assertThat(list.size()).isEqualTo(1);
        Shop bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(bo.getType()).isEqualTo(Shop.RETAILER);
        assertThat(bo.getRegionId()).isEqualTo(10L);
        assertThat(bo.getAddress()).isEqualTo("黄图岗南街112");
    }

    @Test void retrieveByTypeAndStatusAndName7(){
        List<Shop> list = shopDao.retrieveByTypeAndStatusAndName(Shop.RETAILER, Shop.ONLINE, "OOMALL自营商铺", 1, 10).getList();
        assertThat(list.size()).isEqualTo(1);
        Shop bo = list.get(0);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("OOMALL自营商铺");
        assertThat(bo.getType()).isEqualTo(Shop.RETAILER);
        assertThat(bo.getStatus()).isEqualTo(Shop.ONLINE);
        assertThat(bo.getRegionId()).isEqualTo(10L);
        assertThat(bo.getAddress()).isEqualTo("黄图岗南街112");
    }

    @Test void retrieveByTypeAndStatusAndName8(){
        List<Shop> list = shopDao.retrieveByTypeAndStatusAndName(Shop.RETAILER, Shop.OFFLINE, "OOMALL自营商铺", 1, 10).getList();
        assertThat(list.isEmpty()).isTrue();
    }

}
