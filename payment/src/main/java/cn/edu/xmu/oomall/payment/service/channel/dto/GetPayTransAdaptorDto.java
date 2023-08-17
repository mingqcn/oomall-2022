package cn.edu.xmu.oomall.payment.service.channel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询订单
 * */
@Data
@NoArgsConstructor
public class GetPayTransAdaptorDto {

    /**
     * 订单金额
     * 微信:Amount.total
     * 支付宝:total_amount
     * */
    private Long amount;
}
