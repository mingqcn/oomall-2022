package cn.edu.xmu.oomall.shop.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.ShopApplication;
import cn.edu.xmu.oomall.shop.ShopTestApplication;
import cn.edu.xmu.oomall.shop.controller.vo.PieceTemplateVo;
import cn.edu.xmu.oomall.shop.controller.vo.RegionTemplateVo;
import cn.edu.xmu.oomall.shop.controller.vo.WeightTemplateVo;
import cn.edu.xmu.oomall.shop.dao.bo.template.Piece;
import cn.edu.xmu.oomall.shop.dao.bo.template.PieceTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.Weight;
import cn.edu.xmu.oomall.shop.dao.bo.template.WeightTemplate;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import cn.edu.xmu.oomall.shop.dao.template.PieceTemplateDao;
import cn.edu.xmu.oomall.shop.dao.template.RegionTemplateDao;
import cn.edu.xmu.oomall.shop.dao.template.TemplateDao;
import cn.edu.xmu.oomall.shop.dao.template.WeightTemplateDao;
import cn.edu.xmu.oomall.shop.mapper.PieceTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.RegionTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.WeightTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.PieceTemplatePo;
import cn.edu.xmu.oomall.shop.mapper.po.RegionTemplatePo;
import cn.edu.xmu.oomall.shop.mapper.po.WeightTemplatePo;
import cn.edu.xmu.oomall.shop.mapper.po.WeightThresholdPo;
import cn.edu.xmu.oomall.shop.service.dto.PieceTemplateDto;
import cn.edu.xmu.oomall.shop.service.dto.RegionTemplateDto;
import cn.edu.xmu.oomall.shop.service.dto.WeightTemplateDto;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
* @author Zhanyu Liu
* @date 2022/12/2 21:14
*/
@SpringBootTest(classes = ShopTestApplication.class)
@Transactional
public class RegionTemplateServiceTest {
    @Autowired
    RegionTemplateService regionTemplateService;

    @MockBean
    RocketMQTemplate rocketMQTemplate;
    @Autowired
    WeightTemplatePoMapper weightTemplatePoMapper;

    @Autowired
    PieceTemplatePoMapper pieceTemplatePoMapper;

    @Autowired
    RegionTemplatePoMapper regionTemplatePoMapper;


    @MockBean
    RedisUtil redisUtil;

    @MockBean
    FreightDao freightDao;
    UserDto user = new UserDto(1L, "test1", 0L, 1);

    @Test
    public void insertRegionTemplate1(){
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        PieceTemplate bo=new PieceTemplate();
        bo.setRegionId(999999L);
        bo.setTemplateId(2L);
        bo.setTemplateDao("pieceTemplateDao");
        bo.setAdditionalItems(1);
        bo.setAdditionalPrice(1L);
        bo.setFirstItems(1);
        bo.setFirstPrice(1L);
        bo.setUpperLimit(999);
        bo.setUnit(1);
        regionTemplateService.insertRegionTemplate(1L,bo,user);
        RegionTemplatePo po1=regionTemplatePoMapper.findByTemplateIdAndRegionId(2L,999999L).orElse(null);
        PieceTemplatePo po2=pieceTemplatePoMapper.findById(new ObjectId(po1.getObjectId())).orElse(null);
        assertThat(po1).isNotNull();
        assertThat(po2).isNotNull();
        assertThat(po1.getUnit()).isEqualTo(1);
        assertThat(po1.getUpperLimit()).isEqualTo(999);
        assertThat(po2.getFirstItems()).isEqualTo(1);
        assertThat(po2.getAdditionalPrice()).isEqualTo(1L);
        regionTemplateService.deleteRegionTemplate(1L,2L,999999L);
    }

    @Test
    public void insertRegionTemplate2(){
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        PieceTemplate bo=new PieceTemplate();
        bo.setRegionId(0L);
        bo.setTemplateId(2L);
        bo.setTemplateDao("pieceTemplateDao");
        bo.setAdditionalItems(2);
        bo.setAdditionalPrice(1L);
        bo.setFirstItems(1);
        bo.setFirstPrice(1L);
        try {
            regionTemplateService.insertRegionTemplate(1L,bo,user);
        }catch (BusinessException e){
            assertThat(e.getErrno()).isEqualTo(ReturnNo.FREIGHT_REGIONEXIST);
        }
    }

