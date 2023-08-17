//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import lombok.*;

/**
 * 下单参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class AliPostTransParam {
    /*必选*/
    /**
     * 商户订单号
     * payTrans.out_no
     * */
    @NonNull
    private String out_trade_no;
    /**
     * 订单描述
     * */
    @NonNull
    private String subject;
    /**
     * 订单总金额
     * payTrans.amount
     * */
    @NonNull
    private String total_amount;
}
