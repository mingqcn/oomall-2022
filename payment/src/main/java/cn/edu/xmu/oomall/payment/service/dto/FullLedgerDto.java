package cn.edu.xmu.oomall.payment.service.dto;

import cn.edu.xmu.oomall.payment.dao.bo.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;



@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class FullLedgerDto {
    @NotNull(message = "分账id必填")
    private Long id;

    private String outNo;

    private String transNo;

    private Long amount;

    private LocalDateTime successTime;

    private Byte status;

    private SimpleChannelDto shopChannel;

    private SimpleAdminUserDto creator;

    private SimpleTransDto trans;

    private Byte type;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private SimpleAdminUserDto modifier;

    private SimpleAdminUserDto adjustor;

    private LocalDateTime adjustTime;

}
