package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import lombok.*;

import java.util.List;

/**
 * 发起分账参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class AliPostDivPayParam {
    /*必选*/
    /**
     * 结算请求流水号，由商家自定义
     * divpayTrans.out_no
     * */
    @NonNull
    String out_request_no;
    /**
     * 支付宝订单号
     * divpayTrans.payTrans.trans_no
     * */
    @NonNull
    String trade_no;
    /**
     * 分账明细信息
     * */
    @NonNull
    List<OpenApiRoyaltyDetailInfoPojo> royalty_parameters;


    @Data
    @NoArgsConstructor
    public class OpenApiRoyaltyDetailInfoPojo {
        /*必选*/
        /**
         * 收入方支付宝账号(平台支付宝账号)
         * channel.spmchid
         * */
        @NonNull
        private String trans_in;

        /*可选*/
        private String royalty_type="transfer";
        /**
         * 支出方账号
         * channel.submchid
         * */
        private String trans_out;
        private String trans_in_type="userId";
        private String trans_out_type="userId";
        private String desc;
    }
}
