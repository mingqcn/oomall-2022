//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.template;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import lombok.Setter;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 模板类型
 */
public abstract class TemplateType{

    /**
     * 根据商品返回计算值
     * @author Ming Qiu
     * <p>
     * date: 2022-11-19 21:15
     * @param item 货品
     * @return
     */
    public abstract Integer getCount(ProductItem item);

    /**
     * 计算总量
     *
     * @param productItems
     * @return
     * @author Ming Qiu
     * <p>
     * date: 2022-11-19 21:41
     */
    public Integer cacuTotal(Collection<ProductItem> productItems) {
        return productItems.stream().map(item -> getCount(item) * item.getQuantity()).reduce((x, y) -> x + y).get();
    }

}
