package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


import javax.sound.midi.Receiver;
import java.util.List;

/**
 * 发起分账参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class WePostDivPayParam {
    /*必选*/
    /**
     * 微信分配的商户id
     * channel.appid
     * */
    @NonNull
    private String appid;

    /**
     * 支付订单号
     * divPayTrans.payTrans.trans_no
     * */
    @NonNull
    private String transaction_id;

    /**
     * 分账账号
     * divPayTrans.out_no
     */
    @NonNull
    private String out_order_no;

    /**
     * 账户接收方
     * */
    @NonNull
    private List<Receiver> receivers;

    /**
     * 是否解冻剩余未分账资金
     * */
    @NonNull
    private boolean unfreeze_unsplit=false;

    @Data
    @NoArgsConstructor
    public class Receiver {
        /*必选*/
        @NonNull
        private String type="MERCHANT_ID"; //MERCHANT_ID, PERSONAL_OPENID
        /**
         * 商户号
         * channel.mchid
         * */
        @NonNull
        private String account;
        /**
         * 分账金额
         * divPayTrans.amount
         * */
        @NonNull
        private Long amount;
        @NonNull
        private String description;
        /*可选*/
        private String name;
    }
}
