package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import cn.edu.xmu.oomall.payment.dao.bo.DivRefundTrans;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 请求分账退回返回值
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeGetDivRefundRetObj {
    private String order_id;
    private String out_order_no;
    private String out_return_no;
    private String return_id;
    private String return_mchid;
    private Long amount;
    private String description;
    private String result; // PROCESSING：处理中; SUCCESS：已成功; FAILED：已失败
    private String fail_reason;
    private LocalDateTime create_time;
    private LocalDateTime finish_time;

    public Byte getByteResult() {
        switch(result) {
            case "PROCESSING": return DivRefundTrans.NEW;
            case "SUCCESS": return DivRefundTrans.SUCCESS;
            case "FAILED": return DivRefundTrans.FAIL;
        }
        return null;
    }
}
