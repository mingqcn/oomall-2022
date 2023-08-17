//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.template;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import lombok.Data;

import java.io.Serializable;

@Data
public class Piece extends TemplateType implements Serializable {

    @Override
    public Integer getCount(ProductItem item) {
        return 1;
    }
}
