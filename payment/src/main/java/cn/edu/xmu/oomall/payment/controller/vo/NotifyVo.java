package cn.edu.xmu.oomall.payment.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotifyVo {
    String appid;
    String mchid;
    String out_trade_no;
    String transaction_id;
    String trade_type;
    String trade_state;
    String trade_state_desc;
    String bank_type;
    String success_time;
    Payer payer;
    Amount amount;
    @Data
    @NoArgsConstructor
    public class Payer {
        /**
         *  用户服务标识
         */
        private String sp_openid;
    }
    @Data
    @NoArgsConstructor
    public class Amount {
        /**
         * 总金额
         */
        private Long total;
        private Long payer_total;
        /**
         *  货币类型
         */
        private String currency;
        private String payer_currency;
    }
}
