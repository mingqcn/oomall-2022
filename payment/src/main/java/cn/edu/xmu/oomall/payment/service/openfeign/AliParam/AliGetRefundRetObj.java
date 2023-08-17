package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import cn.edu.xmu.oomall.payment.dao.bo.DivRefundTrans;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 查询退款返回值
 * @author Wenbo Li
 * */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AliGetRefundRetObj extends PublicRetObj {
    /*可选*/
    private String trade_no;
    private String out_trade_no; // 订单号
    private String out_request_no; // 退款请求号
    private Double total_amount; // 交易总金额
    private Double refund_amount; // 退款总金额
    private String refund_status = ""; //REFUND_SUCCESS, 为空说明失败
    /**
     * 退分账明细
     * */
    private List<RefundRoyaltyResult> refund_royaltys;
    private LocalDateTime gmt_refund_pay; // 需要在入参的query_options中指定"gmt_refund_pay"值时才返回该字段信息。
    private String refund_hyb_amount;

    public Byte getDivRefundByteResult() {
        return DivRefundTrans.SUCCESS;
    }
    @Data
    @NoArgsConstructor
    public class RefundRoyaltyResult {
        /*必选*/
        /**
         * 退分账金额
         * */
        private String refund_amount;
        private String result_code; // 退分账结果码

        /*可选*/
        private String royalty_type;
        private String trans_out;
        private String trans_in;
        private String trans_out_email;
        private String trans_in_email;
    }
}
