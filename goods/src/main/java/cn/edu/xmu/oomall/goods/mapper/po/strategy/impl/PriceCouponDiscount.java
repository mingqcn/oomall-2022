package cn.edu.xmu.oomall.goods.mapper.po.strategy.impl;



import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponDiscount;
import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponLimitation;
import cn.edu.xmu.oomall.goods.mapper.po.strategy.Item;

import java.io.Serializable;
import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-18
 */
public class PriceCouponDiscount extends BaseCouponDiscount implements Serializable {

	public PriceCouponDiscount(){}

	public PriceCouponDiscount(BaseCouponLimitation limitation, long value) {
		super(limitation, value);
	}

	@Override
	public void calcAndSetDiscount(List<Item> items) {
		long total = 0L;
		for (Item oi : items) {
			total += oi.getPrice() * oi.getQuantity();
		}

		for (Item oi : items) {
			long discount = oi.getPrice() - (long) ((1.0 * oi.getQuantity() * oi.getPrice() / total) * value / oi.getQuantity());
			oi.setDiscount(discount);
		}
	}
}
