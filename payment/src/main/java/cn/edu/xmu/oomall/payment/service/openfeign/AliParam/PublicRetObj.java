package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公共响应参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicRetObj {
    /*必选*/
    /**
     * 网关返回码
     * */
    private String code;
    /**
     * 网关返回码描述
     * */
    private String message;
    /**
     * 签名
     * */
    private String sign;

    /*可选*/
    /**
     * 业务返回码
     * */
    private String sub_code;
    /**
     * 业务返回码描述
     * */
    private String sub_message;
}
