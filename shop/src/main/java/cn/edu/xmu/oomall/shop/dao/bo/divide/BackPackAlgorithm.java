package cn.edu.xmu.oomall.shop.dao.bo.divide;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BackPackAlgorithm implements PackAlgorithm{

    private static final Logger logger = LoggerFactory.getLogger(BackPackAlgorithm.class);



    /**
     *
     * @author He Zhou
     * <p>
     * date: 2022-11-24 0:01
     * @param items
     * @param packageSize
     * @return
     */

    @Override
    public Collection<Item> pack(Collection<Item> items, Integer packageSize) {
        logger.debug("pack: packageSize = {}, items = {}", packageSize, items);

        Item[] arrayItems = items.toArray(Item[]::new);

        logger.debug("create a new pack");
        int arrayLength = arrayItems.length;
        Set<Item> newPack = new HashSet<>();

        int [][]dp = new int[arrayLength + 1][packageSize + 1];
        int [][]path = new int[arrayLength + 1][packageSize + 1];
        for(int i = 1; i <= arrayLength; i++) {
            int weight = arrayItems[i - 1].getCount();
            if(weight > packageSize){
                for(int j = packageSize; j >= 0; --j){
                    dp[i][j] = dp[i - 1][j];
                }
                continue;
            }
            for(int j = packageSize; j >= 1; --j){
                if(j >= weight){
                    if(dp[i - 1][j - weight] == 0) {
                        dp[i][j] = dp[i - 1][j];
                        continue;
                    }
                    else if(dp[i - 1][j] == 0){
                        dp[i][j] = dp[i - 1][j - weight] + 1;
                        path[i][j] = 1;
                    }
                    else {
                        if(dp[i - 1][j - weight] + 1 < dp[i - 1][j]){
                            dp[i][j] = dp[i - 1][j - weight] + 1;
                            path[i][j] = 1;
                        }
                        else{
                            dp[i][j] = dp[i - 1][j];
                        }
                    }
                }
                else{
                    dp[i][j] = dp[i - 1][j];
                }
            }
            dp[i][0] = 0;
            dp[i][weight] = 1;
            path[i][weight] = 1;
        }
        for(int j = packageSize; j >= 1; --j){
            if(dp[arrayLength][j] > 0){
                int x = arrayLength, y = j;
                while(x > 0 && y > 0){
                    if(path[x][y] == 1){
                        logger.debug("pack: add item {}", arrayItems[x - 1]);
                        y -= arrayItems[x - 1].getCount();
                        Item item = ((ArrayList<Item>) items).get(x - 1);
                        if(!newPack.add(item)){
                            logger.debug("pack: item exist {}", item);
                            newPack.stream().forEach(packItem -> {
                                if (packItem.equals(item)){
                                    logger.debug("pack: add to {}", packItem);
                                    packItem.incr();
                                }
                            });
                        }
                        items.remove(item);
                    }
                    x--;
                }
                break;
            }
        }
        logger.debug("newPack: {}",newPack);

        return newPack;
    }
}