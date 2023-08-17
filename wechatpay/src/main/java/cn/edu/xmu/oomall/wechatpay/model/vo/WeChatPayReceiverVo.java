package cn.edu.xmu.oomall.wechatpay.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeChatPayReceiverVo {
    @NotBlank
    private String appid;
    @NotBlank
    private String type;
    @NotBlank
    private String account;
    private String name;
    @NotBlank
    private String relationType;
    private String customRelation;

}
