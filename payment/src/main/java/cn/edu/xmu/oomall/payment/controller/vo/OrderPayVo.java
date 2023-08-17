//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.edu.xmu.javaee.core.model.Constants.*;

/**
 * 支付的vo
 */
@Data
@NoArgsConstructor
public class OrderPayVo {

    @NotNull(message="开始时间不能为空")
    private LocalDateTime timeBegin = BEGIN_TIME;

    @NotNull(message="结束时间不能为空")
    private LocalDateTime timeEnd = END_TIME;

    @NotNull(message="支付渠道必填")
    private Long shopChannelId;

    @Min(value = 0, message="付款金额需大于等于0")
    private Long amount;

    @Min(value = 0, message="付款分账金额需大于等于0")
    private Long divAmount;

    /**
     * 支付用户标识
     */
    private String spOpenid;

}
