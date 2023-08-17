package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

/**
 * 发起退款参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class WePostRefundParam {
   /*二选一*/
    /**
     * 微信订单号
     * refundTrans.payTrans.trans_no
     * */
    private String transaction_id;

    /**
     * 商户订单号
     * refundTrans.payTrans.out_no
     * */
    private String out_trade_no;

    /*必选*/
    /**
     * 商户退款单号
     * refundTrans.out_no
     * */
    @NonNull
    private String out_refund_no;
    @NonNull
    private Amount amount;

    /*可选*/
    private String reason;
    private String notify_url;


    @Data
    @NoArgsConstructor
    public class Amount {
        /**
         * 总金额
         * refundTrans.payTrans.amount
         */
        @NonNull
        private Long total;
        /**
         *  货币类型
         *  "CNY"
         */
        @NonNull
        private String currency;
        /**
         * 退款金额
         * refundTrans.amount
         * */
        @NonNull
        private Long refund;
        /*可选*/
        private List<From> from;

        @Data
        @NoArgsConstructor
        public class From {
            /*必选*/
            private String account; // AVAILABLE : 可用余额, UNAVAILABLE : 不可用余额
            private String amount;
        }
    }
}
