//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.divide;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.Template;
import cn.edu.xmu.oomall.shop.dao.bo.template.WeightTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 最优运费分包
 */
public class OptimalDivideStrategy extends DivideStrategy {

    private static final Logger logger = LoggerFactory.getLogger(OptimalDivideStrategy.class);

    public OptimalDivideStrategy(PackAlgorithm algorithm) {
        super(algorithm);
    }

    /**
     * 退火初始温度
     */
    private static final double TEMPERATURE_START = 1000.0;
    /**
     * 退火终止温度
     */
    private static final double TEMPERATURE_END = 1.0;
    /**
     * 退火降温率
     */
    private static final double TEMPERATURE_DECREASE_RATE = 0.994;

    @Override
    public Collection<Collection<ProductItem>> divide(RegionTemplate template, Collection<ProductItem> productItems) {
        Collection<Collection<ProductItem>> ret = null;
        logger.debug("divide: template = {}", template);

        //分成一个一个商品，小于包裹尺寸的才留下
        Collection<Item> singleItems = Item.extractToSingle(productItems, template.gotType(), template.getUpperLimit());
        //用算法分成多个包裹
        Collection<Collection<Item>> dividePack = this.saPack(singleItems, productItems, template);
        ret = Item.gotProductItems(productItems, dividePack);

        return ret;
    }

    /**
     * 模拟退火分包
     *
     * @author xumingyi
     * <p>
     * date: 2022-11-24 16:46
     * @param singleItems 物品列表
     * @param productItems 原始物品列表
     * @param template 运费模板
     * @return 分包结果
     */
    private Collection<Collection<Item>> saPack(Collection<Item> singleItems, Collection<ProductItem> productItems, RegionTemplate template) {
        if (null == singleItems || 0 == singleItems.size()) {
            return new ArrayList<>();
        }
        if(1 == singleItems.size()) {
            // 单个物品，直接装入
            Collection<Collection<Item>> ret = new ArrayList<>();
            ret.add(singleItems);
            return ret;
        }

        ArrayList<Item> items = singleItems.stream()
                .sorted(Comparator.comparingInt(Item::getCount).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
        int lowerLimit = items.stream().map(Item::getCount).reduce(0, Integer::max);

        Collection<Collection<Item>> ret = null;
        long preFee = Long.MAX_VALUE;
        int preLimit = template.getUpperLimit();

        double temperature = TEMPERATURE_START;
        int loopCount = 0;
        Random rng = new Random();

        // 退火算法
        while(temperature > TEMPERATURE_END) {
            // 随机交换物品位置
            int index1 = 0;
            int index2 = 0;
            while(index1 == index2) {
                index1 = rng.nextInt(items.size());
                index2 = rng.nextInt(items.size());
            }
            Item temp = items.get(index1);
            items.set(index1, items.get(index2));
            items.set(index2, temp);

            // 随机变化限制
            double limitChangeRate = rng.nextDouble() - 0.5;
            int limitChange = (int) (preLimit * limitChangeRate);
            if(limitChange == 0) {
                limitChange = limitChangeRate < 0 ? -1 : 1;
            }
            int curLimit = preLimit + limitChange;
            curLimit = Math.min(Math.max(curLimit, lowerLimit), template.getUpperLimit());

            Collection<Collection<Item>> packResult = firstFit(items, curLimit);

            long curFee = caculateDivideFee(packResult, productItems, template);
            long delta = curFee - preFee;
            if(delta > 0 && Math.exp(-delta / temperature) < rng.nextDouble()) {
                // 拒绝最新解
                temp = items.get(index1);
                items.set(index1, items.get(index2));
                items.set(index2, temp);
            } else {
                // 接受最新解
                preFee = curFee;
                ret = packResult;
                preLimit = curLimit;
            }
            temperature *= TEMPERATURE_DECREASE_RATE;
            ++loopCount;
        }
        logger.debug("saPack: loopCount = {}", loopCount);
        return ret;
    }

    /**
     * First-Fit 在某个重量和件数限制下的贪心分包算法
     * 单次时间复杂度O(N^2)
     * @author xumingyi
     * <p>
     * date: 2022-11-24 16:46
     * @param singleItems 物品列表
     * @param limit 包裹内物品重量限制
     * @return 分包结果
     */
    private Collection<Collection<Item>> firstFit(Collection<Item> singleItems, Integer limit) {
        ArrayList<Collection<Item>> packs = new ArrayList<>();
        ArrayList<Integer> packWeight = new ArrayList<>();

        for (Item item : singleItems) {
            Integer weight = item.getCount();

            // 查找第一个能够放下的包裹
            Integer targetIndex = null;
            for(int i = 0; i < packs.size(); i++) {
                if(weight + packWeight.get(i) <= limit) {
                    targetIndex = i;
                    break;
                }
            }
            if(null == targetIndex) {
                ArrayList<Item> newPack = new ArrayList<>();
                newPack.add(item);
                packs.add(newPack);
                packWeight.add(weight);
            } else {
                Collection<Item> pack = packs.get(targetIndex);
                pack.add(item);
                packWeight.set(targetIndex, packWeight.get(targetIndex) + weight);
            }
        }

        return packs;
    }

    /**
     * 计算某个分包方案的花费
     */
    private Long caculateDivideFee(Collection<Collection<Item>> packs, Collection<ProductItem> productItems,  RegionTemplate template) {
        Long freight = 0L;
        Collection<Collection<ProductItem>> productPacks = Item.gotProductItems(productItems, packs);
        for(Collection<ProductItem> pack : productPacks) {
            freight += template.cacuFreight(pack);
        }
        return freight;
    }

    @Override
    protected int gotPackageSize(Integer total, Integer upperLimit) {
        return 0;
    }
}
