package cn.edu.xmu.oomall.wechatpay.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeChatPayDivReceiverRetVo {

    private String type;
    private String account;
    private Integer amount;
    private String description;
    private String result;
    private String failReason;
    //@JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
    private String createTime;
    //@JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="yyyy-MM-dd'T'HH:mm:ss.SSS" ,timezone = "GMT+8")
    private String finishTime;
    private String detailId;

    public WeChatPayDivReceiverRetVo(WeChatPayDivReceiverVo weChatPayDivReceiverVo){
        this.type = weChatPayDivReceiverVo.getType();
        this.account = weChatPayDivReceiverVo.getAccount();
        this.amount = weChatPayDivReceiverVo.getAmount();
        this.description = weChatPayDivReceiverVo.getDescription();
    }
}
