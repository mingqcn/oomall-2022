package cn.edu.xmu.oomall.wechatpay.model.vo;

import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayRefund;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author ziyi guo
 * @date 2021/12/1
 */
@Data
@NoArgsConstructor
public class WeChatPayRefundNotifyRetVo {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Amount{
        private Integer total;
        private Integer refund;
        private Integer payerTotal;
        private Integer payerRefund;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ciphertext{
        private String mchid;
        private String outTradeNo;
        private String transactionId;
        private String outRefundNo;
        private String refundId;
        private String refundStatus;
        private String userReceivedAccount;
        private Amount amount;
        @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
        private LocalDateTime successTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Resource{
        private String algorithm;
        private String originalType;
        private Ciphertext ciphertext;
        private String nonce;
    }

    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
    private LocalDateTime createTime;
    private String eventType;
    private String summary;
    private String resourceType;
    private Resource resource;

    public WeChatPayRefundNotifyRetVo(WeChatPayRefund weChatPayRefund){
        this.id = "EV-2018022511223320873";
        this.createTime = LocalDateTime.now();
        this.eventType = "Refund." + weChatPayRefund.getStatus();
        this.summary = null;
        this.resourceType = null;
        this.resource = new Resource("AEAD_AES_256_GCM","transaction",
                new Ciphertext("1230000109", weChatPayRefund.getOutTradeNo(), "1217752501201407033233368018", weChatPayRefund.getOutRefundNo(), String.valueOf(weChatPayRefund.getId()), weChatPayRefund.getStatus(), "招商银行信用卡0403",
                        new Amount(weChatPayRefund.getTotal(), weChatPayRefund.getRefund(), weChatPayRefund.getPayerTotal(), weChatPayRefund.getRefund()),weChatPayRefund.getSuccessTime()),
                "fdasfjihihihlkja484w");
    }

}
