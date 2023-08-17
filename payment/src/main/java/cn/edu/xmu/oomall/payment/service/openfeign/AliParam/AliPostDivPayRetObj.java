package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * 发起分账返回值
 * @author Wenbo Li
 * */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AliPostDivPayRetObj extends PublicRetObj {
    /*必选*/
    private String trade_no;

    /*可选*/
    private String settle_no; // 退还账号
}
