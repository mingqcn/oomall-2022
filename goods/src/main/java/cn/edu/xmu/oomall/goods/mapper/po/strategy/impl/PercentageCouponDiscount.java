package cn.edu.xmu.oomall.goods.mapper.po.strategy.impl;

import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponDiscount;
import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponLimitation;
import cn.edu.xmu.oomall.goods.mapper.po.strategy.Item;

import java.io.Serializable;
import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-19
 */
public class PercentageCouponDiscount extends BaseCouponDiscount implements Serializable {

	public PercentageCouponDiscount(){}

	public PercentageCouponDiscount(BaseCouponLimitation limitation, long value) {
		super(limitation, value);
	}

	@Override
	public void calcAndSetDiscount(List<Item> items) {
		for (Item oi : items) {
			oi.setDiscount(oi.getPrice() - value / 100 * oi.getPrice());
		}
	}
}
