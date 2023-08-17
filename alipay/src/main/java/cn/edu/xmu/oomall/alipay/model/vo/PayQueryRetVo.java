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
public class PayQueryRetVo {
    /**
     * 商铺交易号
     */
    @JsonProperty("out_trade_no")
    private String outTradeNo;
    /**
     * 格式2014-11-27 15:45:57,未支付成功就是空
     */
    @JsonProperty("send_pay_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private LocalDateTime sendPayDate;


    /**
     * 交易状态：
     * TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
     * TRADE_SUCCESS（交易支付成功）
     * TRADE_FAILED（交易支付失败）
     * TRADE_FINISHED（交易结束，不可退款）
     */
    @JsonProperty("trade_status")
    private String tradeStatus;

    /**
     * 交易的订单金额，单位为分
     */
    @JsonProperty("total_amount")
    private Long totalAmount;


    /**
     * 订单实际支付金额
     */
    @JsonProperty("buyer_pay_amount")
    private Long buyerPayAmount;


    private String code;
    private String msg;

    @JsonProperty("sub_code")
    private String subCode;
    @JsonProperty("sub_msg")
    private String subMsg;




    /**
     * 固定：1595620
     */
    @JsonProperty("buyer_logon_id")
    private String buyerLogonId;
    /**
     * 支付宝交易号固定：	2013112011001004330000121536
     */
    @JsonProperty("trade_no")
    private String tradeNo;

    /**
     * 固定:creditAdvanceV2
     */
    @JsonProperty("credit_pay_mode")
    private String creditPayMode;
    /**
     * 固定：ZMCB99202103310000450000041833
     */
    @JsonProperty("credit_biz_order_id")
    private String creditBizOrderId;



    public PayQueryRetVo(AlipayReturnNo alipayReturnNo) {
        code="10000";
        msg="Success";
        this.code="40004";
        this.msg="Business Failed";
        this.subCode=alipayReturnNo.getSubCode();
        this.subMsg=alipayReturnNo.getSubMsg();
    }

    public void setDefault()
    {
        buyerLogonId="1595620";
        tradeNo="2013112011001004330000121536";
        creditPayMode="creditAdvanceV2";
        creditBizOrderId="ZMCB99202103310000450000041833";
    }
}
