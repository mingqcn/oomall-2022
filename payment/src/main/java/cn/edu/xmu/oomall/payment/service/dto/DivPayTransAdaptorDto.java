package cn.edu.xmu.oomall.payment.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DivPayTransAdaptorDto {
    /**
     * 渠道id
     * wepay: trade_no
     * alipay: trade_no
     */
    private String transNo;

    /**
     * 内部交易id
     * wepay: transaction_id
     * alipay: out_trade_no
     */
    private String outNo;

    /**
     * 分账金额
     * wepay: amount
     * alipay: amount
     */
    private Long amount;

    /**
     * 分账完成时间
     * wepay: finish_time
     * alipay: now
     */
    private LocalDateTime successTime;


    /**
     * 状态
     * wepay: result
     * alipay：按照返回的业务错误码
     */
    private Byte status;
}