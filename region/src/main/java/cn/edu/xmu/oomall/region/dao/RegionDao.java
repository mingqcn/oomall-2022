package cn.edu.xmu.oomall.region.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.region.dao.bo.Region;
import cn.edu.xmu.oomall.region.mapper.RegionPoMapper;
import cn.edu.xmu.oomall.region.mapper.po.RegionPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;


@Repository
@RefreshScope
public class RegionDao {
    private final static Logger logger = LoggerFactory.getLogger(RegionDao.class);
    private final static String KEY = "R%d";
    private final static String PARENT_KEY = "RP%d";
    private final static Long SAVE_FAILED_ID = -1L;

    @Value("${oomall.region.timeout}")
    private int timeout;

    private final RegionPoMapper regionPoMapper;
    private final RedisUtil redisUtil;

    @Autowired
    public RegionDao(RegionPoMapper regionPoMapper, RedisUtil redisUtil) {
        this.regionPoMapper = regionPoMapper;
        this.redisUtil = redisUtil;
    }

    public void setBo(Region bo) {
        bo.setRegionDao(RegionDao.this);
    }

    public Region getBo(RegionPo po, Optional<String> redisKey) {
        Region bo = cloneObj(po, Region.class);
        this.setBo(bo);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, timeout));
        return bo;
    }

    /**
     * 通过id查找地区
     *
     * @param id id
     * @return Region
     * @throws RuntimeException
     */
    public Region findById(Long id) throws RuntimeException {
        logger.debug("findById: id = {}", id);
        if (null == id) {
            return null;
        }
        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            Region bo = (Region) redisUtil.get(key);
            this.setBo(bo);
            return bo;
        }
        Optional<RegionPo> ret = regionPoMapper.findById(id);
        if (ret.isPresent()) {
            return this.getBo(ret.get(), Optional.of(key));
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区", id));
        }
    }

    /**
     * 通过id查找所有上级地区
     * @param id 地区id
     * @return 上级地区列表
     * @throws RuntimeException
     */
    public List<Region> retrieveParentsRegionsById(Long id) throws RuntimeException {
        logger.debug("findParentsRegionsById: id = {}", id);
        if(null == id) {
            return null;
        }
        String key = String.format(PARENT_KEY, id);
        if(redisUtil.hasKey(key)) {
            List<Long> parentIds = (List<Long>) redisUtil.get(key);
            return parentIds.stream().map(this::findById).filter(Objects::nonNull).collect(Collectors.toList());
        }
        Region region;
        try {
            region = this.findById(id);
        }
        catch (BusinessException e) {
            return null;
        }
        List<Region> boList = new ArrayList<>();
        while(boList.size() < 10 && !Region.INVALID_ID.equals(region.getPid())) {
            region = region.getParentRegion();
            boList.add(region);
        }
        this.redisUtil.set(key, (ArrayList<Long>) boList.stream().map(Region::getId).collect(Collectors.toList()), timeout);
        return boList;
    }

    /**
     * 通过pid查找子地区
     *
     * @param pid
     * @return List<Region>
     * @throws RuntimeException
     */
    public List<Region> retrieveSubRegionsById(Long pid, Integer page, Integer pageSize) throws RuntimeException {
        logger.debug("retrieveSubRegionsByPid: pid = {}", pid);
        if (null == pid) {
            return null;
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<RegionPo> poPage = regionPoMapper.findByPid(pid, pageable);
        if (poPage.isEmpty()) {
            return new ArrayList<>();
        } else {
            return poPage.stream()
                    .map(po -> this.getBo(po, Optional.of(String.format(KEY, po.getId()))))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 创建地区
     *
     * @param bo   地区bo
     * @param user 登录用户
     */
    public void insert(Region bo, UserDto user) throws RuntimeException {
        this.setBo(bo);
        bo.setLevel(bo.getLevel());
        bo.setId(null);
        RegionPo po = cloneObj(bo, RegionPo.class);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        logger.debug("save: po = {}", po);
        po = regionPoMapper.save(po);
        bo.setId(po.getId());
    }

    /**
     * 修改地区信息
     *
     * @param bo   地区bo
     * @param user 登录用户
     * @return
     */
    public String saveById(Region bo, UserDto user) throws RuntimeException {
        RegionPo po = cloneObj(bo, RegionPo.class);
        putUserFields(po, "modifier", user);
        putGmtFields(po, "modified");
        logger.debug("saveById: po = {}", po);
        regionPoMapper.save(po);
        if(SAVE_FAILED_ID.equals(po.getId())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "地区", bo.getId()));
        }
        return String.format(KEY, bo.getId());
    }
}
