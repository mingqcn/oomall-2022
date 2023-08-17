//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.divide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 简单打包算法
 * 将商品按照顺序分成多个包裹
 */
public class SimpleAlgorithm implements PackAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(SimpleAlgorithm.class);

    @Override
    public Collection<Item> pack(Collection<Item> items, Integer packageSize) {
        logger.debug("pack: packageSize = {}, items = {}", packageSize, items);
        int addUp = 0;
        Set<Item> pack = new HashSet<>();;
        Iterator<Item> itemIterator = items.iterator();
        while (itemIterator.hasNext()){
            Item item = itemIterator.next();
            if (addUp + item.getCount() <= packageSize){
                logger.debug("pack: pack = {}, item = {}, addUp = {}", pack, item, addUp);
                //包裹可以放下
                addUp += item.getCount();
                if (!pack.add(item)) {
                    logger.debug("pack: item exists {}", item);
                    pack.stream().forEach(packItem -> {
                        if (packItem.equals(item)){
                            logger.debug("pack: add to {}", packItem);
                            packItem.incr();
                        }
                    });
                }
                itemIterator.remove();
            }
        }
        logger.debug("pack: pack = {}", pack);
        return pack;
    }
}
