package cn.edu.xmu.oomall.goods.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvanceSaleActVo {

    @NotNull(message = "活动名不能为空!")
    private String name;

    @Min(value = 0,message = "价格不能小于0!")
    private Long price;

    @Min(value = 0,message = "定金不能小于0!")
    private Long advancePayPrice;

    @Min(value = 0,message = "数量不能小于0!")
    private Integer quantity;

    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime beginTime;

    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime payTime;

    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    @Min(value = 0,message = "最大数量不能小于0!")
    private Integer maxQuantity;


}
