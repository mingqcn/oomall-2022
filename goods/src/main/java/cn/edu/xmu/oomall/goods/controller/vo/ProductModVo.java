package cn.edu.xmu.oomall.goods.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author wuzhicheng
 * @create 2022-12-04 13:36
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductModVo {

    String skuSn;

    String name;

    @Min(value = 0, message = "原价不能小于0")
    Long originalPrice;

    @Min(value = 0, message = "重量不能小于0")
    Long weight;

    Long categoryId;

    String barcode;

    String unit;

    String originPlace;

    Long shopLogisticsId;

    Long templateId;

    Integer commissionRatio;

    Integer freeThreshold;
}
