package cn.edu.xmu.oomall.shop.service.dto;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class RegionTemplateDto {
    private Long id;
    private Integer unit;
    private Region region;
    private UserDto creator;
    private LocalDateTime gmtCreate;
    private UserDto modifier;
    private LocalDateTime gmtModified;
}
