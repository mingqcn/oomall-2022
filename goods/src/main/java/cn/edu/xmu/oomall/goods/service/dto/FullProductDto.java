package cn.edu.xmu.oomall.goods.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wuzhicheng
 * @create 2022-12-04 12:19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class FullProductDto {
    Long id;
    IdNameTypeDto shop;
    List<IdNameDto> otherProducts;
    String name;
    String skuSn;
    Long originalPrice;
    Long weight;
    Byte status;
    String unit;
    String barCode;
    String originPlace;
    Integer freeThreshold;
    Integer commissionRatio;
    IdNameDto category;
    IdNameDto template;
    IdNameDto creator;
    IdNameDto modifier;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;
}
