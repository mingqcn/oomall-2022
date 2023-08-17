package cn.edu.xmu.oomall.shop.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author chenyz
 * @date 2022-11-29 18:53
 */
@Data
@NoArgsConstructor
public class ShopConsigneeVo {
    /**
     * 店铺联系人姓名和电话
     */
    private String name;
    private String mobile;

    @NotNull(message = "地区必填")
    private Long regionId;

    @NotBlank(message = "店铺联系人详细地址不能为空")
    private String address;
}
