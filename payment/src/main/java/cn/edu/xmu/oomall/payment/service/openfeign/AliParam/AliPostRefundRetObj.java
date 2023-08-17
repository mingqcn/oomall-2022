package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * 发起退款返回值
 * @author Wenbo Li
 * */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AliPostRefundRetObj extends PublicRetObj {
    /*必选*/
    private String trade_no;
    private String out_trade_no;
    private String buyer_logon_id; // 商家支付宝账号
    private String refund_change;
    private Double refund_fee; // 退款金额
    private String buyer_user_id; // 买家支付宝账号

    /*可选*/
    private String send_back_fee;
}
