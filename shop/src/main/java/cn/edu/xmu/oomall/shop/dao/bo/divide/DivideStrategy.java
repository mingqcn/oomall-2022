//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.divide;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 分包策略
 */
public abstract class DivideStrategy {

    private static final Logger logger = LoggerFactory.getLogger(DivideStrategy.class);


    protected PackAlgorithm algorithm;

    public DivideStrategy(PackAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 按照不同标准分包
     * @author Ming Qiu
     * <p>
     * date: 2022-11-15 21:48
     * @param productItems
     * @return
     */
    public Collection<Collection<ProductItem>> divide(RegionTemplate template, Collection<ProductItem> productItems){

        Collection<Collection<ProductItem>> ret = null;
        Integer total = template.gotType().cacuTotal(productItems);
        logger.debug("divide: total = {}", total);
        if (total <= template.getUpperLimit()){
            //未超过上限
            ret = new ArrayList<>(){
                {
                    add(productItems);
                }
            };
        }else{
            //需要平分包裹
            int packageSize = this.gotPackageSize(total, template.getUpperLimit());

            //分成一个一个商品，小于包裹尺寸的才留下
            Collection<Item> singleItems = Item.extractToSingle(productItems, template.gotType(), template.getUpperLimit());
            Collection<Collection<Item>> packs = new ArrayList<>();
            //多次使用算法分成多个包裹
            Integer previousSize = 0;
            while (singleItems.size() > 0 && singleItems.size() != previousSize){
                logger.debug("divide: previousSize = {}, singleItems = {}", previousSize, singleItems);
                previousSize = singleItems.size();
                Collection<Item> pack = this.algorithm.pack(singleItems, packageSize);
                if (pack.size() > 0 ){
                    packs.add(pack);
                }
            }

            ret = Item.gotProductItems(productItems, packs);
            logger.debug("divide: left singleItems = {}", singleItems);
        }
        return ret;
    }

    /**
     * 获得包裹数
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 21:42
     * @param total
     * @param upperLimit
     * @return
     */
    protected abstract int gotPackageSize(Integer total, Integer upperLimit);


}
