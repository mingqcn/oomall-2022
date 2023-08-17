package cn.edu.xmu.oomall.shop.controller.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateVo {
    private Long id;
    private String name;
    @JsonProperty(value = "default")
    private Boolean defaultModel;
}
