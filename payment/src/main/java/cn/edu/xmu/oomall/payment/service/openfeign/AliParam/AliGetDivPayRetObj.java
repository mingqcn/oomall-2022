package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 查询分账信息返回值
 * @author Wenbo Li
 * */

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AliGetDivPayRetObj extends PublicRetObj {
    /*必选*/
    private String out_request_no;
    private LocalDateTime operation_dt;
    private List<RoyaltyDetail> royalty_detail_list;

    @Data
    @NoArgsConstructor
    public class RoyaltyDetail {
        /*必选*/
        private String operation_type="transfer"; // replenish(补差)、replenish_refund(退补差)、transfer(分账)、transfer_refund(退分账)
        private Double amount;
        private String state; // 分账状态，SUCCESS成功，FAIL失败，PROCESSING处理中

        /*可选*/
        /**
         * 分账执行时间
         * */
        private LocalDateTime exec_dt;
        private String trans_in; // 分账接受账号,只有在operation_type为replenish_refund(退补差)，transfer(分账)才返回该字段
        private String trans_out; // 分账接受账号,只有在operation_type为replenish(补差),transfer_refund(退分账)类型才返回该字段
        private String trans_in_type; // 分账接受账号
        private String trans_out_type; // 分账接受账号
        private String error_code;
        private String error_desc;
    }
}
