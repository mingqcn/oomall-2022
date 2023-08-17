//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.mapper.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "shop_service_product")
public class ShopServiceProductPo {
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

    private Long maintainerId;

    private Long productId;
    /**
     * 开始时间
     */
    private LocalDateTime beginTime;

    /**
     * 终止时间
     */
    private LocalDateTime endTime;

    /**
     * 有效 0 无效 1
     */
    private Byte invalid;

    private Long regionId;

    /**
     * 优先级 0优先
     */
    private Integer priority;
}
