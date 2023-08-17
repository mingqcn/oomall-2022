package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import lombok.*;

/**
 * 取消订单信息参数
 * @author Wenbo Li
 * */

@Data
@NoArgsConstructor
public class AliCancelOrderParam {
    /*二选一*/
    /**
     * 支付宝交易号
     * payTrans.trans_no
     * */
    private String trade_no;

    /**
     * 内部交易号
     * payTrans.out_no
     * */
    private String out_trade_no;

    /*可选*/
    private String operation_id;
}
