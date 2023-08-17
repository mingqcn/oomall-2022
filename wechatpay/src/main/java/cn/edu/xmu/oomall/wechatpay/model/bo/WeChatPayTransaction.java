package cn.edu.xmu.oomall.wechatpay.model.bo;

import cn.edu.xmu.oomall.wechatpay.model.VoObject;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayTransactionRetVo;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayTransactionVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ziyi guo
 * @date 2021/11/30
 */
@Data
@NoArgsConstructor
public class WeChatPayTransaction implements VoObject,Serializable {

    private Long id;
    private String appid;
    private String mchid;
    private String outTradeNo;
    private String transactionId;
    private String tradeType;
    private String tradeState;
    private String tradeStateDesc;
    private Integer total;
    private Integer payerTotal;
    private String currency;
    private String payerCurrency;
    private String openid;
    private LocalDateTime successTime;
    private String description;
    private LocalDateTime timeExpire;
    private String notifyUrl;
    private String prepayId;

    public WeChatPayTransaction(WeChatPayTransactionVo weChatPayTransactionVo){
        this.appid = weChatPayTransactionVo.getAppid();
        this.mchid = weChatPayTransactionVo.getMchid();
        this.description = weChatPayTransactionVo.getDescription();
        this.timeExpire = weChatPayTransactionVo.getTimeExpire();
        this.outTradeNo = weChatPayTransactionVo.getOutTradeNo();
        this.total = weChatPayTransactionVo.getAmount().getTotal();
        this.currency = weChatPayTransactionVo.getAmount().getCurrency();
        this.openid = weChatPayTransactionVo.getPayer().getOpenid();
        this.notifyUrl = weChatPayTransactionVo.getNotifyUrl();
    }
    @Override
    public WeChatPayTransactionRetVo createVo(){
        return new WeChatPayTransactionRetVo(this);
    }
    @Override
    public WeChatPayTransactionRetVo createSimpleVo(){
        return new WeChatPayTransactionRetVo(this);
    }

}
