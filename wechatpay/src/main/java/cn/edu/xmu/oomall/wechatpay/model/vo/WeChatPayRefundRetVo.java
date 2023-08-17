package cn.edu.xmu.oomall.wechatpay.model.vo;

import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayRefund;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author ziyi guo
 * @date 2021/11/30
 */
@Data
@NoArgsConstructor
public class WeChatPayRefundRetVo {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundAmountRetVo{
        private Integer total;
        private Integer refund;
        private Integer payerTotal;
        private Integer payerRefund;
        private Integer settlementRefund;
        private Integer settlementTotal;
        private Integer discountRefund;
        private String currency;
    }

    private String refundId;

    private String outRefundNo;

    private String transactionId;

    private String outTradeNo;

    private String channel;

    private String userReceivedAccount;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
    private LocalDateTime successTime;

    private String status;

    private RefundAmountRetVo amount;

    public WeChatPayRefundRetVo(WeChatPayRefund weChatPayRefund){
        this.refundId = String.valueOf(weChatPayRefund.getId());
        this.outRefundNo = weChatPayRefund.getOutRefundNo();
        this.transactionId = "1217752501201407033233368018";
        this.outTradeNo = weChatPayRefund.getOutTradeNo();
        this.channel = "ORIGINAL";
        this.userReceivedAccount = "招商银行信用卡0403";
        this.successTime = weChatPayRefund.getSuccessTime();
        this.status = weChatPayRefund.getStatus();
        this.amount = new RefundAmountRetVo(weChatPayRefund.getTotal(), weChatPayRefund.getRefund(), weChatPayRefund.getPayerTotal(), weChatPayRefund.getRefund(), null, null, null, null);
    }
}
