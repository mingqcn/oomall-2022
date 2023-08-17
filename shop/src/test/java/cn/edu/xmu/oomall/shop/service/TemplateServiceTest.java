package cn.edu.xmu.oomall.shop.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.ShopApplication;
import cn.edu.xmu.oomall.shop.ShopTestApplication;
import cn.edu.xmu.oomall.shop.controller.vo.TemplateModifyVo;
import cn.edu.xmu.oomall.shop.controller.vo.TemplateVo;
import cn.edu.xmu.oomall.shop.dao.FreightTemplateDao;
import cn.edu.xmu.oomall.shop.dao.bo.template.Template;
import cn.edu.xmu.oomall.shop.dao.template.RegionTemplateDao;
import cn.edu.xmu.oomall.shop.dao.template.TemplateDao;
import cn.edu.xmu.oomall.shop.mapper.PieceTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.RegionTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.TemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.PieceTemplatePo;
import cn.edu.xmu.oomall.shop.mapper.po.RegionTemplatePo;
import cn.edu.xmu.oomall.shop.mapper.po.TemplatePo;
import cn.edu.xmu.oomall.shop.service.dto.TemplateRetDto;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
/**
* @author Zhanyu Liu
* @date 2022/12/2 21:13
*/
@SpringBootTest(classes = ShopTestApplication.class)
@Transactional
public class TemplateServiceTest {
    @Autowired
    TemplateService templateService;

    @MockBean
    RocketMQTemplate rocketMQTemplate;
    @Autowired
    TemplatePoMapper templatePoMapper;

    @Autowired
    RegionTemplatePoMapper regionTemplatePoMapper;

    @Autowired
    PieceTemplatePoMapper pieceTemplatePoMapper;

    @MockBean
    RedisUtil redisUtil;

    UserDto user = new UserDto(1L, "test1", 0L, 1);

    @Test
    public void cloneTemplate1() throws CloneNotSupportedException {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        TemplateVo vo=templateService.cloneTemplate(1L,1L,user);
        assertThat(vo.getId()).isNotEqualTo(1L);
        assertThat(vo.getDefaultModel()).isEqualTo(false);
        assertThat(vo.getName().substring(0,4)).isEqualTo("计重模板");
    }

    @Test
    public void cloneTemplate2() throws CloneNotSupportedException {
        String name="123434567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678912341";

        TemplatePo po=new TemplatePo();
        po.setId(1L);
        po.setShopId(2L);
        po.setName(name);
        po.setDefaultModel(Template.COMMON);
        templatePoMapper.save(po);
        Optional<TemplatePo> template= templatePoMapper.findById(po.getId());
        TemplatePo tmp=template.orElse(null);
        assertThat(tmp.getId()).isEqualTo(po.getId());
        assertThat(tmp.getDefaultModel()).isEqualTo(Template.COMMON);
        assertThat(tmp.getName().substring(0,4)).isEqualTo("1234");

        TemplateVo newvo=templateService.cloneTemplate(1L,2L,user);
        assertThat(newvo.getId()).isNotEqualTo(po.getId());
        assertThat(newvo.getDefaultModel()).isEqualTo(false);
        assertThat(newvo.getName().substring(0,4)).isEqualTo("1234");
    }
    @Test
    public void updateTemplateById1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        TemplateModifyVo vo=new TemplateModifyVo();
        vo.setName("test2");
        vo.setDefaultModel((byte)1);
        templateService.updateTemplateById(1L,1L,vo,user);

        TemplatePo tmp=templatePoMapper.findById(1L).orElse(null);

        vo.setDefaultModel((byte)0);
        templateService.updateTemplateById(1L,1L,vo,user);

        vo.setDefaultModel((byte)1);
        templateService.updateTemplateById(1L,1L,vo,user);
    }

    @Test
    public void updateTemplateById2(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        TemplateModifyVo vo=new TemplateModifyVo();
        vo.setName("test2");
        vo.setDefaultModel((byte)1);
        try{
            templateService.updateTemplateById(2L,1L,vo,user);
        }catch (BusinessException e){
            assertThat(e.getErrno()).isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
    }

    @Test
    public void updateTemplateById3(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        TemplateModifyVo vo=new TemplateModifyVo();
        vo.setName("test2");
        vo.setDefaultModel((byte)1);
        templateService.updateTemplateById(1L,2L,vo,user);
    }

    @Test
    public void findTemplateById1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        TemplateRetDto dto=templateService.findTemplateById(1L,1L);
        assertThat(dto.getName()).isEqualTo("计重模板");
        assertThat(dto.getDefaultModel()).isEqualTo((byte)1);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCreator().getName()).isEqualTo("admin11");
    }

    @Test
    public void retrieveTemplateByName1(){
        PageDto<TemplateVo> ret=templateService.retrieveTemplateByName(1L,"计重模板",1,10);
        TemplateVo vo=ret.getList().get(0);
        assertThat(vo.getId()).isEqualTo(1);
        assertThat(vo.getName()).isEqualTo("计重模板");
    }

    @Test
    public void retrieveTemplateByName2(){
        PageDto<TemplateVo> ret=templateService.retrieveTemplateByName(1L,"",2,1);
        TemplateVo vo=ret.getList().get(0);
        assertThat(vo.getId()).isEqualTo(2);
        assertThat(vo.getName()).isEqualTo("计件模板");
    }

    @Test
    public void deleteTemplate(){
        TemplatePo po=new TemplatePo();
        LocalDateTime now=LocalDateTime.now();
        po.setGmtCreate(now);
        po.setShopId(1L);
        po.setName("test");
        po=templatePoMapper.save(po);
        RegionTemplatePo po1=new RegionTemplatePo();
        po1.setRegionId(999999L);
        po1.setTemplateId(po.getId());
        po1.setObjectId("63930f78d4f468435d942103");
        po1.setUnit(1);
        po1.setUpperLimit(2);
        po1.setGmtCreate(now);
        po1.setTemplateDao(TemplateDao.PIECE);
        regionTemplatePoMapper.save(po1);
        PieceTemplatePo po2=new PieceTemplatePo();
        po2.setObjectId(new ObjectId("63930f78d4f468435d942103"));
        po2.setFirstItems(1);
        pieceTemplatePoMapper.insert(po2);
        RegionTemplatePo po3=new RegionTemplatePo();
        po3.setRegionId(99999999L);
        po3.setTemplateId(po.getId());
        po3.setObjectId("63930f78d4f468435d942104");
        po3.setUnit(1);
        po3.setUpperLimit(2);
        po3.setGmtCreate(now);
        po3.setTemplateDao(TemplateDao.PIECE);
        regionTemplatePoMapper.save(po3);
        PieceTemplatePo po4=new PieceTemplatePo();
        po4.setObjectId(new ObjectId("63930f78d4f468435d942104"));
        po4.setFirstItems(1);
        pieceTemplatePoMapper.insert(po4);

        templateService.deleteTemplate(po.getId());
    }
}
