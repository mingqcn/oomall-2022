package cn.edu.xmu.oomall.goods.mapper.po.strategy;

import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-18
 */
public interface Computable {

	List<Item> compute(List<Item> items);
}
