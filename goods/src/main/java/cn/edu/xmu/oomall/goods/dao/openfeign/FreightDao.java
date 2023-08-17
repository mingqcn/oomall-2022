package cn.edu.xmu.oomall.goods.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.goods.service.dto.IdNameDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author wuzhicheng
 * @create 2022-12-04 0:19
 */
@FeignClient("region-service")
public interface FreightDao {

    @GetMapping("/regions/{id}")
    InternalReturnObject<IdNameDto> findRegionById(@PathVariable Long id);
}
