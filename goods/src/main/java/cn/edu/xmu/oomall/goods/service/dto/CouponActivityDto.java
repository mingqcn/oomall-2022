package cn.edu.xmu.oomall.goods.service.dto;

import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponDiscount;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class CouponActivityDto {

    private Long id;

    private String name;

    private Integer quantity;

    private LocalDateTime couponTime;

    private Integer quantityType;

    private IdNameTypeDto shop;

    private Integer validTerm;

    private String strategy;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private List<SimpleOnsaleDto> onsaleList;
}