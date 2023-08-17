package cn.edu.xmu.oomall.goods.controller.vo;

import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponDiscount;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CouponactivityVo {

    @NotNull(message = "优惠活动名称不能为空")
    private String name;

    @Min(value = 0, message = "0-不用优惠券")
    private Integer quantity;

    @Min(value = 0, message = "0-每人数量")
    @Max(value = 1, message = "1-总数控制")
    private Integer quantityType;

    @Min(value = 0, message = "0-与活动同")
    private Integer validTerm;

    private LocalDateTime couponTime;

    private String strategy;
}