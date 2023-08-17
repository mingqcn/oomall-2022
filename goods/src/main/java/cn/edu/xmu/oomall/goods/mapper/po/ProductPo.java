//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "goods_product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPo {
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

    private Long shopId;

    private Long goodsId;

    private Long categoryId;

    private Long templateId;

    private String skuSn;

    private String name;

    private Long originalPrice;

    private Long weight;

    private String barcode;

    private String unit;

    private String originPlace;

    private Long shopLogisticId;

    private Integer commissionRatio;

    private Byte status;

    private Integer freeThreshold;
}
