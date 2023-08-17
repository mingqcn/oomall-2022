//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.goods.service.dto;

import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class FullGrouponActDto {

    private Long id;

    private String name;

    private List<ThresholdPo> thresholds;

    private IdNameTypeDto shop;

    private IdNameDto creator;

    private IdNameDto modifier;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}
