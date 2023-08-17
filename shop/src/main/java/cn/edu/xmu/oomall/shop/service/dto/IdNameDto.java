//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
public class IdNameDto {
    private Long id;
    private String name;
}
