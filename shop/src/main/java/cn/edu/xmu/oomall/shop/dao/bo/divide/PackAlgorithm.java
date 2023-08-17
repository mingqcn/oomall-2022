//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.divide;

import java.util.Collection;

/**
 * 打包算法
 */
public interface PackAlgorithm {

    /**
     * 将商品放入包裹
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 15:18
     * @param productItems 商品
     * @param packageSize 包裹大小
     * @return 包裹中的商品
     */
    public abstract  Collection<Item> pack(Collection<Item> productItems, Integer packageSize);

}
