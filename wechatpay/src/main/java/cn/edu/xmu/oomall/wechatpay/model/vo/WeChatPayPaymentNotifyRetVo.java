package cn.edu.xmu.oomall.wechatpay.model.vo;

import cn.edu.xmu.javaee.core.util.Common;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayTransaction;
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
public class WeChatPayPaymentNotifyRetVo {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Resource{
        private String algorithm;
        private String originalType;
        private WeChatPayTransactionRetVo ciphertext;
        private String nonce;
    }
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
    private LocalDateTime createTime;
    private String eventType;
    private String summary;
    private String resourceType;
    private Resource resource;

    public WeChatPayPaymentNotifyRetVo(WeChatPayTransaction weChatPayTransaction) {
        this.id = "EV-2018022511223320873";
        this.createTime = LocalDateTime.now();
        this.eventType = "TRANSACTION." + weChatPayTransaction.getTradeState();
        this.summary = null;
        this.resourceType = null;
        this.resource = new Resource("AEAD_AES_256_GCM","transaction", weChatPayTransaction.createVo(), "fdasflkja484w");
    }

}
