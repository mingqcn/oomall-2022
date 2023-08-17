package cn.edu.xmu.oomall.wechatpay.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
public class WeChatPayDivPayTransVo {
    @NotBlank
    private String appid;

    @NotBlank
    private String transactionId;

    @NotBlank
    private String outOrderNo;

    @NotNull
    private Collection<WeChatPayDivReceiverVo> receivers;

    @NotNull
    private Boolean unfreezeUnsplit;
}
