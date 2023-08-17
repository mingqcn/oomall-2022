package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询订单返回值
 * @author Wenbo Li
 * */

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeGetTransRetObj {
    /*必选*/
    private String appid;
    private String mchid;
    private String out_trade_no;
    private String transaction_id;
    private String trade_type; // JSAPI
    private String trade_state; // SUCCESS
    private String trade_state_desc; // 订单状态描述
    /*可选*/
    private String success_time;
    Payer payer = new Payer();
    Amount amount = new Amount();
    @Data
    @NoArgsConstructor
    public class Payer {
        /**
         *  用户服务标识
         */
        private String openid;
    }

    @Data
    @NoArgsConstructor
    public class Amount {
        private Long total;
        private Long payer_total;
        private String currency;
        private String payer_currency;
    }
}
