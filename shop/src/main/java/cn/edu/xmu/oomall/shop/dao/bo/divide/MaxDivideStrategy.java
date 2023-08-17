//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.divide;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;

import java.util.*;

/**
 * 到上限分包
 */
public class MaxDivideStrategy extends DivideStrategy{

    public MaxDivideStrategy(PackAlgorithm algorithm) {
        super(algorithm);
    }

    /**
     * 传过来的num即上限
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 21:46
     * @param total
     * @param upperLimit
     * @return
     */
    @Override
    protected int gotPackageSize(Integer total, Integer upperLimit) {
        return upperLimit;
    }
}
