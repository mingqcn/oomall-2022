package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求分账返回值
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WePostDivPayRetObj {
    private String transaction_id;
    private String out_order_no;
    private String order_id; // 分账单号
    private String state;
}
