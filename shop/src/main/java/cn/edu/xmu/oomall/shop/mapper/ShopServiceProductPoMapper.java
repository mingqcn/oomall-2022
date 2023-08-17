//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.mapper;

import cn.edu.xmu.oomall.shop.mapper.po.ShopServiceProductPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShopServiceProductPoMapper extends JpaRepository<ShopServiceProductPo, Long> {
        Page<ShopServiceProductPo> findByMaintainerId(Long maintainerId, Pageable pageable);
        List<ShopServiceProductPo> findByMaintainerId(Long maintainerId);
        Page<ShopServiceProductPo> findByMaintainerIdEqualsAndInvalidEqualsAndBeginTimeBeforeAndEndTimeAfter(Long maintainerId, Byte invalid, LocalDateTime beginTime, LocalDateTime endTime, Pageable pageable);
        Page<ShopServiceProductPo> findByProductIdAndRegionIdOrderByPriority(Long productId, Long regionId, Pageable pageable);
        Page<ShopServiceProductPo> findByProductIdAndRegionIdAndInvalidAndBeginTimeBeforeAndEndTimeAfter(Long productId, Long regionId, Byte invalid, LocalDateTime beginTime, LocalDateTime endTime, Pageable pageable);
}
