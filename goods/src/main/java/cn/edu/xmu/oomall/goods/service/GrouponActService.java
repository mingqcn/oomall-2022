package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.dao.OnsaleDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.dao.activity.ActivityDao;
import cn.edu.xmu.oomall.goods.dao.activity.GrouponActDao;
import cn.edu.xmu.oomall.goods.dao.bo.*;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import cn.edu.xmu.oomall.goods.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Service
public class GrouponActService {

    private static final String GROUPONACT = "grouponActDao";
    private static final String ADVANCESALEACT = "advanceSaleActDao";
    private static final String COUPONACT = "couponActDao";

    private Logger logger = LoggerFactory.getLogger(GrouponActService.class);

    private ProductDao productDao;

    private ShopDao shopDao;

    private RedisUtil redisUtil;

    private OnsaleDao onsaleDao;

    private GrouponActDao grouponActDao;

    private ActivityDao activityDao;


    @Autowired
    public GrouponActService(ProductDao productDao, ShopDao shopDao, RedisUtil redisUtil, OnsaleDao onsaleDao, GrouponActDao grouponActDao, ActivityDao activityDao) {
        this.productDao = productDao;
        this.shopDao = shopDao;
        this.redisUtil = redisUtil;
        this.onsaleDao = onsaleDao;
        this.grouponActDao = grouponActDao;
        this.activityDao = activityDao;
    }

    /**
     * 查询特定商铺的所有状态团购
     *
     * @param shopId    商铺Id
     * @param productId 商品Id
     * @param page      页码
     * @param pageSize  页大小
     * @return
     */
    @Transactional(readOnly = true)
    public PageDto<SimpleGrouponActDto> retrieveByShopIdAndProductIdAndOnsaleId(Long shopId, Long productId, Long onsaleId, Integer page, Integer pageSize) {
        //通过销售id或productId查询销售对象
        List<Onsale> onsales = onsaleDao.retrieveByShopIdAndOnsaleIdAndProductId(shopId, onsaleId, productId);
        List<Activity> actList = new ArrayList<>();
        onsales.forEach(po -> {
            actList.addAll(po.getActList());
        });
        //filter不满足要求的onsale
        List<SimpleGrouponActDto> grouponActList = actList.stream()
                .filter(activity -> GROUPONACT.equals(activity.getActClass()))
                .filter(act -> shopId.equals(act.getShopId()))
                .skip((long) (page - 1) * pageSize).limit(pageSize)
                .map(act -> cloneObj(act, SimpleGrouponActDto.class))
                .collect(Collectors.toList());

        return new PageDto<>(grouponActList, page, pageSize);
    }

    /**
     * 查询团购活动(上线状态)
     *
     * @param shopId    商铺Id
     * @param productId 商品Id
     * @param page      页码
     * @param pageSize  页大小
     * @return
     */
    @Transactional(readOnly = true)
    public PageDto<SimpleGrouponActDto> retrieveByShopIdAndProductId(Long shopId, Long productId, Integer page, Integer pageSize) {
        //通过productId查询onsale对象
        List<Onsale> onsales = onsaleDao.retrieveByShopIdAndProductIdAndInvalidEquals(shopId, productId);
        List<Activity> actList = new ArrayList<>();
        //onsale一对多activity
        onsales.forEach(po -> {
            actList.addAll(po.getActList());
        });
        //filter不满足要求的activity
        List<SimpleGrouponActDto> grouponActList = actList.stream()
                .filter(activity -> GROUPONACT.equals(activity.getActClass()))
                .filter(act -> shopId.equals(act.getShopId()))
                .skip((long) (page - 1) * pageSize).limit(pageSize)
                .map(act -> cloneObj(act, SimpleGrouponActDto.class)).collect(Collectors.toList());

        return new PageDto<>(grouponActList, page, pageSize);
    }

