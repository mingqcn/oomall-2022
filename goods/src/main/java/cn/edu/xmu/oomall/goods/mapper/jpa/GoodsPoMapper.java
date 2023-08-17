package cn.edu.xmu.oomall.goods.mapper.jpa;

import cn.edu.xmu.oomall.goods.mapper.po.GoodsPo;
import cn.edu.xmu.oomall.goods.mapper.po.ProductDraftPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wuzhicheng
 * @create 2022-12-04 16:04
 */
@Repository
public interface GoodsPoMapper extends JpaRepository<GoodsPo, Long> {
}
