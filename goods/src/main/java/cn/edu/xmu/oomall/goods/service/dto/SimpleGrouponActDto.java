//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.goods.service.dto;

import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品视图对象
 * @author Ming Qiu
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SimpleGrouponActDto {

    private Long id;
    private String name;
    private List<ThresholdPo> thresholds;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
