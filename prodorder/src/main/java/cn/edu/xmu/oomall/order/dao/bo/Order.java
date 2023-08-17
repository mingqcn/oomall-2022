//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@ToString(callSuper = true)
@NoArgsConstructor
public class Order extends OOMallObject {

    @Builder
    public Order(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Long customerId, Long shopId, String orderSn, Long pid, String consignee, Long regionId, String address, String mobile, String message, Long activityId, Long packageId, List<OrderItem> orderItems) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.customerId = customerId;
        this.shopId = shopId;
        this.orderSn = orderSn;
        this.pid = pid;
        this.consignee = consignee;
        this.regionId = regionId;
        this.address = address;
        this.mobile = mobile;
        this.message = message;
        this.activityId = activityId;
        this.packageId = packageId;
        this.orderItems = orderItems;
    }

    private Long customerId;

    private Long shopId;

    @Setter
    @Getter
    private String orderSn;

    @Setter
    @Getter
    private Long pid;

    @Setter
    @Getter
    private String consignee;

    @Setter
    private Long regionId;

    @Setter
    @Getter
    private String address;

    @Setter
    @Getter
    private String mobile;

    @Setter
    @Getter
    private String message;

    @Setter
    private Long activityId;

    @Setter
    private Long packageId;

    @Setter
    @Getter
    private List<OrderItem> orderItems;


}
