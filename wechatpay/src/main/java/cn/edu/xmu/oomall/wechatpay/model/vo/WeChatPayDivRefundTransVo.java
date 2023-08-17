package cn.edu.xmu.oomall.wechatpay.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class WeChatPayDivRefundTransVo {
    @NotBlank
    private String outOrderNo;
    @NotBlank
    private String outReturnNo;
    @NotBlank
    private String returnMchid;
    @NotNull
    private Integer amount;
    @NotBlank
    private String description;
}
