//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 退款Vo
 */
@Data
@NoArgsConstructor
public class RefundVo {
    @NotNull(message =  "退款金额不能为空")
    @Min(value = 1, message = "退款金额需大于0")
    private Long amount;

    @NotNull(message =  "分账金额不能为空")
    @Min(value = 0, message = "退回分账金额需大于等于0")
    private Long divAmount;
}
