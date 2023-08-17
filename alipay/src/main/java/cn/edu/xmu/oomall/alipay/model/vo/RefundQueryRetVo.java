package cn.edu.xmu.oomall.alipay.model.vo;

import cn.edu.xmu.oomall.alipay.util.AlipayReturnNo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefundQueryRetVo {
    @JsonProperty("out_trade_no")
    private String outTradeNo;
    /**
     * 本笔退款对应的退款请求号
     */
    @JsonProperty("out_request_no")
    private String outRequestNo;
    /**
     * 该笔退款所对应的交易的订单金额
     */
    @JsonProperty("total_amount")
    private Long totalAmount;
    /**
     * 本次退款请求，对应的退款金额
     */
    @JsonProperty("refund_amount")
    private Long refundAmount;
    /**
     * 退款状态。
     * 固定为REFUND_SUCCESS 退款处理成功
     */
    @JsonProperty("refund_status")
    private String refundStatus;

    /**
     * 退款时间
     */
    @JsonProperty("gmt_refund_pay")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private LocalDateTime gmtRefundPay;


    private String code;
    private String msg;

    @JsonProperty("sub_code")
    private String subCode;
    @JsonProperty("sub_msg")
    private String subMsg;

    /**
     * 支付宝交易号固定：	2013112011001004330000121536
     */
    @JsonProperty("trade_no")
    private String tradeNo;


    public RefundQueryRetVo(String outTradeNo, String outRequestNo, Long totalAmount, Long refundAmount, String refundStatus, LocalDateTime gmtRefundPay) {
        this.outTradeNo = outTradeNo;
        this.outRequestNo = outRequestNo;
        this.totalAmount = totalAmount;
        this.refundAmount = refundAmount;
        this.refundStatus = refundStatus;
        this.gmtRefundPay = gmtRefundPay;
        this.tradeNo = "2013112011001004330000121536";
    }

    public RefundQueryRetVo(AlipayReturnNo alipayReturnNo) {
        this.code="40004";
        this.msg="Business Failed";
        this.subCode=alipayReturnNo.getSubCode();
        this.subMsg=alipayReturnNo.getSubMsg();
    }

    public void setDefault()
    {
        code="10000";
        msg="Success";
        tradeNo="2013112011001004330000121536";
    }
}
