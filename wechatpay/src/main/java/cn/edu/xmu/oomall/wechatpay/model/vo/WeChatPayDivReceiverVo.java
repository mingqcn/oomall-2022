package cn.edu.xmu.oomall.wechatpay.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeChatPayDivReceiverVo {
    private String type;
    private String account;
    private String name;
    private Integer amount;
    private String description;
}
