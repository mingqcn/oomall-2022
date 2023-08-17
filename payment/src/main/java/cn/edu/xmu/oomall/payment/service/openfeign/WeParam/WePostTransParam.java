//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 预下单下单参数
 * */
@Data
@NoArgsConstructor
public class WePostTransParam {
    /*必选*/
    /**
     * 服务商应用ID
     * payTrans.shopChannel.channel.appid
     */
    @NonNull
    private String appid;

    /**
     * 服务商户号
     * payTrans.shopChannel.channel.SpMchid
     */
    private String mchid;

    /**
     * 商品描述
     * any
     */
    private String description;

    /**
     * 商户订单号
     * payTrans.out_trade_no
     */
    private String out_trade_no;

    /**
     * 通知地址
     * payTrans.shopChannel.channel.notifyUrl
     */
    private String notify_url;

    /**
     *  订单金额
     */
    private Amount amount;

    /*可选*/



    /**
     * 支付者
     * @PayTrans: payTrans.getSpOpenid()
     */
    private Payer payer;

    @Data
    @NoArgsConstructor
    public class Amount {
        /*必选*/
        /**
         * 总金额
         *payTrans.amount
         */
        private Long total;

        /*可选*/
        /**
         *  货币类型
         *  "CNY"
         */
        private String currency;
    }

    @Data
    @NoArgsConstructor
    public class Payer {
        /**
         *  用户服务标识
         *  payTrans.sp_openid
         */
         private String openid;
    }
}
