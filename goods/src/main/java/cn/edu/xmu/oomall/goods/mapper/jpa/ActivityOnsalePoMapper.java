//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.mapper.jpa;

import cn.edu.xmu.oomall.goods.mapper.po.ActivityOnsalePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ActivityOnsalePoMapper extends JpaRepository<ActivityOnsalePo, Long> {
    Page<ActivityOnsalePo> findByOnsaleIdEquals(Long onsaleId, Pageable pageable);

    List<ActivityOnsalePo> findByActIdEquals(Long actId);

    List<ActivityOnsalePo> findByActIdAndOnsaleId(Long actId, Long onsaleId);

    void deleteByActId(Long actId);

    Page<ActivityOnsalePo> findByActIdEquals(Long actId, Pageable pageable);

}
