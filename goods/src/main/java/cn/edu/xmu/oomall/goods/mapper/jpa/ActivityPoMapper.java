//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.mapper.jpa;

import cn.edu.xmu.oomall.goods.mapper.po.ActivityPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivityPoMapper extends JpaRepository<ActivityPo, Long> {
    Page<ActivityPo> findAllByActClassEqualsAndShopId(String actClass, Long shopId, Pageable pageable);
    Page<ActivityPo> findAllByActClassEquals(String actClass,Pageable pageable);
    Page<ActivityPo> findByShopIdEquals(Long shopId, Pageable pageable);
}