    @Test
    public void insertRegionTemplate3(){
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        WeightTemplate bo=new WeightTemplate();
        bo.setRegionId(666666L);
        bo.setTemplateId(1L);
        bo.setTemplateDao("weightTemplateDao");
        bo.setFirstWeight(1);
        bo.setFirstWeightPrice(1L);
        bo.setUpperLimit(66);
        bo.setUnit(1);
        List<WeightThresholdPo> poList=new ArrayList<>(){
            {
                add(new WeightThresholdPo(1,1L));
                add(new WeightThresholdPo(2,2L));
                add(new WeightThresholdPo(3,3L));
                add(new WeightThresholdPo(4,4L));
            }
        };
        bo.setThresholds(poList);
        regionTemplateService.insertRegionTemplate(1L,bo,user);
        regionTemplateService.deleteRegionTemplate(1L,1L,666666L);
    }

    @Test
    public void insertRegionTemplate4(){
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        PieceTemplate bo=new PieceTemplate();
        bo.setRegionId(999L);
        bo.setTemplateId(2L);
        bo.setTemplateDao("pieceTemplateDao");
        bo.setAdditionalItems(1);
        bo.setAdditionalPrice(1L);
        bo.setFirstItems(1);
        bo.setFirstPrice(1L);
        bo.setUpperLimit(999);
        bo.setUnit(1);
        regionTemplateService.insertRegionTemplate(1L,bo,user);
        RegionTemplatePo po1=regionTemplatePoMapper.findByTemplateIdAndRegionId(2L,999L).orElse(null);
        PieceTemplatePo po2=pieceTemplatePoMapper.findById(new ObjectId(po1.getObjectId())).orElse(null);
        assertThat(po1).isNotNull();
        assertThat(po2).isNotNull();
        assertThat(po1.getUnit()).isEqualTo(1);
        assertThat(po1.getUpperLimit()).isEqualTo(999);
        assertThat(po2.getFirstItems()).isEqualTo(1);
        assertThat(po2.getAdditionalPrice()).isEqualTo(1L);
        regionTemplateService.deleteRegionTemplate(1L,2L,999L);
    }

    @Test
    public void insertRegionTemplate5(){
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        WeightTemplate bo=new WeightTemplate();
        bo.setRegionId(247478L);
        bo.setTemplateId(1L);
        bo.setTemplateDao("weightTemplateDao");
        bo.setFirstWeight(1);
        bo.setFirstWeightPrice(1L);
        bo.setUpperLimit(11);
        List<WeightThresholdPo> poList=new ArrayList<>(){
            {
                add(new WeightThresholdPo(1,1L));
                add(new WeightThresholdPo(2,2L));
                add(new WeightThresholdPo(3,3L));
                add(new WeightThresholdPo(4,4L));
            }
        };
        bo.setThresholds(poList);
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        try {
            regionTemplateService.insertRegionTemplate(1L,bo,user);
        }catch (BusinessException e){
            assertThat(e.getErrno()).isEqualTo(ReturnNo.FREIGHT_REGIONEXIST);
        }
    }

