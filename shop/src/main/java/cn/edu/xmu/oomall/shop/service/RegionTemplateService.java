package cn.edu.xmu.oomall.shop.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.controller.vo.PieceTemplateVo;
import cn.edu.xmu.oomall.shop.controller.vo.RegionTemplateVo;
import cn.edu.xmu.oomall.shop.controller.vo.WeightTemplateVo;
import cn.edu.xmu.oomall.shop.dao.FreightTemplateDao;
import cn.edu.xmu.oomall.shop.dao.bo.template.*;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import cn.edu.xmu.oomall.shop.dao.template.RegionTemplateDao;
import cn.edu.xmu.oomall.shop.dao.template.TemplateDao;
import cn.edu.xmu.oomall.shop.service.dto.PieceTemplateDto;
import cn.edu.xmu.oomall.shop.service.dto.RegionTemplateDto;
import cn.edu.xmu.oomall.shop.service.dto.WeightTemplateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.util.Common.*;

@Service
public class RegionTemplateService {
    private final Logger logger = LoggerFactory.getLogger(RegionTemplateService.class);

    private RedisUtil redisUtil;
    private FreightTemplateDao freightTemplateDao;

    private RegionTemplateDao regionTemplateDao;

    private ApplicationContext context;

    private FreightDao freightDao;
    @Autowired
    public RegionTemplateService(
            FreightTemplateDao freightTemplateDao,
            RegionTemplateDao regionTemplateDao,
            RedisUtil redisUtil,
            ApplicationContext context,
            FreightDao freightDao
    ){
        this.freightTemplateDao = freightTemplateDao;
        this.regionTemplateDao=regionTemplateDao;
        this.redisUtil=redisUtil;
        this.context=context;
        this.freightDao=freightDao;
    }
    /**
     * 管理员定义或者修改重量或件数模板明细
     * @author Zhanyu Liu
     * @date 2022/11/30 8:04
     * @param shopId
     * @param bo
     * @param user
     */
    @Transactional
    public void saveRegionTemplate(Long shopId,RegionTemplate bo, UserDto user){
        InternalReturnObject<Region> ret=this.freightDao.findRegionById(bo.getRegionId());
        if (ReturnNo.OK != ReturnNo.getByCode(ret.getErrno())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        Template tmp=freightTemplateDao.findTemplateById(bo.getTemplateId());
        if(shopId!=PLATFORM&&shopId!=tmp.getShopId())
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "模板", bo.getTemplateId(),shopId));

        Optional<RegionTemplate> regionTemplate=regionTemplateDao.retrieveByTemplateIdAndRegionId(bo.getTemplateId(),bo.getRegionId());
        RegionTemplate oldBo=null;
        if(regionTemplate.isPresent()){
            oldBo=regionTemplate.get();
        }else{
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        logger.debug("saveRegionTemplate: oldBo={}",oldBo);
        bo.setId(oldBo.getId());
        bo.setObjectId(oldBo.getObjectId());
        logger.debug("saveRegionTemplate: newBo={}",bo);
        regionTemplateDao.save(bo,user);
    }

    /**
     * 管理员定义重量或件数模板明细
     * @author Zhanyu Liu
     * @date 2022/11/30 8:04
     * @param shopId
     * @param bo
     * @param user
     */
    @Transactional
    public void insertRegionTemplate(Long shopId,RegionTemplate bo, UserDto user){
        logger.debug("insertRegionTemplate: bo={},user={}",bo,user);
        InternalReturnObject<Region> ret=this.freightDao.findRegionById(bo.getRegionId());
        if (ReturnNo.OK != ReturnNo.getByCode(ret.getErrno())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        Template tmp=freightTemplateDao.findTemplateById(bo.getTemplateId());
        if(shopId!=PLATFORM&&shopId!=tmp.getShopId())
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "模板",bo.getTemplateId(), shopId));

        Optional<RegionTemplate> regionTemplate=regionTemplateDao.retrieveByTemplateIdAndRegionId(bo.getTemplateId(),bo.getRegionId());
        if(regionTemplate.isPresent()) {
            throw new BusinessException(ReturnNo.FREIGHT_REGIONEXIST, String.format(ReturnNo.FREIGHT_REGIONEXIST.getMessage(), bo.getRegionId()));
        }
        regionTemplateDao.insert(bo,user);
    }

    /**
     * 管理员删除地区模板
     * @author Zhanyu Liu
     * @date 2022/11/30 8:07
     * @param shopId
     * @param id  模板id
     * @param rid 地区id
     */
    @Transactional
    public void deleteRegionTemplate(Long shopId,Long id,Long rid){
        Template template=freightTemplateDao.findTemplateById(id);
        if(shopId!=PLATFORM&&shopId!=template.getShopId())
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "模板", id, shopId));

        List<String> delKeys=regionTemplateDao.delRegionByTemplateIdAndRegionId(id,rid);
        delKeys.forEach(key->{if(redisUtil.hasKey(key))redisUtil.del(key);});
    }

    /**
     * 店家或管理员查询运费模板下属所有地区模板明细
     * @author Zhanyu Liu
     * @date 2022/11/30 7:38
     * @param shopId
     * @param id
     * @param page
     * @param pageSize
     */
    public PageDto<? extends RegionTemplateDto> retrieveRegionTemplateById(Long shopId, Long id, Integer page, Integer pageSize){
        Template template=freightTemplateDao.findTemplateById(id);
        if(shopId!=PLATFORM&&shopId!=template.getShopId())
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "模板", id, shopId));

        List<RegionTemplate> ret=regionTemplateDao.retrieveByTemplateId(id,page,pageSize);
        logger.debug("RegionTemplateService:retrieveRegionTemplateById, ret={}",ret);
        if(ret.isEmpty())
            return new PageDto<>(new ArrayList<>(),page,pageSize);

        PageDto<? extends RegionTemplateDto> pageObj=null;
        List<? extends RegionTemplateDto> voList=ret.stream().map(bo->getDto(bo)).collect(Collectors.toList());
        pageObj = new PageDto<>(voList,page,pageSize);
        logger.debug("RegionTemplateService:retrieveRegionTemplateById, voList={},page={},pageSize={}",pageObj.getList(),pageObj.getPage(),pageObj.getPageSize());
        return pageObj;
    }

    public RegionTemplateDto getDto(RegionTemplate bo){
        TemplateDao templateDao=(TemplateDao) context.getBean(bo.getTemplateDao());
        return templateDao.getRegionTemplateDto(bo);
    }
}
