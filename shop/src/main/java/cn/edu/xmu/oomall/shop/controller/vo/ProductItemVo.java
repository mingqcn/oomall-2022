package cn.edu.xmu.oomall.shop.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductItemVo {
    private Long orderItemId;
    private Long productId;
    private Integer quantity;
    private Integer weight;
}

