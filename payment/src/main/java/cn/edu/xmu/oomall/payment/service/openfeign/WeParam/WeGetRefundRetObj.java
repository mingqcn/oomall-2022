package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 查询退款返回值
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeGetRefundRetObj {
    private String refund_id;
    private String out_refund_no;
    private String transaction_id;
    private String out_trade_no;
    private String channel;
    private String user_received_account;
    private LocalDateTime success_time;
    private LocalDateTime create_time;
    private String status;
    private Amount amount;

    @Data
    @NoArgsConstructor
    public class Amount {
        Long total;
        Long refund;
        Long payer_total;
        Long payer_refund;
        Long settlement_refund;
        Long settlement_total;
        Long discount_refund;
        String currency;
    }
}
