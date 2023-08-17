package cn.edu.xmu.oomall.shop.service.dto;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.shop.dao.bo.template.PieceTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import cn.edu.xmu.oomall.shop.dao.template.TemplateDao;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PieceTemplateDto extends RegionTemplateDto{

    @JsonProperty("firstItem")
    private Integer firstItems;

    @JsonProperty("firstItemPrice")
    private Long firstPrice;

    private Integer additionalItems;

    @JsonProperty("additionalItemsPrice")
    private Long additionalPrice;

    @Builder
    public PieceTemplateDto(Integer firstItems, Integer additionalItems, Long firstPrice, Long additionalPrice, Long id, Integer unit,
                           Region region, UserDto creator, LocalDateTime gmtCreate, UserDto modifier, LocalDateTime gmtModified){
        super(id,unit,region,creator,gmtCreate,modifier,gmtModified);
        this.firstItems=firstItems;
        this.firstPrice=firstPrice;
        this.additionalItems=additionalItems;
        this.additionalPrice=additionalPrice;
    }
}
