package cn.edu.xmu.oomall.payment.controller.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class AlipayNotifyVo {

    @JsonProperty(value = "app_id")
    private String appId;

    @JsonProperty(value = "trade_no")
    private String tradeNo;

    @NotNull(message="内部交易号必填")
    @JsonProperty(value = "out_trade_no")
    private String outTradeNo;

    @NotNull(message="交易完成时间不能为空")
    @JsonProperty(value = "gmt_payment")
    private String gmtPayment;

    @NotNull(message="交易状态必填")
    @JsonProperty(value = "trade_status")
    private String tradeStatus;

    @Min(value = 0, message="付款金额需大于0")
    @JsonProperty(value = "recipet_amount")
    private Long receiptAmount;
}
