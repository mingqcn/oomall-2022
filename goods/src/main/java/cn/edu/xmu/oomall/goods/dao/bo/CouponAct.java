package cn.edu.xmu.oomall.goods.dao.bo;

import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponDiscount;
import com.fasterxml.jackson.annotation.JsonInclude;
import jdk.jfr.Threshold;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠活动
 */
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(callSuper = true)
@Data
public class CouponAct extends Activity {

    private String objectId;


    private LocalDateTime couponTime;

    private Integer quantity;


    private Integer quantityType;


    private Integer validTerm;


    private String strategy;

}