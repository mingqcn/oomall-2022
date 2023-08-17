package cn.edu.xmu.oomall.payment.service.channel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询退款
 * */
@Data
@NoArgsConstructor
public class GetRefundAdaptorDto {

    /**
     * 订单金额
     * 微信:Amount.total
     * 支付宝:refund_amount
     * */
    private Long amount;
}
