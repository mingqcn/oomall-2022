//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThresholdPo implements Serializable {

    /**
     * 低于某个数量
     */
    private Integer quantity;

    /**
     * 比例
     */
    private Long percentage;
}
