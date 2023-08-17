package cn.edu.xmu.oomall.region.controller;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.StatusDto;
import cn.edu.xmu.oomall.region.service.RegionService;
import cn.edu.xmu.oomall.region.service.dto.RegionDto;
import cn.edu.xmu.oomall.region.service.dto.IdNameDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地区控制器
 */
@RestController
@RequestMapping(produces = "application/json;charset=UTF-8")
public class UnAuthorizedController {
    private final Logger logger = LoggerFactory.getLogger(UnAuthorizedController.class);
    private final RegionService regionService;

    @Autowired
    public UnAuthorizedController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping("/regions/states")
    public ReturnObject retrieveRegionsStates() {
        List<StatusDto> dtoList = this.regionService.retrieveRegionsStates();
        return new ReturnObject(ReturnNo.OK, dtoList);
    }

    @GetMapping("/regions/{id}/subregions")
    public ReturnObject retrieveSubRegionsById(@PathVariable Long id,
                                               @RequestParam(required = false, defaultValue = "1") Integer page,
                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageDto<IdNameDto> pageDto = this.regionService.retrieveSubRegionsById(id, page, pageSize, false);
        return new ReturnObject(ReturnNo.OK, pageDto);
    }

    @GetMapping("/regions/{id}")
    public ReturnObject findRegionById(@PathVariable Long id) {
        RegionDto dto = this.regionService.findRegionById(id);
        return new ReturnObject(ReturnNo.OK, dto);
    }
}
