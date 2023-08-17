//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.template;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class TemplateResult {

    /**
     * 费用
     */
    private Long fee;
    /**
     *  包括的商品
     */
    private Collection<ProductItem> pack;

}
