package cn.edu.xmu.oomall.payment.controller.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class IntervalVo {
    @NotNull
    private LocalDateTime beginTime;
    @NotNull
    private LocalDateTime endTime;
}
