//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class ThresholdVo{

    /**
     * 低于某个数量
     */
    @Min(value = 0,message = "门槛数量必须大于0")
    private Integer quantity;

    /**
     * 比例
     */
    @Min(value = 0,message = "返点比例必须大于0")
    private Long percentage;
}
