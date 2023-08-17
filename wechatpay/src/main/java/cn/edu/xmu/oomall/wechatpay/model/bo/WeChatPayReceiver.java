package cn.edu.xmu.oomall.wechatpay.model.bo;

import cn.edu.xmu.oomall.wechatpay.model.VoObject;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayDivPayTransRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayReceiverRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayReceiverVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class WeChatPayReceiver implements VoObject,Serializable {
    private Long id;
    private String appid;
    private String type;
    private String account;
    private String name;
    private String relationType;
    private String 	customRelation;
    @Override
    public Object createVo() {
        return new WeChatPayReceiverRetVo(this);
    }
    @Override
    public Object createSimpleVo() {
        return new WeChatPayReceiverRetVo(this);
    }

    public WeChatPayReceiver(WeChatPayReceiverVo weChatPayReceiverVo){
        this.type = weChatPayReceiverVo.getType();
        this.account = weChatPayReceiverVo.getAccount();
        this.name = weChatPayReceiverVo.getName();
        this.relationType = weChatPayReceiverVo.getRelationType();
        this.customRelation = weChatPayReceiverVo.getCustomRelation();
    }
}
