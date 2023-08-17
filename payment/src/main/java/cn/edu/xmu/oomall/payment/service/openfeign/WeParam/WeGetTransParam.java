package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * 订单相关操作的参数
 * @author Wenbo Li
 * */

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeGetTransParam {
    /*必选*/
    /**
     * 商铺号
     * payTrans.shopChannel.channel.SpMchid
     * */
    @NonNull
    private String mchid;

    /*二选一*/
    /**
     * 订单号
     * payTrans.trans_no
     * */
    private String transId;

    /**
     * 内部交易号
     * payTrans.out_no
     * */
    private String out_trade_no;

}
