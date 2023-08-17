package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.shop.controller.vo.*;
import cn.edu.xmu.oomall.shop.dao.bo.template.PieceTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.WeightTemplate;
import cn.edu.xmu.oomall.shop.dao.template.TemplateDao;
import cn.edu.xmu.oomall.shop.service.RegionTemplateService;
import cn.edu.xmu.oomall.shop.service.TemplateService;
import cn.edu.xmu.oomall.shop.service.dto.PieceTemplateDto;
import cn.edu.xmu.oomall.shop.service.dto.RegionTemplateDto;
import cn.edu.xmu.oomall.shop.service.dto.WeightTemplateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@RestController
@RequestMapping(value = "/shops/{shopId}/templates", produces = "application/json;charset=UTF-8")
public class TemplateController {
    private final Logger logger = LoggerFactory.getLogger(TemplateController.class);

    private TemplateService templateService;

    private RegionTemplateService regionTemplateService;

    @Autowired
    public TemplateController(TemplateService templateService, RegionTemplateService regionTemplateService){
        this.templateService=templateService;
        this.regionTemplateService=regionTemplateService;
    }

    /**
     * 管理员定义运费模板
     */
    @Audit(departName = "shops")
    @PostMapping("")
    public ReturnObject createTemplate(
            @PathVariable("shopId")Long shopId,
            @Validated @RequestBody TemplateModifyVo vo,
            @LoginUser UserDto user
    ){
        TemplateVo ret =templateService.createTemplate(shopId,vo,user);
        return new ReturnObject(ReturnNo.CREATED,ReturnNo.CREATED.getMessage(),ret);
    }

    /**
    * 获得商品的运费模板
    */
    @Audit(departName = "shops")
    @GetMapping("")
    public ReturnObject retrieveTemplateByName(
            @PathVariable("shopId")Long shopId,
            @RequestParam(required = false,defaultValue = "")String name,
            @RequestParam(required = false,defaultValue = "1")Integer page,
            @RequestParam(required = false,defaultValue = "10")Integer pageSize
    ){
        PageDto<TemplateVo> pageDto=this.templateService.retrieveTemplateByName(shopId,name,page,pageSize);
        return new ReturnObject(pageDto);
    }

    /**
    * 管理员克隆运费模板
    */
    @Audit(departName = "shops")
    @PostMapping("/{id}/clone")
    public ReturnObject cloneTemplate(
            @PathVariable("shopId")Long shopId,
            @PathVariable("id")Long id,
            @LoginUser UserDto user
    ) {
        TemplateVo ret=templateService.cloneTemplate(id,shopId,user);
        return new ReturnObject(ReturnNo.CREATED,ReturnNo.CREATED.getMessage(),ret);
    }
    
    /**
    * 获得运费模板详情
    */
    @Audit(departName = "shops")
    @GetMapping("/{id}")
    public ReturnObject findTemplateById(
            @PathVariable("shopId")Long shopId,
            @PathVariable("id")Long id
    ){
        return new ReturnObject(templateService.findTemplateById(shopId,id));
    }

