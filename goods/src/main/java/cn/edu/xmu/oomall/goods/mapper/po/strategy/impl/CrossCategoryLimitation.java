package cn.edu.xmu.oomall.goods.mapper.po.strategy.impl;

import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponLimitation;
import cn.edu.xmu.oomall.goods.mapper.po.strategy.Item;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhongyu wang 22920192204295
 * 跨品类优惠
 */
@NoArgsConstructor
public class CrossCategoryLimitation extends BaseCouponLimitation implements Serializable {
    public CrossCategoryLimitation(long value) {
        super(value);
    }
    @Override
    public boolean pass(List<Item> items) {
        long categoryCount = items.stream().map(Item::getCategoryId).distinct().count();
        return categoryCount >= value;
    }
}
