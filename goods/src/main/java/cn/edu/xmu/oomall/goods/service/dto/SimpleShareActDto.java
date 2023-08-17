package cn.edu.xmu.oomall.goods.service.dto;

import cn.edu.xmu.oomall.goods.controller.vo.ThresholdVo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 黄坤鹏
 * @date 2022/11/30 12:59
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SimpleShareActDto {
    private Long id;
    private String name;
    private List<ThresholdVo> thresholds;
}
