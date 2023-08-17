package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.oomall.goods.dao.bo.SimpleProductDto;
import cn.edu.xmu.oomall.goods.service.CategoryService;
import cn.edu.xmu.oomall.goods.service.OnsaleService;
import cn.edu.xmu.oomall.goods.service.ProductService;
import cn.edu.xmu.oomall.goods.service.dto.StateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 商品控制器
 * @author Ming Qiu
 */
@RestController /*Restful的Controller对象*/
@RequestMapping(produces = "application/json;charset=UTF-8")
public class UnAuthorizedController {

    private final Logger logger = LoggerFactory.getLogger(UnAuthorizedController.class);

    private OnsaleService onsaleService;

    private ProductService productService;

    private CategoryService categoryService;

    @Autowired
    public UnAuthorizedController(ProductService productService, CategoryService categoryService,OnsaleService onsaleService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.onsaleService = onsaleService;
    }

    /**
     * 获得商品信息
     * @author Ming Qiu
     * <p>
     * date: 2022-12-04 19:19
     * @param id
     * @return
     */
    @GetMapping("/products/{id}")
    public ReturnObject findProductById(@PathVariable("id") Long id) {
        return new ReturnObject(productService.getCustomerProductById(id));
    }

    /**
     * 获得商品的所有状态
     * @author wuzhicheng
     * @return
     */
    @GetMapping("/products/states")
    public ReturnObject getStates(){
        List<StateDto> ret=productService.getStates();
        return new ReturnObject(ret);
    }

    /**
     * 查询正式商品
     * @author wuzhicheng
     * @return
     */
    @GetMapping("/products")
    public ReturnObject getSkuList(@RequestParam Long shopId, @RequestParam String barCode,
                                   @RequestParam(required = false,defaultValue = "1") Integer page,
                                   @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        PageDto<SimpleProductDto> ret=productService.getSkuList(shopId, barCode, page, pageSize);
        return new ReturnObject(ret);
    }

    /**
     * 获得商品的历史信息
     * @author Ming Qiu
     * <p>
     * date: 2022-12-04 19:19
     * @param id
     * @return
     */
    @GetMapping("/onsales/{id}")
    public ReturnObject findOnsaleById(@PathVariable("id") Long id){
        return new ReturnObject(productService.findOnsaleById(id));
    }

    @GetMapping("/categories/{id}/subcategories")
    public ReturnObject getSubCategories(@PathVariable("id") Long id) {
        return new ReturnObject(categoryService.retrieveSubCategories(id));
    }

    /**
     * 查看活动中的商品
     *
     * @param id
     * @param page
     * @param pageSize
     * @return
     * @author Liang nan
     */
    @GetMapping("/activities/{id}/onsales")
    public ReturnObject getCouponActProduct(@PathVariable("id") Long id,
                                            @RequestParam(required = false, defaultValue = "1") Integer page,
                                            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return new ReturnObject(onsaleService.getCouponActProduct(id, page, pageSize));
    }
}
