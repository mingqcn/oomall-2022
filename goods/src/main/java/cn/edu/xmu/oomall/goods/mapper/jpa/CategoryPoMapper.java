//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.mapper.jpa;

import cn.edu.xmu.oomall.goods.mapper.po.CategoryPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryPoMapper extends JpaRepository<CategoryPo, Long> {

    Page<CategoryPo> findByPidEquals(Long pid, Pageable pageable);
}
