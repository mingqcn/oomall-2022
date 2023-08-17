package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 查询订单返回值
 * @author Wenbo Li
 * */

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AliGetTransRetObj extends PublicRetObj {
    /*必选*/
    private String trade_no;
    private String out_trade_no;
    private String buyer_logo_id; // 商家支付宝账号
    private String buyer_user_id; // 买家支付宝账号
    private String trade_status; // WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
    private Double total_amount; // yuan


    /*可选*/
    private String trans_currency;
    private Double settle_amount;
    private Double pay_currency;
    private Double pay_amount;
    /**
     * 结算币种
     * */
    private String settle_currency;
    private LocalDateTime send_pay_date;
    private String settle_trans_rate;
    private String trans_pay_rate;
    private Double buyer_pay_amount;
    private Double point_amount;
    private Double invoice_amount;
    private Double receipt_amount;
    private String charge_amount;
    private String settlement_id;
    /**
     * 商家优惠金额
     * */
    private String mdiscount_amount;
    /**
     * 平台优惠金额
     * */
    private String discount_amount;
    private String subject;
    private String body;
    private String ext_infos; // json
    private String passback_params;
}
