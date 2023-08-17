package cn.edu.xmu.oomall.goods.service.dto;

import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 黄坤鹏
 * @date 2022/11/30 22:49
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullShareActDto {
    private Long id;

    private IdNameTypeDto shop;

    private String name;

    private List<ThresholdPo> thresholds;

    private List<SimpleOnsaleDto> onsaleList;

    private IdNameDto creator;

    private IdNameDto modifier;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
