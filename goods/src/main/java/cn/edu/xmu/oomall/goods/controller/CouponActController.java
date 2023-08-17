package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.oomall.goods.controller.vo.OrderInfoVo;
import cn.edu.xmu.oomall.goods.service.ActivityService;
import cn.edu.xmu.oomall.goods.service.CouponActService;
import cn.edu.xmu.oomall.goods.service.OnsaleService;
import cn.edu.xmu.oomall.goods.service.dto.CouponActivityDto;
import cn.edu.xmu.oomall.goods.service.dto.SimpleCouponActivityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController /*Restful的Controller对象*/
@RequestMapping(produces = "application/json;charset=UTF-8")
public class CouponActController {

    private CouponActService couponActService;
    private OnsaleService onsaleService;

    @Autowired
    public CouponActController(CouponActService couponActService,OnsaleService onsaleService) {
        this.couponActService = couponActService;
        this.onsaleService = onsaleService;
    }

    /**
     * 计算优惠
     *
     * @author Liang nan
     */
    @GetMapping("/couponactivities/{id}/caculate")
    public ReturnObject showOwncouponactivities1(@PathVariable("id") Long id,
                                                 @Validated @RequestBody List<OrderInfoVo> orderInfoVoList) {

        return new ReturnObject(couponActService.showOwncouponactivities1(id,orderInfoVoList));
    }

}
