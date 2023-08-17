package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 公共请求参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class PublicRequestParam {
    /*必选*/
    /**
     * 支付宝分配给开发者的应用ID
     * channel.app_id
     * */
    @NonNull
    private String app_id;
    @NonNull
    private String method; // 接口方法
    @NonNull
    private String charset = "utf-8"; //gbk, gb3212
    @NonNull
    private String sign_type = "RSA2";
    /**
     * 用户签名，解析出mchid
     * channel.sp_mchid
     * */
    @NonNull
    private String sign;
    /**
     * 请求时间
     * LocalDateTime.now.(yyyy-MM-dd HH:mm:ss)
     * */
    @NonNull
    private String timestamp;
    @NonNull
    private String version = "1.0";
    /**
     * 非公共请求参数均放在这里
     * */
    @NonNull
    private String biz_content;

    /*可选*/
    private String notify_url;

    public void setPublicParam(String app_id, String method, String sign, String timestamp, String biz_content) {
        this.app_id = app_id;
        this.method = method;
        this.sign = sign;
        this.timestamp = timestamp;
        this.biz_content = biz_content;
    }
}
