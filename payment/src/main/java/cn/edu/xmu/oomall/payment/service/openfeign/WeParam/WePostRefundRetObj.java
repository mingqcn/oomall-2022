package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import cn.edu.xmu.oomall.payment.dao.bo.RefundTrans;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 请求退款返回值
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WePostRefundRetObj {
    /*必选*/
    private String refund_id;
    private String out_refund_no;
    private String transaction_id;
    private String out_trade_no;
    private String channel; // ORIGINAL：原路退款;BALANCE：退回到余额;OTHER_BALANCE：原账户异常退到其他余额账户;OTHER_BANKCARD：原银行卡异常退到其他银行卡
    private String user_received_account; // 招商银行信用卡0403
    private LocalDateTime create_time;
    private String status; // SUCCESS：退款成功;CLOSED：退款关闭;PROCESSING：退款处理中;ABNORMAL：退款异常
    private Amount amount;

    public Byte getByteStatus() {
        switch(status) {
            case "SUCCESS": return RefundTrans.SUCCESS;
            case "CLOSED": return RefundTrans.CANCEL;
            case "PROCESSING": return RefundTrans.NEW;
            case "ABNORMAL": return RefundTrans.FAIL;
        }
        return null;
    }

    /*可选*/
    private LocalDateTime success_time;

    @Data
    @NoArgsConstructor
    public class Amount {
        /*必选*/
        private Long total;
        private Long refund;
        private Long payer_total;
        private Long payer_refund;
        private Long settlement_refund; // 去掉代金券后退款金额
        private Long settlement_total; // 去掉代金券后订单金额
        private Long discount_refund; // 优惠退款金额
        private Long currency;
        /*可选*/
        private List<From> from;
        private Long refund_fee;

        @Data
        @NoArgsConstructor
        public class From {
            /*必选*/
            private String account;
            private String amount;
        }
    }
}
