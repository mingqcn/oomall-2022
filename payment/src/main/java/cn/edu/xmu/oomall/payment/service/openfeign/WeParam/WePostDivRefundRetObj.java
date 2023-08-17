package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 请求分账退回参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WePostDivRefundRetObj {
    private String order_id;
    private String out_order_no;
    private String out_return_no;
    private String return_id;
    private String return_mchid;
    private Long amount;
    private String description;
    private String result;
    private String fail_reason; // 枚举值：ACCOUNT_ABNORMAL：原分账接收方账户异常;TIME_OUT_CLOSED：超时关单;PAYER_ACCOUNT_ABNORMAL：原分账分出方账户异常
    private LocalDateTime create_time;
    private LocalDateTime finish_time;
}
