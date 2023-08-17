package cn.edu.xmu.oomall.shop.service.dto;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.WeightTemplate;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import cn.edu.xmu.oomall.shop.dao.template.TemplateDao;
import cn.edu.xmu.oomall.shop.mapper.po.WeightThresholdPo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeightTemplateDto extends RegionTemplateDto{

    private Integer firstWeight;

    @JsonProperty("firstWeightFreight")
    private Long firstWeightPrice;

    private List<WeightThresholdPo> thresholds;

    @Builder
    public WeightTemplateDto(Integer firstWeight, Long firstWeightPrice, List<WeightThresholdPo> thresholds, Long id, Integer unit,
                            Region region, UserDto creator, LocalDateTime gmtCreate, UserDto modifier, LocalDateTime gmtModified){
        super(id,unit,region,creator,gmtCreate,modifier,gmtModified);
        this.firstWeight=firstWeight;
        this.firstWeightPrice=firstWeightPrice;
        this.thresholds=thresholds;
    }
}
