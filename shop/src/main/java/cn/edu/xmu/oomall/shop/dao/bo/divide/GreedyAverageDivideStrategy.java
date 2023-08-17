package cn.edu.xmu.oomall.shop.dao.bo.divide;

import java.util.*;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;


public class GreedyAverageDivideStrategy extends DivideStrategy{

    private static final Logger logger = LoggerFactory.getLogger(AverageDivideStrategy.class);
    public GreedyAverageDivideStrategy(PackAlgorithm algorithm) {
        super(algorithm);
    }

    @Override
    protected int gotPackageSize(Integer total, Integer upperLimit) {
        return upperLimit;
    }



    /**
     * 最小包裹数
     * @author 王文昊
     * <p>
     * date: 2022-11-24 11:11
     * @param
     * @return
     * 时间复杂度O(m*n),m为包裹最大重量，n为商品数
     */
    /*得到最少的包裹数，将items降序排序，能装进包裹就装进，否则新增加一个包裹*/
    private int minPackage(Integer packageSize, Collection<Item> productItems){
        int packageNum=0;
        productItems=productItems.stream().sorted(Comparator.comparing(Item::getCount,Comparator.reverseOrder())).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Integer> pack=new ArrayList<>();
        for(Item x:productItems){
            pack.add(packageSize);
        }
        for(Item item:productItems){
            for(int i=0;i<pack.size();i++){
                if(pack.get(i)>=item.getCount()){
                    int tmp=pack.get(i)-item.getCount();
                    pack.set(i,tmp);
                    break;
                }
            }
        }
        for(int weight:pack){
            if(weight != packageSize){
                packageNum++;
            }
        }
        return packageNum;
    }



    /**
     * 重量平均分包-贪心
     * @author 王文昊
     * <p>
     * date: 2022-11-24 11:11
     * @param
     * @return
     * 时间复杂度O(m*n),m为包裹最大重量，n为商品数
     */
    /*在最少包裹数的前提下使得每个包裹的重量尽量平均*/
    public Collection<Collection<Item>> WeightAveragePack(Collection<Item> items, Integer packageSize){
        logger.debug("pack: packageSize = {}, items = {}", packageSize, items);
        Collection<Collection<Item>> ret = new ArrayList<>();
        int packageNum=minPackage(packageSize,items);
        ArrayList<ArrayList<Item>> packs=new ArrayList<>();
        for(int i=0;i<packageNum;i++)
            packs.add(new ArrayList<Item>());
        int total= items.stream().mapToInt(Item::getCount).sum();
        int averageWeight=(total%packageNum == 0)?(total/packageNum):(total/packageNum+1);
        items=items.stream().sorted(Comparator.comparing(Item::getCount,Comparator.reverseOrder())).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Integer> comp=new ArrayList<>();
        for(Item item:items){
            comp.clear();
            for(ArrayList<Item> pack:packs){
                if((item.getCount()+pack.stream().mapToInt(Item::getCount).sum())>packageSize){
                    comp.add(Integer.MAX_VALUE);
                    continue;
                }
                pack.add(item);
                int lossTotal=0;//
                for(ArrayList<Item> sumPack:packs){
                    lossTotal+=Math.abs(averageWeight-sumPack.stream().mapToInt(Item::getCount).sum());
                }
                comp.add(lossTotal);
                pack.remove(item);
            }
            //贪心选择item加入哪一个pack里
            if(comp.size()==0)
                return null;
            ArrayList<Item> tmp=packs.get(comp.indexOf(Collections.min(comp)));
            tmp.add(item);
            packs.set(comp.indexOf(Collections.min(comp)),tmp);
        }
        ret.addAll(packs);
        return ret;
    }

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
            packs=this.WeightAveragePack(singleItems,packageSize);
            ret = Item.gotProductItems(productItems, packs);
            logger.debug("all packs: {}", ret);
        }
        return ret;
    }
}
