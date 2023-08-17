package cn.edu.xmu.oomall.wechatpay.model.vo;

import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayReceiver;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class WeChatPayReceiverRetVo {

    private String type;

    private String account;
    private String name;

    private String relationType;

    //private String customRelation;

    public WeChatPayReceiverRetVo(WeChatPayReceiver weChatPayReceiver){
        this.type = weChatPayReceiver.getType();
        this.account = weChatPayReceiver.getAccount();
        this.relationType = weChatPayReceiver.getRelationType();
        this.name = weChatPayReceiver.getName();
        //this.customRelation = weChatPayReceiver.getCustomRelation();
    }
}
