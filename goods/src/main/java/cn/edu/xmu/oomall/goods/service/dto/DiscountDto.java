package cn.edu.xmu.oomall.goods.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DiscountDto {
    private Long couponActivityId;
    private Integer quantity;
    private Long price;
    private Long discount;
}