    /**
    * 管理员修改运费模板
    */
    @Audit(departName = "shops")
    @PutMapping("/{id}")
    public ReturnObject updateTemplateById(
            @PathVariable("shopId")Long shopId,
            @PathVariable("id") Long id,
            @Validated @RequestBody TemplateModifyVo vo,
            @LoginUser UserDto user
    ){
        templateService.updateTemplateById(shopId,id,vo,user);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
    * 删除运费模板，且同步删除与商品的关系
    */
    @Audit(departName = "shops")
    @DeleteMapping("/{id}")
    public ReturnObject deleteTemplate(
            @LoginUser UserDto user,
            @PathVariable("shopId")Long shopId,
            @PathVariable("id") Long id
    ){
        templateService.sendDelTemplateMsg(user,shopId,id);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
    * 管理员定义重量模板明细
    */
    @Audit(departName = "shops")
    @PostMapping("/{id}/regions/{rid}/weighttemplate")
    public ReturnObject createWeightTemplate(
            @PathVariable("shopId")Long shopId,
            @PathVariable("id") Long id,
            @PathVariable("rid") Long rid,
            @Validated @RequestBody WeightTemplateVo vo,
            @LoginUser UserDto user
    ){
        WeightTemplate bo=cloneObj(vo,WeightTemplate.class);
        bo.setTemplateId(id);
        bo.setRegionId(rid);
        bo.setTemplateDao(TemplateDao.WEIGHT);
        regionTemplateService.insertRegionTemplate(shopId,bo,user);
        return  new ReturnObject(ReturnNo.CREATED,ReturnNo.CREATED.getMessage());
    }

    /**
    * 管理员修改重量模板明细
    */
    @Audit(departName = "shops")
    @PutMapping("/{id}/regions/{rid}/weighttemplate")
    public ReturnObject updateWeightTemplate(
            @PathVariable("shopId")Long shopId,
            @PathVariable("id") Long id,
            @PathVariable("rid") Long rid,
            @Validated @RequestBody WeightTemplateVo vo,
            @LoginUser UserDto user
    ){
        WeightTemplate bo=cloneObj(vo,WeightTemplate.class);
        bo.setTemplateId(id);
        bo.setRegionId(rid);
        bo.setTemplateDao(TemplateDao.WEIGHT);
        regionTemplateService.saveRegionTemplate(shopId,bo,user);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
    * 管理员删除地区模板
    */
    @Audit(departName = "shops")
    @DeleteMapping("/{id}/regions/{rid}")
    public ReturnObject deleteRegionTemplate(
            @PathVariable("shopId")Long shopId,
            @PathVariable("id") Long id,
            @PathVariable("rid") Long rid
    ){
        regionTemplateService.deleteRegionTemplate(shopId, id, rid);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
    * 管理员定义件数模板明细
    */
    @Audit(departName = "shops")
    @PostMapping("/{id}/regions/{rid}/piecetemplates")
    public ReturnObject createPieceTemplate(
            @PathVariable("shopId")Long shopId,
            @PathVariable("id") Long id,
            @PathVariable("rid") Long rid,
            @Validated @RequestBody PieceTemplateVo vo,
            @LoginUser UserDto user
    ){
        PieceTemplate bo=cloneObj(vo, PieceTemplate.class);
        bo.setTemplateId(id);
        bo.setRegionId(rid);
        bo.setTemplateDao(TemplateDao.PIECE);
        regionTemplateService.insertRegionTemplate(shopId,bo,user);
        return  new ReturnObject(ReturnNo.CREATED,ReturnNo.CREATED.getMessage());
    }
    /**
    * 管理员修改件数模板
    */
    @Audit(departName = "shops")
    @PutMapping("/{id}/regions/{rid}/piecetemplates")
    public ReturnObject updatePieceTemplate(
            @PathVariable("shopId")Long shopId,
            @PathVariable("id") Long id,
            @PathVariable("rid") Long rid,
            @Validated @RequestBody PieceTemplateVo vo,
            @LoginUser UserDto user
    ){
        PieceTemplate bo=cloneObj(vo, PieceTemplate.class);
        bo.setTemplateId(id);
        bo.setRegionId(rid);
        bo.setTemplateDao(TemplateDao.PIECE);
        regionTemplateService.saveRegionTemplate(shopId,bo,user);
        return new ReturnObject(ReturnNo.OK);
    }
    /**
    * 店家或管理员查询运费模板明细
    */
    @Audit(departName = "shops")
    @GetMapping("/{id}/regions")
    public ReturnObject retrieveRegionTemplateById(
            @PathVariable("shopId")Long shopId,
            @PathVariable("id") Long id,
            @RequestParam(required = false,defaultValue = "1")Integer page,
            @RequestParam(required = false,defaultValue ="10")Integer pageSize
    ){
        /*
         * RegionTemplateVo是PieceTemplateVo和WeightTemplateVo的父类
         * 使用泛型自动映射
         * */
        PageDto<? extends RegionTemplateDto> pageDto=regionTemplateService.retrieveRegionTemplateById(shopId,id,page,pageSize);
        return new ReturnObject(pageDto);
    }
}
