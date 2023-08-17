package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.Common;
import cn.edu.xmu.oomall.goods.controller.vo.CouponactivityVo;
import cn.edu.xmu.oomall.goods.dao.bo.CouponAct;
import cn.edu.xmu.oomall.goods.service.ActivityService;
import cn.edu.xmu.oomall.goods.service.CouponActService;
import cn.edu.xmu.oomall.goods.service.dto.CouponActivityDto;
import cn.edu.xmu.oomall.goods.service.dto.SimpleCouponActivityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 优惠控制器
 * @author Liang nan
 */
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/shops/{shopId}", produces = "application/json;charset=UTF-8")
public class AdminCouponActController {

    private final Logger logger = LoggerFactory.getLogger(AdminCouponActController.class);
    private ActivityService activityService;
    private CouponActService couponActService;

    @Autowired
    public AdminCouponActController(CouponActService couponActService, ActivityService activityService) {

        this.couponActService = couponActService;
        this.activityService = activityService;
    }
    /**
     * 管理员新建己方优惠活动
     *
     * @author Liang nan
     */
    @PostMapping("/couponactivities")
    @Audit(departName = "shops")
    public ReturnObject addCouponactivity(@PathVariable Long shopId,
                                          @Validated @RequestBody CouponactivityVo couponactivityVo,
                                          @LoginUser UserDto creator) {
        CouponAct bo = Common.cloneObj(couponactivityVo, CouponAct.class);
        SimpleCouponActivityDto dto = couponActService.addCouponactivity(shopId, bo, creator);
        return new ReturnObject(dto);
    }

    /**
     * 查看店铺的所有状态优惠活动列表
     *
     * @author Liang nan
     */
    @GetMapping("/couponactivities")
    @Audit(departName = "shops")
    public ReturnObject getCouponactivity(@PathVariable("shopId") Long shopId,
                                          @RequestParam(required = false) Long onsaleId,
                                          @RequestParam(required = false) Long productId,
                                          @RequestParam(required = false, defaultValue = "1") Integer page,
                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        PageDto<SimpleCouponActivityDto> pageDto = couponActService.retrieveByShopIdAndProductIdAndOnsaleId(shopId, productId, onsaleId, page, pageSize);
        return new ReturnObject(pageDto);
    }

    /**
     * 查看优惠活动详情
     *
     * @author Liang nan
     */
    @GetMapping("/couponactivities/{id}")
    @Audit(departName = "shops")
    public ReturnObject getCouponActId(@PathVariable("shopId") Long shopId,
                                       @PathVariable("id") Long id) {
        CouponActivityDto dto = couponActService.findCouponActivityById(shopId, id);
        return new ReturnObject(dto);
    }

    /**
     * 管理员修改己方某优惠活动
     *
     * @author Liang nan
     */
    @PutMapping("/couponactivities/{id}")
    @Audit(departName = "shops")
    public ReturnObject putCouponActProduct(@PathVariable("shopId") Long shopId,
                                            @PathVariable("id") Long id,
                                            @Validated @RequestBody CouponactivityVo couponactivityVo,
                                            @LoginUser UserDto modifier) {
        CouponAct bo = Common.cloneObj(couponactivityVo, CouponAct.class);
        couponActService.updateCouponActivityById(shopId, id, bo, modifier);
        return new ReturnObject();
    }

    /**
     * 管理员取消己方某优惠活动
     *
     * @author Liang nan
     */
    @DeleteMapping("/couponactivities/{id}")
    @Audit(departName = "shops")
    public ReturnObject delCouponAct(@PathVariable("shopId") Long shopId,
                                     @PathVariable("id") Long id) {
        couponActService.deleteCouponActivityById(shopId, id);
        return new ReturnObject();
    }

    /**
     * 管理员为己方活动新增限定范围
     *
     * @author Liang nan
     */
    @PostMapping("/activities/{id}/onsales/{sid}")
    @Audit(departName = "shops")
    public ReturnObject addAct(@PathVariable("shopId") Long shopId,
                               @PathVariable("id") Long id,
                               @PathVariable("sid") Long sid,
                               @LoginUser UserDto creator) {
        activityService.addActivityOnsale(shopId, id, sid, creator);
        return new ReturnObject();
    }

}
