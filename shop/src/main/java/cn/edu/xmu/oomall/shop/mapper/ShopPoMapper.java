//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.mapper;

import cn.edu.xmu.oomall.shop.mapper.po.ShopPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ShopPoMapper extends JpaRepository<ShopPo, Long> {
        Page<ShopPo> findByCreatorId(Long creatorId, Pageable pageable);
        Page<ShopPo> findByStatusNotAndStatusNot(Byte status1, Byte status2, Pageable pageable);
        Page<ShopPo> findByNameAndTypeAndStatusNotAndStatusNot(String name, Byte type, Byte status1, Byte status2, Pageable pageable);
        Page<ShopPo> findByNameAndStatusNotAndStatusNot(String name, Byte status1, Byte status2, Pageable pageable);
        Page<ShopPo> findByTypeAndStatusNotAndStatusNot(Byte type, Byte status1, Byte status2, Pageable pageable);
        Page<ShopPo> findByNameAndTypeAndStatus(String name, Byte type, Byte status, Pageable pageable);
        Page<ShopPo> findByNameAndType(String name, Byte type, Pageable pageable);
        Page<ShopPo> findByNameAndStatus(String name, Byte status, Pageable pageable);
        Page<ShopPo> findByTypeAndStatus(Byte type, Byte status, Pageable pageable);
        Page<ShopPo> findByName(String name, Pageable pageable);
        Page<ShopPo> findByType(Byte type, Pageable pageable);
        Page<ShopPo> findByStatus(Byte status, Pageable pageable);
}
