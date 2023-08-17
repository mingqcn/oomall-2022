package cn.edu.xmu.oomall.shop.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.ShopApplication;
import cn.edu.xmu.oomall.shop.ShopTestApplication;
import cn.edu.xmu.oomall.shop.dao.bo.template.Template;
import cn.edu.xmu.oomall.shop.dao.template.RegionTemplateDao;
import cn.edu.xmu.oomall.shop.mapper.RegionTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.TemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.TemplatePo;
import com.github.pagehelper.PageInfo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
/**
* @author Zhanyu Liu
* @date 2022/12/2 21:11
*/
@SpringBootTest(classes = ShopTestApplication.class)

public class FreightTemplateDaoTest {

    @Autowired
    FreightTemplateDao freightTemplateDao;

    @MockBean
    RocketMQTemplate rocketMQTemplate;

    @Autowired
    TemplatePoMapper templatePoMapper;
    @MockBean
    RedisUtil redisUtil;

    @Test
    @Transactional
    public void retrieveTemplateByName1(){
        List<Template> ret=freightTemplateDao.retrieveTemplateByName(1L,"计重模板",4,10);
        assertThat(ret.isEmpty()).isEqualTo(true);

        ret=freightTemplateDao.retrieveTemplateByName(4L,"计重模板",4,10);
        assertThat(ret.isEmpty()).isEqualTo(true);
    }

    @Test
    @Transactional
    public void retrieveTemplateByName2(){
        List<Template> ret=freightTemplateDao.retrieveTemplateByName(1L,"",2,1);
        Template bo=ret.get(0);
        assertThat(bo.getName()).isEqualTo("计件模板");
        assertThat(bo.getId()).isEqualTo(2L);
        assertThat(bo.getDefaultModel()).isEqualTo((byte)0);
    }
    @Test
    @Transactional
    public void insertTemplate1(){
        Template bo=new Template();
        bo.setName("计件模板2");
        bo.setDefaultModel((byte) 0);
        bo.setShopId(1L);

        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");

        Template newBo=freightTemplateDao.insertTemplate(bo,user);
        assertThat(newBo.getId()).isNotNull();
        assertThat(newBo.getDefaultModel()).isEqualTo((byte)0);
        assertThat(newBo.getName()).isEqualTo("计件模板2");
        assertThat(newBo.getShopId()).isEqualTo(1L);
    }

    @Test
    @Transactional
    public void findTemplateById1(){

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        Template bo=freightTemplateDao.findTemplateById(1L);
        assertThat(bo.getId()).isEqualTo(1L);
        assertThat(bo.getName()).isEqualTo("计重模板");
        assertThat(bo.getShopId()).isEqualTo(1L);
    }

    @Test
    @Transactional
    public void findTemplateById2(){

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        try{
            Template bo=freightTemplateDao.findTemplateById(null);
        }
        catch (Exception e){
            assertThat(e instanceof BusinessException).isEqualTo(true);
        }

    }

    @Test
    @Transactional
    public void findTemplateById3(){

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        try{
            Template bo=freightTemplateDao.findTemplateById(4L);
        }
        catch (Exception e){
            assertThat(e instanceof BusinessException).isEqualTo(true);
        }
    }

    @Test
    @Transactional
    public void saveTemplateById1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        Template bo=new Template();
        bo.setId(1L);
        bo.setName("计件模板3");
        bo.setDefaultModel((byte) 0);
        bo.setShopId(1L);

        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");

        freightTemplateDao.saveTemplateById(bo, user);
        TemplatePo po=templatePoMapper.findById(1L).orElse(null);
        assertThat(po.getDefaultModel()).isEqualTo((byte)0);
        assertThat(po.getModifierId()).isEqualTo(2L);
        assertThat(po.getModifierName()).isEqualTo("test1");
    }

    @Test
    @Transactional
    public void saveTemplateById2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        Template bo=new Template();
        bo.setId(2L);
        bo.setDefaultModel((byte) 1);

        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");

        freightTemplateDao.saveTemplateById(bo, user);
        TemplatePo po=templatePoMapper.findById(2L).orElse(null);
        assertThat(po.getModifierId()).isEqualTo(2L);
        assertThat(po.getDefaultModel()).isEqualTo((byte)1);
        assertThat(po.getModifierName()).isEqualTo("test1");
        assertThat(po.getShopId()).isEqualTo(1L);
        assertThat(po.getName()).isEqualTo("计件模板");
    }


    @Test
    @Transactional
    public void findTemplateByShopIdAndDefaultModel1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        Optional<Template> ret=freightTemplateDao.retrieveTemplateByShopIdAndDefaultModel(1L);
        Template bo=ret.orElse(null);
        assertThat(bo.getShopId()).isEqualTo(1L);
        assertThat(bo.getDefaultModel()).isEqualTo((byte) 1);
    }

    @Test
    @Transactional
    public void findTemplateByShopIdAndDefaultModel2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        Template bo=new Template();
        bo.setId(1L);
        bo.setDefaultModel((byte) 0);

        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");

        freightTemplateDao.saveTemplateById(bo, user);

        Optional<Template> ret=freightTemplateDao.retrieveTemplateByShopIdAndDefaultModel(1L);
        bo=ret.orElse(null);
        assertThat(bo).isNull();
    }

}
