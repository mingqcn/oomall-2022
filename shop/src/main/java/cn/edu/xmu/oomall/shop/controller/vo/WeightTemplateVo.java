package cn.edu.xmu.oomall.shop.controller.vo;


import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import cn.edu.xmu.oomall.shop.mapper.po.WeightThresholdPo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class WeightTemplateVo extends RegionTemplateVo{

    private Integer firstWeight;

    @JsonProperty("firstWeightFreight")
    private Long firstWeightPrice;

    private List<WeightThresholdPo> thresholds;
}
