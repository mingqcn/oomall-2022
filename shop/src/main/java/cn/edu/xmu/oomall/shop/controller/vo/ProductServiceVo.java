package cn.edu.xmu.oomall.shop.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static cn.edu.xmu.javaee.core.model.Constants.*;

/**
 * @author chenyz
 * @date 2022-11-26 22:26
 */
@Data
@NoArgsConstructor
public class ProductServiceVo {

    @NotNull(message="开始时间不能为空")
    LocalDateTime beginTime = BEGIN_TIME;

    @NotNull(message="结束时间不能为空")
    LocalDateTime endTime = END_TIME;

    /**
     * 类型: 0 有效 1 无效
     */
    Byte invalid;

    @Min(value = 0, message = "优先级最高为0")
    Integer priority;
}
