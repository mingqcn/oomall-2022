//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service.channel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 请求退款
 * */
@Data
@NoArgsConstructor
public class PostRefundAdaptorDto {

    /**
     * 支付的用户id
     * alipay： buyer_user_id
     * wepay: user_received_account
     */
    private String userReceivedAccount;

    /**
     * 退款的交易单号
     * alipay: trade_no
     * wepay: transaction_id
     */
    protected String transNo;

    /**
     * 退款金额
     * alipay: refund_fee
     * wepay: amount.refund
     */
    protected Long amount;

    /**
     * 交易时间
     * alipay: =now
     * wepay: success_time
     */
    protected LocalDateTime successTime;

    /**
     * 状态
     * alipay: null
     * wepay: status
     */
    private Byte status;
}
