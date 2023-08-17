package cn.edu.xmu.oomall.shop.controller.vo;


import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class PieceTemplateVo extends RegionTemplateVo{
    @JsonProperty("firstItem")
    private Integer firstItems;

    @JsonProperty("firstItemPrice")
    private Long firstPrice;

    private Integer additionalItems;

    @JsonProperty("additionalItemsPrice")
    private Long additionalPrice;

}
