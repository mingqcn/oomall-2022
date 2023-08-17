package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import lombok.*;

import java.util.List;

/**
 * 发起退款(退分账)参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class AliPostRefundParam {
    /*必选*/
    /**
     * 退款金额
     * */
    @NonNull
    private Double refund_amount;

    /*二选一*/
    /**
     * 支付交易号
     * refundTrans.payTrans.trans_no
     * */
    private String trade_no;
    /**
     * 商户订单号
     * refundTrans.payTrans.out_no
     * */
    private String out_trade_no;

    /*可选*/
    /**
     * 商户订单号
     * refundTrans.out_no
     * */
    private String out_request_no;

    private String refund_reason;

    private String[] query_options; // refund_detail_item_list：退款使用的资金渠道；deposit_back_info：触发银行卡冲退信息通知

    /*退分账*/
    /**
     * 退分账明细
     * */
    private List<OpenApiRoyaltyDetailInfoPojo> refund_royalty_parameters;

    @Data
    @NoArgsConstructor
    public class OpenApiRoyaltyDetailInfoPojo {
        /*必选*/
        /**
         * 收入方账户(商家)
         * shop.submchid
         * */
        private String trans_in;

        /*可选*/
        private String trans_in_type="userId"; //userId表示是支付宝账号对应的支付宝唯一用户号;cardAliasNo表示是卡编号;loginName表示是支付宝登录号；
        private String trans_in_name; // 分账收款方姓名

        /**
         * 支出方账户(支付宝)
         * channel.spmchid
         * */
        private String trans_out;
        private String trans_out_type="userId"; // 支出方账户类型,userId表示是支付宝账号对应的支付宝唯一用户号;loginName表示是支付宝登录号

        private String royalty_type="transfer"; // 默认为transfer 分账
        private String desc; // 分账描述
        private String royalty_scene; // 可选值：达人佣金、平台服务费、技术服务费、其他
        /**
         * */
        private Double amount; // 分账金额
    }
}
