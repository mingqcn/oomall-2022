//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 调用商品模块
 */
@FeignClient(name = "goods-service")
public interface GoodsDao {

    @GetMapping("/products/{id}")
    InternalReturnObject<Product> retrieveProductById(@PathVariable  Long id);

}
