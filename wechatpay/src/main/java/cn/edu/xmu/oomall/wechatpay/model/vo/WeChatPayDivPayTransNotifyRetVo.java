package cn.edu.xmu.oomall.wechatpay.model.vo;

import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayDivPayTrans;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayTransaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class WeChatPayDivPayTransNotifyRetVo {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Resource{
        private String algorithm;
        private String originalType;
        private WeChatPayDivPayTransRetVo ciphertext;
        private String nonce;
    }

    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
    private LocalDateTime createTime;
    private String eventType;
    private String summary;
    private String resourceType;
    private Resource resource;

    public WeChatPayDivPayTransNotifyRetVo(WeChatPayDivPayTrans weChatPayDivPayTrans) {
        this.id = "EV-2018022511223320873";
        this.createTime = LocalDateTime.now();
        this.eventType = "TRANSACTION." + weChatPayDivPayTrans.getState();
        this.summary = "分账";
        this.resourceType = "encrypt-resource";
        this.resource = new Resource("AEAD_AES_256_GCM","profitsharing", weChatPayDivPayTrans.createVo(), "fdasflkja484w");
    }
}
