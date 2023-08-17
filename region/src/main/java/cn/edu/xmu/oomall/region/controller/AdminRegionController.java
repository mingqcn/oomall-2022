package cn.edu.xmu.oomall.region.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.region.controller.vo.RegionVo;
import cn.edu.xmu.oomall.region.dao.bo.Region;
import cn.edu.xmu.oomall.region.service.RegionService;
import cn.edu.xmu.oomall.region.service.dto.IdNameDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.regex.Pattern;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;

/**
 * 地区管理员控制器
 */
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/shops/{did}", produces = "application/json;charset=UTF-8")
public class AdminRegionController {
    private final Logger logger = LoggerFactory.getLogger(AdminRegionController.class);
    private final RegionService regionService;

    @Autowired
    public AdminRegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping("/regions/{id}/subregions")
    @Audit(departName = "shops")
    public ReturnObject getSubRegionsById(@PathVariable Long did, @PathVariable Long id,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        if (!PLATFORM.equals(did)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "地区", id, did));
        }
        return new ReturnObject(this.regionService.retrieveSubRegionsById(id, page, pageSize, true));
    }

    @PostMapping("/regions/{id}/subregions")
    @Audit(departName = "shops")
    public ReturnObject createSubRegions(@PathVariable Long did, @PathVariable Long id, @LoginUser UserDto user,
                                         @Validated @RequestBody RegionVo vo) {
        if (!PLATFORM.equals(did)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "地区", id, did));
        }
        IdNameDto dto = this.regionService.createSubRegions(id, vo.getName(), vo.getShortName(), vo.getMergerName(),
                vo.getPinyin(), Double.parseDouble(vo.getLng()), Double.parseDouble(vo.getLat()),
                vo.getAreaCode(), vo.getZipCode(), vo.getCityCode(), user);
        return new ReturnObject(ReturnNo.CREATED, dto);
    }

    @PutMapping("/regions/{id}")
    @Audit(departName = "shops")
    public ReturnObject updateRegionById(@PathVariable Long did, @PathVariable Long id, @LoginUser UserDto user,
                                         @RequestBody RegionVo vo) {
        if (!PLATFORM.equals(did)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "地区", id, did));
        }
        //手动进行参数检查，保证参数不能为空但是可以为null
        Arrays.stream(RegionVo.class.getMethods())
                .filter(method -> Pattern.compile("get[A-Z][a-z][a-zA-Z0-9]*").matcher(method.getName()).matches()
                        && !method.getDeclaringClass().equals(Object.class)
                        && 0 == method.getParameterCount())
                .forEach(method -> {
                    String param = null;
                    try {
                        param = (String) method.invoke(vo);
                    } catch (Exception e) {
                        throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
                    }
                    if(param != null && param.isBlank()) {
                        throw new BusinessException(ReturnNo.FIELD_NOTVALID, method.getName().substring(3));
                    }
                });
        Double lng = vo.getLng() == null ? null : Double.parseDouble(vo.getLng());
        Double lat = vo.getLat() == null ? null : Double.parseDouble(vo.getLat());
        this.regionService.updateRegionById(id, vo.getName(), vo.getShortName(), vo.getMergerName(),
                vo.getPinyin(), lng, lat, vo.getAreaCode(), vo.getZipCode(), vo.getCityCode(), user);
        return new ReturnObject(ReturnNo.OK);
    }

    @DeleteMapping("/regions/{id}")
    @Audit(departName = "shops")
    public ReturnObject deleteRegionById(@PathVariable Long did, @PathVariable Long id, @LoginUser UserDto user) {
        if (!PLATFORM.equals(did)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "地区", id, did));
        }
        this.regionService.updateRegionStatusById(id, Region.ABANDONED, null, user);
        return new ReturnObject(ReturnNo.OK);
    }

    @PutMapping("/regions/{id}/suspend")
    @Audit(departName = "shops")
    public ReturnObject suspendRegionById(@PathVariable Long did, @PathVariable Long id, @LoginUser UserDto user) {
        if (!PLATFORM.equals(did)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "地区", id, did));
        }
        this.regionService.updateRegionStatusById(id, Region.SUSPENDED, null, user);
        return new ReturnObject(ReturnNo.OK);
    }

    @PutMapping("/regions/{id}/resume")
    @Audit(departName = "shops")
    public ReturnObject resumeRegionById(@PathVariable Long did, @PathVariable Long id, @LoginUser UserDto user) {
        if (!PLATFORM.equals(did)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "地区", id, did));
        }
        this.regionService.updateRegionStatusById(id, Region.VALID, null, user);
        return new ReturnObject(ReturnNo.OK);
    }
}
