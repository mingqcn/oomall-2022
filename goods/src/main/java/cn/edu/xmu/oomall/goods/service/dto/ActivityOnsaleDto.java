package cn.edu.xmu.oomall.goods.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 黄坤鹏
 * @date 2022/11/30 20:34
 */
@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ActivityOnsaleDto {
    private Long id;
    private Long actId;
    private Long onsaleId;
}
