package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 取消订单请求参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class WeCancelOrderParam {
    /*必选*/
    /**
     * 直连商户
     * payTrans.shopChannel.channel.SpMchid
     * */
    @NonNull
    private String mchid;

    /**
     * 订单号
     * payTrans.out_no
     * */
    @NonNull
    private String out_trade_no;
}
