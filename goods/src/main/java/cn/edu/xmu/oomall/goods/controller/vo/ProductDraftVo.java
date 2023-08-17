package cn.edu.xmu.oomall.goods.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author wuzhicheng
 * @create 2022-12-04 11:04
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDraftVo {
    private String skuSn;

    private String name;

    @Min(value = 0, message = "原价不能小于0")
    private Long originalPrice;

    @Min(value = 0, message = "重量不能小于0")
    private Long weight;

    private Long categoryId;

    private String barCode;

    private String unit;

    private String originPlace;
}
