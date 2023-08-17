//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.goods.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OnSaleVo {
    @Min(value = 0,message = "价格必须大于0")
    private Long price;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime beginTime;
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;
    @Min(value = 0,message = "数量必须大于0")
    private Integer quantity;
    @Min(value = 0, message = "最大数量必须大于0")
    private Integer maxQuantity;
    private Byte type;
}
