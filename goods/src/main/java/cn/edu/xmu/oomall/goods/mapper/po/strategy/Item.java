//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.mapper.po.strategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Long id;
    private String name;
    private Long categoryId;
    private Long onsaleId;
    private Integer quantity;
    private Long price;
    private Long discount;
    private Long couponActivityId;
}
