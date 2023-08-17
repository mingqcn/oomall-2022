//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service.channel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 查询分账回退
 * */
@Data
@NoArgsConstructor
public class GetDivRefundAdaptorDto {

    /**
     * 渠道id
     * wepay: return_id
     * alipay: trade_no
     */
    private String transNo;

    /**
     * 内部交易id
     * wepay: out_return_no
     * alipay: out_trade_no
     */
    private String outNo;

    /**
     * 回退金额
     * wepay: amount
     * alipay: refund_fee
     */
    private Long amount;

    /**
     * 成功时间
     * wepay: finish_time
     * alipay: now
     */
    private LocalDateTime successTime;

    /**
     * 状态
     * wepay: result
     * alipay：固定为RefundTrans.SUCCESS
     */
    private Byte status;
}
