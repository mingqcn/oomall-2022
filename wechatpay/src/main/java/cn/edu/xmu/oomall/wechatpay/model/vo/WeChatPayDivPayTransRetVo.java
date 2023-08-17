package cn.edu.xmu.oomall.wechatpay.model.vo;

import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayDivPayTrans;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
public class WeChatPayDivPayTransRetVo {
    private String transactionId;

    private String outOrderNo;

    private String orderId;

    private String state;

    private Collection<WeChatPayDivReceiverRetVo> receivers = new ArrayList<>();

    public WeChatPayDivPayTransRetVo(WeChatPayDivPayTrans weChatPayDivTrans){
        this.transactionId = weChatPayDivTrans.getTransactionId();
        this.outOrderNo = weChatPayDivTrans.getOutOrderNo();
        this.orderId = weChatPayDivTrans.getOrderId();
        this.state = weChatPayDivTrans.getState();
        this.receivers = weChatPayDivTrans.getRetReceivers();
    }
}
