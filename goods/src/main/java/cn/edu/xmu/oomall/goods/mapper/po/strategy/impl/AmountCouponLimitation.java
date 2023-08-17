package cn.edu.xmu.oomall.goods.mapper.po.strategy.impl;


import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponLimitation;
import cn.edu.xmu.oomall.goods.mapper.po.strategy.Item;

import java.io.Serializable;
import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-19
 */
public class AmountCouponLimitation extends BaseCouponLimitation implements Serializable {

	public AmountCouponLimitation(){}

	public AmountCouponLimitation(long value) {
		super(value);
	}

	@Override
	public boolean pass(List<Item> items) {
		long t = 0;
		for (Item oi : items) {
			t += oi.getQuantity();
		}
		return t >= value;
	}
}
