package cn.edu.xmu.oomall.wechatpay.model.vo;

import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayDivRefundTrans;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class WeChatPayDivRefundTransRetVo {
    private String orderId;
    private String outOrderNo;
    private String outReturnNo;
    private String returnId;
    private String returnMchid;
    private Integer amount;
    private String description;
    private String result;
    private String createTime;
    private String finishTime;

    public WeChatPayDivRefundTransRetVo(WeChatPayDivRefundTrans weChatPayDivRefundTrans){
        this.orderId = "3008450740201411110007820472";
        this.outOrderNo = weChatPayDivRefundTrans.getOutOrderNo();
        this.outReturnNo = weChatPayDivRefundTrans.getOutReturnNo();
        this.returnId = weChatPayDivRefundTrans.getReturnId();
        this.returnMchid = weChatPayDivRefundTrans.getReturnMchid();
        this.amount = weChatPayDivRefundTrans.getAmount();
        this.description = weChatPayDivRefundTrans.getDescription();
        this.result = weChatPayDivRefundTrans.getResult();
        this.createTime = weChatPayDivRefundTrans.getCreateTime();
        this.finishTime = weChatPayDivRefundTrans.getFinishTime();
    }
}
