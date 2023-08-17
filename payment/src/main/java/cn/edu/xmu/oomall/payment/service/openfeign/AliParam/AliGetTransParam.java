package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import lombok.*;

/**
 * 订单查询参数
 * @author Wenbo Li
 * */

@Data
@NoArgsConstructor
public class AliGetTransParam {

    /*二选一*/
    /**
     * payTrans.trans_no
     * */
    private String transId; // 订单号

    /**
     * payTrans.out_no
     * */
    private String out_trade_no; // 内部交易号

    /*可选*/
    /*
    trade_settle_info：返回的交易结算信息，包含分账、补差等信息；
    fund_bill_list：交易支付使用的资金渠道；
    voucher_detail_list：交易支付时使用的所有优惠券信息；
    discount_goods_detail：交易支付所使用的单品券优惠的商品优惠信息；
    mdiscount_amount：商家优惠金额；
    * */
    private String[] query_options;
}
