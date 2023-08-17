//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class OrderItemDto implements Serializable {
    private Long onsaleId;

    private Integer quantity;

    private Long actId;

    private Long couponId;
}
