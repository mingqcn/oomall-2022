package cn.edu.xmu.oomall.goods.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuzhicheng
 * @create 2022-12-13 19:16
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SimpleProductDraftDto {
    Long id;
    String name;
    String originalPrice;
    String originPlace;
}