    @Test
    public void saveRegionTemplate1(){
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        PieceTemplate bo=new PieceTemplate();
        bo.setRegionId(152L);
        bo.setTemplateId(2L);
        bo.setTemplateDao("pieceTemplateDao");
        bo.setAdditionalItems(1);
        bo.setAdditionalPrice(1L);
        bo.setFirstItems(1);
        bo.setFirstPrice(1L);
        try{
            regionTemplateService.saveRegionTemplate(2L,bo,user);
        }catch (BusinessException e){
            assertThat(e.getErrno()).isEqualTo(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
    }
    @Test
    public void saveRegionTemplate2(){
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        WeightTemplate bo=new WeightTemplate();
        bo.setRegionId(248059L);
        bo.setTemplateId(1L);
        bo.setTemplateDao("weightTemplateDao");
        bo.setFirstWeight(500);
        bo.setFirstWeightPrice(1000L);
        bo.setUpperLimit(11);
        List<WeightThresholdPo> poList=new ArrayList<>(){
            {
                add(new WeightThresholdPo(10000,100L));
                add(new WeightThresholdPo(50000,50L));
                add(new WeightThresholdPo(100000,10L));
                add(new WeightThresholdPo(300000,5L));
                add(new WeightThresholdPo(500000,0L));
            }
        };
        bo.setThresholds(poList);
        regionTemplateService.saveRegionTemplate(1L,bo,user);
    }

    @Test
    public void saveRegionTemplate3(){
        InternalReturnObject<Region> ret=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region());
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(ret);
        PieceTemplate bo=new PieceTemplate();
        bo.setRegionId(248059L);
        bo.setTemplateId(2L);
        bo.setTemplateDao("pieceTemplateDao");
        bo.setAdditionalItems(2);
        bo.setAdditionalPrice(100L);
        bo.setFirstItems(2);
        bo.setFirstPrice(500L);
        regionTemplateService.saveRegionTemplate(1L,bo,user);
        RegionTemplatePo po1=regionTemplatePoMapper.findByTemplateIdAndRegionId(2L,248059L).orElse(null);
        PieceTemplatePo po2=pieceTemplatePoMapper.findById(new ObjectId(po1.getObjectId())).orElse(null);
        assertThat(po1).isNotNull();
        assertThat(po2).isNotNull();
        assertThat(po1.getUnit()).isEqualTo(2);
        assertThat(po1.getUpperLimit()).isEqualTo(10);
        assertThat(po2.getFirstItems()).isEqualTo(2);
        assertThat(po2.getAdditionalPrice()).isEqualTo(100L);
    }

    @Test
    public void retrieveRegionTemplateById1(){
        InternalReturnObject<Region> tmp=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region(666L,"xm"));
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(tmp);
        WeightTemplatePo po1 = new WeightTemplatePo();

        po1.setObjectId(new ObjectId("63930f78d4f468435d9420fd"));
        po1.setFirstWeight(500);
        po1.setFirstWeightPrice(1000L);
        po1.setThresholds(new ArrayList<>(){
            {
                add(new WeightThresholdPo(10000,100L));
                add(new WeightThresholdPo(50000,50L));
                add(new WeightThresholdPo(100000,10L));
                add(new WeightThresholdPo(300000,5L));
                add(new WeightThresholdPo(500000,0L));
            }
        });
        weightTemplatePoMapper.save(po1);

        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        PageDto<? extends RegionTemplateDto> ret=regionTemplateService.retrieveRegionTemplateById(1L,1L,1,10);
        assertThat(ret.getList().get(0) instanceof WeightTemplateDto).isTrue();

        WeightTemplateDto dto=(WeightTemplateDto)ret.getList().get(0);
        assertThat(dto.getId()).isEqualTo(107L);
        assertThat(dto.getCreator().getName()).isEqualTo("admin");
        assertThat(dto.getUnit()).isEqualTo(100);
        assertThat(dto.getRegion().getId()).isEqualTo(666L);
        assertThat(dto.getFirstWeight()).isEqualTo(500);
        assertThat(dto.getThresholds().get(0).getBelow()).isEqualTo(10000);
        assertThat(dto.getThresholds().get(0).getPrice()).isEqualTo(100L);
        assertThat(dto.getFirstWeightPrice()).isEqualTo(1000L);
    }

    @Test
    public void retrieveRegionTemplateById2(){
        InternalReturnObject<Region> tmp=new InternalReturnObject<>(ReturnNo.OK.getErrNo(),ReturnNo.OK.getMessage(),new Region(666L,"xm"));
        Mockito.when(freightDao.findRegionById(Mockito.anyLong())).thenReturn(tmp);
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        PageDto<? extends RegionTemplateDto> ret=regionTemplateService.retrieveRegionTemplateById(1L,2L,2,1);
        assertThat(ret.getList().get(0).getId()).isEqualTo(106L);
        assertThat(ret.getList().get(0).getCreator().getName()).isEqualTo("admin");
        assertThat(ret.getList().get(0).getUnit()).isEqualTo(2);
        assertThat(ret.getList().get(0) instanceof PieceTemplateDto).isTrue();
        PieceTemplateDto dto=(PieceTemplateDto)ret.getList().get(0);
        assertThat(dto.getFirstItems()).isEqualTo(1);
        assertThat(dto.getAdditionalItems()).isEqualTo(2);
        assertThat(dto.getRegion().getName()).isEqualTo("xm");
    }

    @Test
    public void deleteRegionTemplate1(){
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        RegionTemplatePo po1=new RegionTemplatePo();
        po1.setRegionId(999999L);
        po1.setTemplateId(2L);
        po1.setObjectId("63930f78d4f468435d942102");
        po1.setUnit(1);
        po1.setUpperLimit(2);
        po1.setTemplateDao(TemplateDao.PIECE);
        LocalDateTime now=LocalDateTime.now();
        po1.setGmtCreate(now);
        regionTemplatePoMapper.save(po1);
        PieceTemplatePo po2=new PieceTemplatePo();
        po2.setObjectId(new ObjectId("63930f78d4f468435d942102"));
        po2.setFirstItems(1);
        pieceTemplatePoMapper.insert(po2);
        regionTemplateService.deleteRegionTemplate(1L,2L,999999L);
    }

}
