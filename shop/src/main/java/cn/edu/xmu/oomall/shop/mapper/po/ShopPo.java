//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.mapper.po;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "shop_shop")
public class ShopPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 创建者id
     */
    private Long creatorId;

    /**
     * 创建者
     */
    private String creatorName;

    /**
     * 修改者id
     */
    private Long modifierId;

    /**
     * 修改者
     */
    private String modifierName;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;

    /**
     * 商铺名称
     */
    private String name;

    /**
     * 商铺保证金
     */
    private Long deposit;

    /**
     * 商铺保证金门槛
     */
    private Long depositThreshold;

    /**
     * 状态
     */
    private Byte status;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系人
     */
    private String consignee;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 类型 0：电商 1：服务商
     */
    private Byte type;

    private Long regionId;

    private Integer freeThreshold;
}