    /**
     * 根据id查找团购信息
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public GrouponActDto findById(Long id) {
        Activity byId = activityDao.findById(id);
        if (!GROUPONACT.equals(byId.getActClass())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "团购", id));
        }
        GrouponActDto grouponActDto = cloneObj(byId, GrouponActDto.class);
        Shop shop = shopDao.getShopById(byId.getShopId()).getData();
        grouponActDto.setShop(new IdNameTypeDto(shop.getId(), shop.getName(), shop.getType()));
        return grouponActDto;
    }

    /**
     * 查看团购详情
     *
     * @param shopId
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public FullGrouponActDto findByShopIdAndActId(Long shopId, Long id) {
        Activity byId = activityDao.findById(id);
        //是否为团购
        if (!GROUPONACT.equals(byId.getActClass())) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "团购", id));
        }
        //是否与商铺信息一致
        if (shopId != byId.getShopId()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "团购与商铺", byId.getId(), shopId));
        }
        Shop shop = shopDao.getShopById(byId.getShopId()).getData();
        FullGrouponActDto fullGrouponActDto = cloneObj(byId, FullGrouponActDto.class);
        fullGrouponActDto.setShop(new IdNameTypeDto(shop.getId(), shop.getName(), shop.getType()));
        fullGrouponActDto.setCreator(new IdNameDto(byId.getCreatorId(), byId.getCreatorName()));
        fullGrouponActDto.setModifier(new IdNameDto(byId.getModifierId(), byId.getModifierName()));
        return fullGrouponActDto;
    }

    /**
     * 新增团购或活动
     *
     * @param shopId
     * @param pid
     * @param name
     * @param thresholdPo
     * @param userDto
     * @return
     */
    @Transactional(readOnly = true)
    public SimpleGrouponActDto createGrouponAct(Long shopId, Long pid, String name, ThresholdPo thresholdPo, UserDto userDto) {
        Onsale onsale = onsaleDao.findById(pid);
        if (onsale.getActList().stream().map(Activity::getActClass)
                .anyMatch(t -> t.equals((COUPONACT)) || t.equals(ADVANCESALEACT))) {
            throw new BusinessException(ReturnNo.GROUPON_NOTCOEXIST, ReturnNo.GROUPON_NOTCOEXIST.getMessage());
        }
        List<ThresholdPo> thresholds = new ArrayList<>();
        thresholds.add(thresholdPo);
        GrouponAct grouponAct = new GrouponAct();
        grouponAct.setThresholds(thresholds);
        grouponAct.setName(name);
        grouponAct.setActClass("grouponActDao");
        grouponAct.setShopId(shopId);
        activityDao.insert(grouponAct, userDto);

        activityDao.insertActivityOnsale(grouponAct.getId(), pid, userDto);

        onsale.setType((byte) 2);
        onsale.setInvalid((byte) 0);
        onsaleDao.save(onsale, userDto);

        return cloneObj(grouponAct, SimpleGrouponActDto.class);
    }

    /**
     * 修改团购信息
     *
     * @param shopId
     * @param id
     * @param name
     * @param thresholdPo
     * @param userDto
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnObject updateById(Long shopId, Long id, String name, ThresholdPo thresholdPo, UserDto userDto) {
        Activity activity = activityDao.findById(id);
        if (!GROUPONACT.equals(activity.getActClass()) || shopId != activity.getShopId()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "团购活动", id));
        }
        GrouponAct grouponAct = cloneObj(activity, GrouponAct.class);
        grouponAct.setName(name);
        List<ThresholdPo> thresholds = new ArrayList<>();
        thresholds.add(thresholdPo);
        grouponAct.setThresholds(thresholds);
        putUserFields(grouponAct, "modifier", userDto);
        putGmtFields(grouponAct, "modified");
        grouponActDao.save(grouponAct);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 取消团购
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public ReturnNo cancelById(Long shopId, Long id, UserDto user) {
        Activity activity = activityDao.findById(id);
        if (activity == null || !GROUPONACT.equals(activity.getActClass()) || shopId != activity.getShopId()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "团购活动", id));
        }
        List<ActivityOnsaleDto> activityOnsale = activityDao.retrieveActivityOnsaleByActId(id);
        Onsale onsale = this.onsaleDao.findById(activityOnsale.get(0).getOnsaleId());
        if (onsale.getBeginTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ReturnNo.LATE_BEGINTIME);
        }
        //修改onsale属性
        onsale.setInvalid((byte) 1);
        //若满足条件则修改时间
        if (onsale.getEndTime().isAfter(LocalDateTime.now()))
            onsale.setEndTime(LocalDateTime.now());
        String key = this.onsaleDao.save(onsale, user);
        //删除redis缓存
        if (redisUtil.hasKey(key)) {
            redisUtil.del(key);
        }
        return ReturnNo.OK;
    }
}
