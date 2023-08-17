//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.divide;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AverageDivideStrategy extends DivideStrategy{

    private static final Logger logger = LoggerFactory.getLogger(AverageDivideStrategy.class);

    public AverageDivideStrategy(PackAlgorithm algorithm) {
        super(algorithm);
    }

    @Override
    protected int gotPackageSize(Integer total, Integer upperLimit) {
        int num = (total / upperLimit);
        if (total % upperLimit > 0){
            num += 1;
        }
        return total / num;
    }

}
