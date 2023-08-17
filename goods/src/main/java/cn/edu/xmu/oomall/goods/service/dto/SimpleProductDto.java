package cn.edu.xmu.oomall.goods.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wuzhicheng
 * @create 2022-12-03 22:33
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SimpleProductDto {
    Long id;
    String name;
    Long price;
    Byte status;
    Integer quantity;
}
