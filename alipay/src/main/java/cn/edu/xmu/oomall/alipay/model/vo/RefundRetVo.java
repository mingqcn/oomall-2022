package cn.edu.xmu.oomall.alipay.model.vo;

import cn.edu.xmu.oomall.alipay.util.AlipayReturnNo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefundRetVo {

    @JsonProperty("out_trade_no")
    private String outTradeNo;

    /**
     * 退款总金额。
     * 指该笔交易累计已经退款成功的金额
     */
    @JsonProperty("refund_fee")
    private BigDecimal refundFee;

    private String code;
    private String msg;

    @JsonProperty("sub_code")
    private String subCode;
    @JsonProperty("sub_msg")
    private String subMsg;


    /**
     * 固定 :2013112011001004330000121536
     */
    @JsonProperty("trade_no")
    private String tradeNo;

    /**
     * 固定:1595620
     */
    @JsonProperty("buyer_logon_id")
    private String buyerLogonId;
    /**
     * 本次退款是否发生了资金变化 固定：Y
     */
    @JsonProperty("fund_change")
    private String fundChange;
    /**
     * 固定:2088101117955611
     */
    @JsonProperty("buyer_user_id")
    private String buyerUserId;

    public RefundRetVo(AlipayReturnNo alipayReturnNo) {
        this.code="40004";
        this.msg="Business Failed";
        this.subCode=alipayReturnNo.getSubCode();
        this.subMsg=alipayReturnNo.getSubMsg();
    }

    public void setDefault()
    {
        code="10000";
        msg="Success";
        buyerUserId="2088101117955611";
        fundChange="Y";
        buyerLogonId="1595620";
        tradeNo="2013112011001004330000121536";
    }
}
