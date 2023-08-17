package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.controller.vo.AdvanceSaleActVo;
import cn.edu.xmu.oomall.goods.dao.OnsaleDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.dao.activity.ActivityDao;
import cn.edu.xmu.oomall.goods.dao.activity.AdvanceSaleActDao;
import cn.edu.xmu.oomall.goods.dao.bo.Activity;
import cn.edu.xmu.oomall.goods.dao.bo.AdvanceSaleAct;
import cn.edu.xmu.oomall.goods.dao.bo.Onsale;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Service
public class AdvanceSaleService {

    private Logger logger = LoggerFactory.getLogger(AdvanceSaleService.class);
    private AdvanceSaleActDao advanceSaleActDao;
    private ShopDao shopDao;
    private ActivityDao activityDao;
    private OnsaleDao onsaleDao;
    private RedisUtil redisUtil;
    private ProductDao productDao;


    @Autowired
    public AdvanceSaleService(AdvanceSaleActDao advanceSaleActDao, ActivityDao activityDao,
                              OnsaleDao onsaleDao, RedisUtil redisUtil, ShopDao shopDao, ProductDao productDao) {
        this.advanceSaleActDao = advanceSaleActDao;
        this.activityDao = activityDao;
        this.productDao = productDao;
        this.onsaleDao = onsaleDao;
        this.shopDao = shopDao;
        this.redisUtil = redisUtil;
    }



    /**
     * 用户查询所有上线的预售活动(只可查询已上架的预售活动)
     * @author 兰文强 25120202201946
     * @date 2022/12/14 23:10
     */
    public PageDto<SimpleAdvanceSaleActDto> retrieveAllAdvanceSaleAct(Long shopId, Long productId, Integer page, Integer pageSize) {
        PageDto<Activity> ret = advanceSaleActDao.retrieveValidByShopIdAndProductId(shopId, productId,page, pageSize);
        List<SimpleAdvanceSaleActDto> advanceSaleActDtos = ret.getList().stream()
                .map(act-> cloneObj(act, SimpleAdvanceSaleActDto.class))
                .collect(Collectors.toList());
        return new PageDto<>(advanceSaleActDtos, page, pageSize);
    }


    /**
     * 用户查询预售详情信息
     * @author 兰文强 25120202201946
     * @date 2022/12/14 23:10
     */
    public AdvanceSaleActDto retrieveValidById(Long id) {
        AdvanceSaleAct activity = advanceSaleActDao.findValidAdvanceSaleActById(id);
        AdvanceSaleActDto ret = cloneObj(activity,AdvanceSaleActDto.class);
        ret.setShop(cloneObj(shopDao.getShopById(activity.getShopId()).getData(), SimpleShopDto.class));
        return ret;
    }

    /**
     * 管理员根据id查询预售信息
     * @author 兰文强 25120202201946
     * @date 2022/12/14 23:14
     */
    public FullAdvanceSaleActDto retrieveById(Long id, UserDto userDto) {
        AdvanceSaleAct advanceSaleAct = advanceSaleActDao.findById(id);
        FullAdvanceSaleActDto ret = cloneObj(advanceSaleAct,FullAdvanceSaleActDto.class);
        ret.setShop(cloneObj(shopDao.getShopById(advanceSaleAct.getShopId()).getData(), SimpleShopDto.class));
        ret.setCreator(new IdNameDto(advanceSaleAct.getCreatorId(), advanceSaleAct.getCreatorName()));
        ret.setModifier(new IdNameDto(advanceSaleAct.getModifierId(), advanceSaleAct.getModifierName()));
        return ret;
    }


    /**
     * 管理员查询特定商铺所有预售活动
     * @author 兰文强 25120202201946
     * @date 2022/12/2 15:57
     */
    public PageDto<SimpleAdvanceSaleActDto> retrieveAdvanceSaleActByShopId(Long shopId, Long productId, Long onsaleId, Integer page,
                                                                           Integer pageSize, UserDto userDto) {
        List<Activity> actList = advanceSaleActDao.retrieveByShopIdAndProductIdAndOnsaleId(shopId, productId, onsaleId, page, pageSize).getList();
        if(actList==null){
            return new PageDto<>(new ArrayList<>(),page,pageSize);
        }
        List<SimpleAdvanceSaleActDto> ret = actList.stream()
                .map(bo->cloneObj(bo,SimpleAdvanceSaleActDto.class))
                .collect(Collectors.toList());
        return new PageDto<>(ret, page, pageSize);
    }


    /**
     * 管理员新增预售
     * @author 兰文强 25120202201946
     * @date 2022/12/2 16:33
     */
    public ReturnObject createAdvanceSaleAct(Long shopId, Long id, String name, LocalDateTime payTime, Long advancePayPrice, UserDto userDto) {
        //判断是否与优惠或团购活动并存
        Onsale onsale = onsaleDao.findById(id);
        if (onsale.getActList().stream().map(Activity::getActClass).anyMatch(s -> s.equals("grouponActDao") || s.equals("couponActDao"))) {
            return new ReturnObject(ReturnNo.ADVSALE_NOTCOEXIST, "预售与优惠和团购活动不能并存!");
        }
        AdvanceSaleAct advanceSaleAct = new AdvanceSaleAct(shopId, name, null, AdvanceSaleAct.ACTCLASS, payTime, advancePayPrice);
        advanceSaleAct = (AdvanceSaleAct) activityDao.insert(advanceSaleAct, userDto);
        onsale.setInvalid((byte) 1);
        onsaleDao.save(onsale, userDto);
        return new ReturnObject(cloneObj(advanceSaleAct,SimpleAdvanceSaleActDto.class));
    }


    /**
     * 管理员修改预售
     * @author 兰文强 25120202201946
     * @date 2022/12/2 18:01
     */
    public ReturnObject updateAdvanceSaleAct(Long shopId, Long id, AdvanceSaleActVo onsale, UserDto userDto) {
        AdvanceSaleAct act = advanceSaleActDao.findById(id);
        //支付时间是否冲突
        if (onsale.getPayTime().isAfter(onsale.getEndTime())) {
            return new ReturnObject(ReturnNo.ADV_SALE_TIMELATE);
        }
        if (onsale.getPayTime().isBefore(onsale.getBeginTime())) {
            return new ReturnObject(ReturnNo.ADV_SALE_TIMEEARLY);
        }
        act.setDeposit(onsale.getAdvancePayPrice());
        act.setPayTime(onsale.getPayTime());
        act.setName(onsale.getName());
        activityDao.save(act, userDto);
        return new ReturnObject(ReturnNo.OK);
    }


    /**
     * 管理员取消预售活动
     * @author 兰文强 25120202201946
     * @date 2022/12/2 23:28
     */
    public ReturnObject deleteAdvanceSaleActById(Long shopId, Long id, UserDto userDto) {
        advanceSaleActDao.delActivityById(id, shopId);
        return new ReturnObject(ReturnNo.OK);
    }

}
