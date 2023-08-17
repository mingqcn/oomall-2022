package cn.edu.xmu.oomall.goods.service.dto;

import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 黄坤鹏
 * @date 2022/11/30 20:13
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShareActDto {
    private Long id;
    private IdNameTypeDto shop;
    private String name;
    private List<ThresholdPo> thresholds;
    private List<SimpleOnsaleDto> onsaleList;
}
