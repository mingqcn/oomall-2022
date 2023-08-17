package cn.edu.xmu.oomall.goods.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author wuzhicheng
 * @create 2022-12-03 23:35
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ProductDraftDto {
    Long id;
    IdNameTypeDto shop;
    String name;
    Long originalPrice;
    String originPlace;
    IdNameDto category;
    IdNameDto productName;
    IdNameDto creator;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;
    IdNameDto modifier;

}
