package cn.edu.xmu.oomall.shop.mapper;

import cn.edu.xmu.oomall.shop.mapper.po.TemplatePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplatePoMapper extends JpaRepository<TemplatePo, Long>{
    Page<TemplatePo> findByShopIdAndNameLike (Long shopId, String name,Pageable pageable);
    Page<TemplatePo> findByNameLike (String name,Pageable pageable);
    Optional<TemplatePo> findByShopIdAndDefaultModel(Long shopId, Byte defaultModel);
}
