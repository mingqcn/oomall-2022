//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.template;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;


public class Weight extends TemplateType {



    public Integer getCount(ProductItem item) {
        return item.getWeight().intValue();
    }

}
