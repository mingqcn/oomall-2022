//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString(callSuper = true)
@NoArgsConstructor
public class OrderItem extends OOMallObject implements Serializable {

    @Builder
    public OrderItem(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Long orderId, Long onsaleId, Integer quantity, Long price, Long discountPrice, Long point, String name, Long actId, Long couponId, Byte commented) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.orderId = orderId;
        this.onsaleId = onsaleId;
        this.quantity = quantity;
        this.price = price;
        this.discountPrice = discountPrice;
        this.point = point;
        this.name = name;
        this.actId = actId;
        this.couponId = couponId;
        this.commented = commented;
    }

    @Setter
    @Getter
    private Long orderId;

    @Setter
    @Getter
    private Long onsaleId;

    @Setter
    @Getter
    private Integer quantity;

    @Setter
    @Getter
    private Long price;

    @Setter
    @Getter
    private Long discountPrice;

    @Setter
    @Getter
    private Long point;

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private Long actId;

    @Setter
    @Getter
    private Long couponId;

    @Setter
    @Getter
    private Byte commented;
}
