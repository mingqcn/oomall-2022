package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 请求分账信息返回值
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeGetDivPayRetObj {
    private String transaction_id;
    private String out_order_no;
    private String order_id;
    private String state; // PROCESSING, FINISH
    private Receivers receivers;

    @Data
    @NoArgsConstructor
    public class Receivers {
        private Long amount;
        private String description;
        private String type;
        private String account;
        private String result;
        private String fail_reason;
        private String detail_id;
        private LocalDateTime create_time;
        private LocalDateTime finish_time;
    }
}
