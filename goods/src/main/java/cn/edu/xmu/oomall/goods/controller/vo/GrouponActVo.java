//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.goods.controller.vo;

import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrouponActVo {
    @NotNull(message = "活动名称不可为空")
    private String name;
    private ThresholdPo strategy;
}
