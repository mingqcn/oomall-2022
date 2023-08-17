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
public class NewAdvanceSaleActVo {

    @NotNull(message = "活动名称不能为空!")
    private String name;

    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime payTime;

    @Min(value = 0,message = "定金不能小于0!")
    private Long advancePayPrice;


}
