package cn.edu.xmu.oomall.goods.mapper.po.strategy.impl;


import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponDiscount;
import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponLimitation;
import cn.edu.xmu.oomall.goods.mapper.po.strategy.Item;

import java.io.Serializable;
import java.util.List;

public class CheapestPercentageDiscount extends BaseCouponDiscount implements Serializable {

	public CheapestPercentageDiscount() {
		super();
	}

	public CheapestPercentageDiscount(BaseCouponLimitation limitation, long value) {
		super(limitation, value);
	}

	@Override
	public void calcAndSetDiscount(List<Item> items) {
		int min = Integer.MAX_VALUE;
		int total = 0;
		for (int i = 0; i < items.size(); i++) {
			Item oi = items.get(i);
			total += oi.getPrice() * oi.getQuantity();
			if (oi.getPrice() < min) {
				min = i;
			}
		}

		long discount = (long) ((1.0 * value / 100) * items.get(min).getPrice());

		for (Item oi : items) {
			oi.setDiscount(oi.getPrice() - (long) ((1.0 * oi.getPrice() * oi.getQuantity()) / total * discount / oi.getQuantity()));
		}
	}
}
