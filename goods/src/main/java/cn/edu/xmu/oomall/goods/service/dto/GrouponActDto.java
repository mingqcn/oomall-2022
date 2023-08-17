package cn.edu.xmu.oomall.goods.service.dto;

import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author prophesier
 * @create 2022-12-04 21:17
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrouponActDto {
    private Long id;
    private IdNameTypeDto shop;
    private String name;
    private List<ThresholdPo> thresholds;
}
