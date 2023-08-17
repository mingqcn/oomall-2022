package cn.edu.xmu.oomall.payment.service.dto;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author 黄坤鹏
 * @date 2022/11/9 10:00
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullShopChannelDto {
    private Long id;
    /**
     * 子商户号
     */
    private String subMchid;
    /**
     * 状态
     */
    private Byte status;
    /**
     * 支付渠道
     */
    private SimpleChannelDto channel;

    private UserDto creator;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private UserDto modifier;
}
