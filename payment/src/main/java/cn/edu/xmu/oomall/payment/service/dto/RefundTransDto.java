package cn.edu.xmu.oomall.payment.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefundTransDto {
    private Long id;
    private String outNo;
    private String transNo;
    private Long amount;
    private Long divAmount;
    private LocalDateTime successTime;
    private SimpleChannelDto channel;
    private Byte status;
    private String userReceivedAccount;
    private SimpleUserDto creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private SimpleUserDto modifier;
    private SimpleUserDto adjustor;
    private LocalDateTime adjustTime;
}
