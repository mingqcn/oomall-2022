package cn.edu.xmu.oomall.shop.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenyz
 * @date 2022-12-09 21:30
 */
@Data
@NoArgsConstructor
public class ShopModifyVo {
    /**
     * 联系人信息
     */
    ShopConsigneeVo consignee;

    /**
     * 商铺免邮金额
     */
    Integer freeThreshold;
}
