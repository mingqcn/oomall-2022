package cn.edu.xmu.oomall.goods.mapper.jpa;

import cn.edu.xmu.oomall.goods.mapper.po.OnsalePo;
import cn.edu.xmu.oomall.goods.mapper.po.ProductDraftPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wuzhicheng
 * @create 2022-12-03 23:45
 */
@Repository
public interface ProductDraftPoMapper extends JpaRepository<ProductDraftPo, Long> {
    Page<ProductDraftPo> findByShopIdEquals(Long shopId, Pageable pageable);

    Page<ProductDraftPo> findByCategoryIdEquals(Long id, Pageable pageable);
}
