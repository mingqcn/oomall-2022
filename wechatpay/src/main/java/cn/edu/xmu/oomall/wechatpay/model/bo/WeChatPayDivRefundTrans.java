package cn.edu.xmu.oomall.wechatpay.model.bo;

import cn.edu.xmu.oomall.wechatpay.model.VoObject;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayDivRefundTransRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayDivRefundTransVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class WeChatPayDivRefundTrans  implements VoObject, Serializable {

    private String orderId;
    private String outOrderNo;
    private String outReturnNo;
    private String returnMchid;
    private Integer amount;
    private String description;


    private String returnId;
    private String result;
    private String createTime;
    private String finishTime;
    @Override
    public Object createVo() {
        return new WeChatPayDivRefundTransRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return new WeChatPayDivRefundTransRetVo(this);
    }

    public WeChatPayDivRefundTrans(WeChatPayDivRefundTransVo weChatPayDivRefundTransVo){
        this.outOrderNo = weChatPayDivRefundTransVo.getOutOrderNo();
        this.amount = weChatPayDivRefundTransVo.getAmount();
        this.outReturnNo = weChatPayDivRefundTransVo.getOutReturnNo();
        this.returnMchid = weChatPayDivRefundTransVo.getReturnMchid();
        this.description = weChatPayDivRefundTransVo.getDescription();
    }
}
