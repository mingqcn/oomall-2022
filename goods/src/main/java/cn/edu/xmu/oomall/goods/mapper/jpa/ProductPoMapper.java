//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.mapper.jpa;

import cn.edu.xmu.oomall.goods.mapper.po.ProductPo;
import io.lettuce.core.StrAlgoArgs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPoMapper extends JpaRepository<ProductPo, Long> {

    Page<ProductPo> findByNameEqualsAndStatusNot(String name, Byte status, Pageable pageable);

    Page<ProductPo> findByGoodsIdEquals(Long id, Pageable pageable);

    Page<ProductPo> findByShopIdEqualsAndBarcodeEquals(Long shopId, String barCode, Pageable pageable);

    Page<ProductPo> findByTemplateIdEquals(Long templateId, Pageable pageable);

    Page<ProductPo> findByCategoryIdEquals(Long id, Pageable pageable);
}
