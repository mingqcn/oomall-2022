package cn.edu.xmu.oomall.payment.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullPayTransDto {

    private Long id;

    private String outNo;

    private String transNo;

    private Long amount;

    private Long divAmount;

    private LocalDateTime successTime;

    private String prepayId;

    private Byte inRefund;

    private SimpleChannelDto channel;

    private Byte status;

    private LocalDateTime timeBegin;

    private LocalDateTime timeExpire;

    private SimpleUserDto adjustor;

    private LocalDateTime adjustTime;

    private SimpleUserDto creator;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private SimpleUserDto modifier;
}