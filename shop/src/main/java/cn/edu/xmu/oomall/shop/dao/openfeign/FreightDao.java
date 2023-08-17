//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "region-service")
public interface FreightDao {

    @GetMapping("/regions/{id}")
    InternalReturnObject<Region> findRegionById(@PathVariable  Long id);

    @GetMapping("/internal/regions/{id}/parents")
    InternalReturnObject<List<Region>> retrieveParentRegionsById(@PathVariable  Long id);
}
