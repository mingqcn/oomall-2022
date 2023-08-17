//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.divide;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.dao.bo.template.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Item{
    private Long id;
    private Long productId;
    private Integer count;
    private Integer quantity;

    public Item(Long id, Long productId, Integer count) {
        this.id = id;
        this.productId = productId;
        this.count = count;
        this.quantity = 1;
    }

    /**
     * 由Item再转换成ProductItem，相同的合并
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 15:07
     * @param productItems
     * @param items
     * @return
     */
    public static Collection<Collection<ProductItem>> gotProductItems(Collection<ProductItem> productItems, Collection<Collection<Item>> items) {
        Collection<Collection<ProductItem>> ret = new ArrayList<>();
        items.stream().forEach(pack -> {
            List<ProductItem> prodPack = pack.stream().map(item -> {
                List<ProductItem> findProdItems =  productItems.stream().
                        filter(prodItem -> prodItem.getId() == item.getId() && prodItem.getProductId() == item.getProductId())
                        .limit(1)
                        .map(productItem -> {
                            try {
                                return productItem.cloneWithQuantity(item.getQuantity());
                            } catch (CloneNotSupportedException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());
                return findProdItems.get(0);
            }).collect(Collectors.toList());
            ret.add(prodPack);
        });
        return ret;
    }

    /**
     * 将商品展成每项单个，小于包裹大小的才留下
     * @author Ming Qiu
     * <p>
     * date: 2022-11-19 21:45
     * @param productItems 商品
     * @param templateType 模板类型
     * @param packSize 包裹大小
     * @return
     */
    public static Collection<Item> extractToSingle(Collection<ProductItem> productItems, TemplateType templateType, Integer packSize){
        List<Item> singleItems = new ArrayList<>(productItems.size());
        for (ProductItem item : productItems){
            if (templateType.getCount(item) <= packSize) {
                for (int i = 0; i < item.getQuantity(); i++) {
                    Item newItem = new Item(item.getId(), item.getProductId(), templateType.getCount(item));
                    singleItems.add(newItem);
                }
            }
        }
        return singleItems;
    }

    @Override
    public int hashCode(){
        final int prime = 59;
        return id.intValue() * prime + productId.intValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj instanceof Item) {
            Item item = (Item) obj;
            return item.getId() == this.id && item.getProductId() == this.productId;
        }
        return false;
    }

    public void incr(){
        this.quantity += 1;
    }
}