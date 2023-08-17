//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.mapper.jpa;

import cn.edu.xmu.oomall.goods.mapper.po.OnsalePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OnsalePoMapper extends JpaRepository<OnsalePo, Long> {
    Page<OnsalePo> findByProductIdEqualsAndEndTimeAfter(Long productId, LocalDateTime time, Pageable pageable);

    Page<OnsalePo> findByProductIdIs(Long productId,Pageable pageable);

    Page<OnsalePo> findByIdAndProductId(Long id, Long productId, Pageable pageable);

    @Query(value = "select o from OnsalePo o where o.productId = ?1 and ((o.beginTime >= ?2 and o.beginTime < ?3) or (o.endTime > ?2 and o.endTime <= ?3) or (o.beginTime <= ?2 and o.endTime >= ?3))")
    Page<OnsalePo> findOverlap(Long productId, LocalDateTime beginTime, LocalDateTime endTime, Pageable pageable);
    Page<OnsalePo> findByShopId(Long shopId, Pageable pageable);
    Page<OnsalePo> findByShopIdAndProductId(Long shopId, Long productId, Pageable pageable);
    Page<OnsalePo> findByProductIdAndInvalidEquals(Long productId, Byte invalid, Pageable pageable);
    Page<OnsalePo> findByShopIdAndInvalidEquals(Long shopId, Byte invalid, Pageable pageable);
    Page<OnsalePo> findByShopIdAndProductIdAndInvalidEquals(Long shopId, Long productId, Byte invalid, Pageable pageable);
    Page<OnsalePo> findByInvalidEquals(Byte invalid, Pageable pageable);
}
