package cn.edu.xmu.oomall.shop.service.dto;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class TemplateRetDto {
    private Long id;

    private String name;

    private Byte defaultModel;

    private UserDto creator;

    private UserDto modifier;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
