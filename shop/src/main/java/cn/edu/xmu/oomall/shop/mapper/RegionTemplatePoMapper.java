//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.mapper;

import cn.edu.xmu.oomall.shop.mapper.po.RegionTemplatePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionTemplatePoMapper extends JpaRepository<RegionTemplatePo, Long> {

    Optional<RegionTemplatePo> findByTemplateIdAndRegionId(Long tid,Long rid);
    Page<RegionTemplatePo> findByTemplateId(Long tid, Pageable pageable);
    List<RegionTemplatePo> findByTemplateId(Long tid);

    List<RegionTemplatePo> deleteByTemplateId(Long id);
}
