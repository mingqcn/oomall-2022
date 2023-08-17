package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.oomall.goods.service.GrouponActService;
import cn.edu.xmu.oomall.goods.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 团购控制器
 * @author prophesier
 */
@RestController /*Restful的Controller对象*/
@RequestMapping(produces = "application/json;charset=UTF-8")
public class GrouponController {

    private final Logger logger = LoggerFactory.getLogger(GrouponController.class);

    private GrouponActService grouponActService;

    private ProductService productService;

    @Autowired
    public GrouponController(ProductService productService,GrouponActService grouponActService) {
        this.productService = productService;
        this.grouponActService=grouponActService;
    }

    @GetMapping("/groupons")
    public ReturnObject retrieveGrouponAct(@RequestParam Long shopId,
                                           @RequestParam Long productId,
                                           @RequestParam(required = false, defaultValue = "1") Integer page,
                                           @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        return new ReturnObject(grouponActService.retrieveByShopIdAndProductId(shopId,productId,page,pageSize));
    }

    @Audit(departName = "goods")
    @GetMapping("/groupons/{id}")
    public ReturnObject findGrouponActById(@PathVariable Long id){
        return new ReturnObject(grouponActService.findById(id));
    }


}
