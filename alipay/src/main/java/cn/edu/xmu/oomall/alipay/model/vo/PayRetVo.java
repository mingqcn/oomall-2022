package cn.edu.xmu.oomall.alipay.model.vo;

import cn.edu.xmu.oomall.alipay.util.AlipayReturnNo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayRetVo {
    /**
     * 商铺交易号
     */
    @JsonProperty("out_trade_no")
    private String outTradeNo;

    /**
     * 该笔订单的资金总额，单位为人民币（分）
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;


    private String code;
    private String msg;

    @JsonProperty("sub_code")
    private String subCode;
    @JsonProperty("sub_msg")
    private String subMsg;


    /**
     * 固定：2088111111116894
     */
    @JsonProperty("seller_id")
    private String sellerId;
    /**
     * 固定：20161008001
     */
    @JsonProperty("merchant_order_no")
    private String merchantOrderNo;
    /**
     * 支付宝交易号固定：	2013112011001004330000121536
     */
    @JsonProperty("trade_no")
    private String tradeNo;

    public PayRetVo(AlipayReturnNo alipayReturnNo) {
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
        merchantOrderNo="20161008001";
        sellerId="2088111111116894";
    }
}
