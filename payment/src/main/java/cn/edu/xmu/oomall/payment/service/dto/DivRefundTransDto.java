package cn.edu.xmu.oomall.payment.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DivRefundTransDto {
    private Long id;
    private String outNo;
    private String transNo;
    private Long amount;
    private LocalDateTime successTime;
    private SimpleChannelDto channel;
    private Long shopId;
    private String shopName;
}
