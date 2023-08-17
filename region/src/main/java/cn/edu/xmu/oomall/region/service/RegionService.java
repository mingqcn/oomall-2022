package cn.edu.xmu.oomall.region.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.StatusDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.region.dao.RegionDao;
import cn.edu.xmu.oomall.region.dao.bo.Region;
import cn.edu.xmu.oomall.region.service.dto.RegionDto;
import cn.edu.xmu.oomall.region.service.dto.IdNameDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Service
public class RegionService {
    private final Logger logger = LoggerFactory.getLogger(RegionService.class);

    private final RegionDao regionDao;
    private final RedisUtil redisUtil;

    @Autowired
    public RegionService(RegionDao regionDao, RedisUtil redisUtil) {
        this.regionDao = regionDao;
        this.redisUtil = redisUtil;
    }

    /**
     * 获取下级地区
     *
     * @param id                 父地区id
     * @param page               页码
     * @param pageSize           页大小
     * @param isAdmin 是否包括已废弃地区
     * @return PageDto
     */
    @Transactional
    public PageDto<IdNameDto> retrieveSubRegionsById(Long id, Integer page, Integer pageSize, boolean isAdmin) {
        this.regionDao.findById(id);
        List<IdNameDto> dtoList = regionDao.retrieveSubRegionsById(id, page, pageSize)
                .stream()
                .filter(bo -> isAdmin || Region.VALID.equals(bo.getStatus()))
                .map(bo -> cloneObj(bo, IdNameDto.class))
                .collect(Collectors.toList());
        logger.debug("retrieveSubRegionsById: dtoList = {}", dtoList);
        return new PageDto<>(dtoList, page, pageSize);
    }

    /**
     * 创建新的子地区
     *
     * @param id   父地区id
     * @param user 登录用户
     * @return SimpleRegionDto
     */
    @Transactional
    public IdNameDto createSubRegions(Long id, String name, String shortName, String mergerName, String pinyin,
                                      Double lng, Double lat, String areaCode,
                                      String zipCode, String cityCode, UserDto user) {
        Region bo = new Region(name, shortName, mergerName, pinyin, lng, lat, areaCode, zipCode, cityCode, Region.VALID);
        bo.setPid(id);
        logger.debug("createSubRegions: bo = {}", bo);
        this.regionDao.insert(bo, user);
        return cloneObj(bo, IdNameDto.class);
    }

    /**
     * 通过id更新地区
     *
     * @param id   地区id
     * @param user 登录用户
     */
    @Transactional
    public void updateRegionById(Long id, String name, String shortName, String mergerName, String pinyin,
                                 Double lng, Double lat, String areaCode,
                                 String zipCode, String cityCode, UserDto user) {
        Region bo = this.regionDao.findById(id);
        logger.debug("updateRegionById: bo = {}", bo);
        if (Region.ABANDONED.equals(bo.getStatus())) {
            throw new BusinessException(ReturnNo.REGION_ABANDONE, String.format(ReturnNo.REGION_ABANDONE.getMessage(), id));
        }
        bo = new Region(name, shortName, mergerName, pinyin, lng, lat, areaCode, zipCode, cityCode, null);
        bo.setId(id);
        String key = this.regionDao.saveById(bo, user);
        this.redisUtil.del(key);
    }

    /**
     * 递归修改地区状态
     *
     * @param id     地区id
     * @param status 修改后的状态
     * @param region 目标地区（父地区为null）
     */
    @Transactional
    public void updateRegionStatusById(Long id, Byte status, Region region, UserDto user) {
        logger.debug("updateRegionStatusById: id = {}, status = {}", id, status);
        Region bo = (null == region) ? this.regionDao.findById(id) : region;
        if (!bo.allowStatus(status)) {
            if (null == region) {
                //如果是父地区则抛出异常，如果是子地区则跳过，不跳过的话递归会出问题
                throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "地区", id, bo.getStatusName()));
            }
            return;
        }
        bo.setStatus(status);
        String key = this.regionDao.saveById(bo, user);
        this.regionDao.retrieveSubRegionsById(id, 1, MAX_RETURN)
                .forEach(subRegion -> updateRegionStatusById(subRegion.getId(), status, subRegion, user));
        this.redisUtil.del(key);
    }

    /**
     * 获取所有地区状态
     *
     * @return
     */
    @Transactional
    public List<StatusDto> retrieveRegionsStates() {
        return Region.STATUSNAMES.keySet().stream().map(key -> new StatusDto(key, Region.STATUSNAMES.get(key))).collect(Collectors.toList());
    }

    /**
     * 通过id查找地区
     *
     * @param id 地区id
     * @return RegionDto
     */
    @Transactional
    public RegionDto findRegionById(Long id) {
        logger.debug("findRegionById: id = {}", id);
        Region bo = this.regionDao.findById(id);
        RegionDto dto = cloneObj(bo, RegionDto.class);
        dto.setCreator(new IdNameDto(bo.getCreatorId(), bo.getCreatorName()));
        dto.setModifier(new IdNameDto(bo.getModifierId(), bo.getModifierName()));
        dto.setLat(bo.getLat().toString());
        dto.setLng(bo.getLng().toString());
        logger.debug("findRegionById: dto = {}", dto);
        return dto;
    }

    /**
     * 查找上级地区
     *
     * @param id 地区id
     * @return List
     */
    @Transactional
    public List<IdNameDto> retrieveParentsRegionsById(Long id) {
        logger.debug("getParentsRegionsById: id = {}", id);
        List<IdNameDto> dtoList = this.regionDao.retrieveParentsRegionsById(id)
                .stream()
                .map(bo -> cloneObj(bo, IdNameDto.class))
                .collect(Collectors.toList());
        logger.debug("getParentsRegionsById: dtoList = {}", dtoList);
        return dtoList;
    }
}
