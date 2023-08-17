package cn.edu.xmu.oomall.alipay.model.vo;

import cn.edu.xmu.oomall.alipay.util.AlipayReturnNo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloseRetVo {
    @JsonProperty("out_trade_no")
    private String outTradeNo;

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


    public CloseRetVo(AlipayReturnNo alipayReturnNo) {
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
