package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.dao.OnsaleDao;
import cn.edu.xmu.oomall.goods.dao.activity.ActivityDao;
import cn.edu.xmu.oomall.goods.dao.activity.CouponActDao;
import cn.edu.xmu.oomall.goods.dao.bo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;

@Service
public class ActivityService {

    private static final Logger logger = LoggerFactory.getLogger(CouponActService.class);
    private ActivityDao activityDao;
    private OnsaleDao onsaleDao;
    private RedisUtil redisUtil;

    @Autowired
    public ActivityService(ActivityDao activityDao, OnsaleDao onsaleDao, RedisUtil redisUtil) {
        this.activityDao = activityDao;
        this.onsaleDao = onsaleDao;
        this.redisUtil = redisUtil;
    }


    /**
     * 为己方活动新增限定范围
     *
     * @param shopId 商户Id
     * @param id     活动Id
     */
    @Transactional
    public void addActivityOnsale(Long shopId, Long id, Long sid, UserDto creator) {
        Activity activity = this.activityDao.findById(id);
        //范围检查
        if (shopId != PLATFORM && !activity.getShopId().equals(shopId)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "优惠活动", activity.getId(), shopId));
        }
        Onsale onsale = this.onsaleDao.findById(sid);
        this.activityDao.addActivityOnsale(id,onsale, creator);
    }

}