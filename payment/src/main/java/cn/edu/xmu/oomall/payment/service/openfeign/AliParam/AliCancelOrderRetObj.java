package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * 取消订单返回值
 * @author Wenbo Li
 * */

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AliCancelOrderRetObj extends PublicRetObj {
    /*可选*/
    private String trade_no;
    private String out_trade_no;
}
