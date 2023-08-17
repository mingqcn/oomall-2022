package cn.edu.xmu.oomall.wechatpay.model.bo;

import cn.edu.xmu.oomall.wechatpay.model.VoObject;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayDivReceiverRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayDivReceiverVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayDivPayTransRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayDivPayTransVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
public class WeChatPayDivPayTrans implements VoObject,Serializable {

    private String appid;

    private String transactionId;

    private String outOrderNo;

    private Collection<WeChatPayDivReceiverVo> receivers = new ArrayList<>();

    private Boolean unfreezeUnsplit;

    private String orderId;

    private String state;

    private LocalDateTime successTime;

    private Integer payerTotal;

    private Collection<WeChatPayDivReceiverRetVo> retReceivers = new ArrayList<>();

    public WeChatPayDivPayTrans(WeChatPayDivPayTransVo weChatPayDivPayTransVo){
        this.appid = weChatPayDivPayTransVo.getAppid();
        this.transactionId = weChatPayDivPayTransVo.getTransactionId();
        this.outOrderNo = weChatPayDivPayTransVo.getOutOrderNo();
        this.receivers = weChatPayDivPayTransVo.getReceivers();
    }
    @Override
    public WeChatPayDivPayTransRetVo createVo() {
        return new WeChatPayDivPayTransRetVo(this);
    }
    @Override
    public WeChatPayDivPayTransRetVo createSimpleVo() {
        return new WeChatPayDivPayTransRetVo(this);
    }
}
