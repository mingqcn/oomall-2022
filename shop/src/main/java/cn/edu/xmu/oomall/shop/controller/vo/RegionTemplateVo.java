package cn.edu.xmu.oomall.shop.controller.vo;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class RegionTemplateVo {
    @Min(value = 1, message = "计量单位至少为1")
    private Integer unit;

    @Min(value = 1, message = "数量上限至少为1")
    private Integer upperLimit;
}
