package cn.edu.xmu.oomall.payment.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayNotifyDto {

    private Byte status;

    private String outTradeNo;

    private String transNo;

    private Long amount;

    private LocalDateTime successTime;
}
