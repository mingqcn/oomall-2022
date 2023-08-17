//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.goods.dao.bo.Shop;
import cn.edu.xmu.oomall.goods.dao.bo.Template;
import cn.edu.xmu.oomall.goods.service.dto.IdNameDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("shop-service")
public interface ShopDao {

    @GetMapping("/shops/{id}")
    InternalReturnObject<Shop> getShopById(@PathVariable Long id);

    @GetMapping("/shops/{shopId}/templates/{id}")
    InternalReturnObject<Template> getTemplateById(@PathVariable Long shopId, @PathVariable Long id);

}
