package cn.edu.xmu.oomall.wechatpay.model.vo;

import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayTransaction;
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
public class WeChatPayTransactionRetVo {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionAmountRetVo{
        private Integer total;
        private Integer payerTotal;
        private String currency;
        private String payerCurrency;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayerRetVo{
        private String openid;
    }

    private String appid;

    private String mchid;

    private String outTradeNo;

    private String transactionId;

    private String tradeType;

    private String tradeState;

    private String tradeStateDesc;

    private TransactionAmountRetVo amount;

    private PayerRetVo payer;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
    private LocalDateTime successTime;


    public WeChatPayTransactionRetVo(WeChatPayTransaction weChatPayTransaction){
        this.appid = "wxd678efh567hg6787";
        this.mchid = "1230000109";
        this.outTradeNo = weChatPayTransaction.getOutTradeNo();
        this.transactionId = String.valueOf(weChatPayTransaction.getId());
        this.tradeType = "JSAPI";
        this.tradeState = weChatPayTransaction.getTradeState();
        this.tradeStateDesc = null;
        this.amount = new TransactionAmountRetVo(weChatPayTransaction.getTotal(), weChatPayTransaction.getPayerTotal(), "CNY", "CNY");
        this.payer = new PayerRetVo("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
        this.successTime = weChatPayTransaction.getSuccessTime();
    }
}
