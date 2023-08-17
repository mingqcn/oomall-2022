package cn.edu.xmu.oomall.payment.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DivPayTransDto {

    private Long id;

    private String outNo;

    private String transNo;

    private Long amount;

    private LocalDateTime successTime;

    private String prepayId;

    private Byte inRefund;

    private SimpleChannelDto channel;

    private Byte status;

    private String timeBegin;

    private String timeExpire;

    private SimpleAdminUserDto adjustor;

    private String adjustTime;

    private SimpleAdminUserDto creator;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private SimpleAdminUserDto modifier;
}
