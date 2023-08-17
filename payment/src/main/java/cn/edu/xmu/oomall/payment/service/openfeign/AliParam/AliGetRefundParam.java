package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 退款(退分账)查询参数
 * @author Wenbo Li
 * */

@Data
@NoArgsConstructor
public class AliGetRefundParam {
    /*必选*/
    /**
     * 退款请求号
     * refundTrans.trans_no
     * if null: refundTrans.payTrans.out_no
     * divRefundTrans.refundTrans.payTrans.out_no
     * */
    @NonNull
    private String out_request_no;

    /*二选一*/
    /**
     * 交易订单号
     * refundTrans.payTrans.trans_no
     * divRefundTrans.refundTrans.payTrans.trans_no
     * */
    private String trade_no;

    /**
     * 内部交易订单号
     * refundTrans.payTrans.out_no
     * divRefundTrans.refundTrans.payTrans.out_no
     * */
    private String out_trade_no;

    /*可选*/
    private String[] query_options;

    private LocalDateTime gmt_refund_pay; // query_options中指定"gmt_refund_pay"值时才返回
}
