package cn.edu.xmu.oomall.payment.service.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Setter
@Getter
@ToString
public class SimpleTransDto {
    private Long id;
    private String outNo;
    private String transNo;
    private Long amount;
    private Byte status;
    private LocalDateTime successTime;
}
