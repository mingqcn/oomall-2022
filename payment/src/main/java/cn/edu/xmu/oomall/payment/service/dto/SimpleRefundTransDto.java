package cn.edu.xmu.oomall.payment.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SimpleRefundTransDto extends SimpleTransDto {

    private String userReceivedAccount;

    private LocalDateTime successTime;

}
