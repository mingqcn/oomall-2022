package cn.edu.xmu.oomall.payment.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleRefundDto {
    private Long id;
    private String outNo;
    private String transNo;
    private Long amount;
    private Byte status;
    private String userReceivedAccount;
    private LocalDateTime successTime;
}
