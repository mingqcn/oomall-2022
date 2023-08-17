package cn.edu.xmu.oomall.shop.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author chenyz
 * @date 2022-11-26 10:53
 */
@Data
@NoArgsConstructor
public class ShopVo {
    @NotBlank(message = "店铺名称不能为空")
    String name;

    /**
     * 类型: 0 电商 1 服务商
     */
    Byte type;

    /**
     * 联系人信息
     */
    ShopConsigneeVo consignee;

    /**
     * 商铺免邮金额
     */
    Integer freeThreshold;

}
