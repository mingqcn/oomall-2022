package cn.edu.xmu.oomall.payment.controller.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class WepayNotifyVo {

    @Data
    @NoArgsConstructor
    public class WePayResource {

        @Data
        @NoArgsConstructor
        public class Payer {
            @JsonProperty(value = "sp_openid")
            private String spOpenId;
        };

        @Data
        @NoArgsConstructor
        public class Amount {
            @JsonProperty(value = "total")
            private Long total;

            @JsonProperty(value = "payer_total")
            private Long payerTotal;
        };

        @JsonProperty(value = "sp_appid")
        private String spAppId;

        @JsonProperty(value = "sp_mchid")
        private String spMchId;

        @JsonProperty(value = "sub_mchid")
        private String subMchId;

        @NotNull(message="内部交易号必填")
        @JsonProperty(value = "out_trade_no")
        private String outTradeNo;

        @JsonProperty(value = "transaction_id")
        private String transactionId;

        @NotNull(message="交易状态必填")
        @JsonProperty(value = "trade_state")
        private String tradeState;

        @NotNull(message="成功时间不能为空")
        @JsonProperty(value = "success_time")
        private String successTime;

        @Min(value = 0, message="付款金额需大于0")
        @JsonProperty(value = "amount")
        private Amount amount;

        @JsonProperty(value = "payer")
        private Payer payer;
    }

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "create_time")
    private String createTime;

    @JsonProperty(value = "resource")
    private WePayResource resource;
}
