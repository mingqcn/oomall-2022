package cn.edu.xmu.oomall.shop.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author chenyz
 * @date 2022-11-26 18:38
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ShopDto {
    private Long id;
    private String name;
    private Long deposit;
    private Long depositThreshold;
    private Byte status;
    private ConsigneeDto consignee;
    private Byte type;
    private Integer freeThreshold;
    private IdNameDto creator;
    private IdNameDto modifier;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
