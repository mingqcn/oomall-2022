package cn.edu.xmu.oomall.shop.controller.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FreightPriceVo {
    private Long freightPrice;

    private Collection<ProductItemVo> pack;
}
