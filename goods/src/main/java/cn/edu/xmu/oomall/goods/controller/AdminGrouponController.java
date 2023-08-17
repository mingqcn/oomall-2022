package cn.edu.xmu.oomall.goods.controller;


import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.goods.controller.vo.GrouponActVo;
import cn.edu.xmu.oomall.goods.service.GrouponActService;
import cn.edu.xmu.oomall.goods.service.ProductService;
import cn.edu.xmu.oomall.goods.service.dto.FullGrouponActDto;
import cn.edu.xmu.oomall.goods.service.dto.SimpleGrouponActDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 团购控制器
 * @author prophesier
 */
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/shops/{shopId}", produces = "application/json;charset=UTF-8")
public class AdminGrouponController {

    private final Logger logger = LoggerFactory.getLogger(AdminGrouponController.class);


    private ProductService productService;

    private GrouponActService grouponActService;

    @Autowired
    public AdminGrouponController(ProductService productService,GrouponActService grouponActService) {
        this.productService = productService;
        this.grouponActService=grouponActService;
    }



    /**
     * 管理员查看特定团购详情
     * @param shopId
     * @param actId
     * @return
     */
    @GetMapping("/groupons/{id}")
    public ReturnObject findGrouponById(@PathVariable Long shopId,
                                 @PathVariable("id") Long actId){
        FullGrouponActDto fullGrouponActDto = grouponActService.findByShopIdAndActId(shopId, actId);
        return new ReturnObject(fullGrouponActDto);
    }

    //管理员查询商铺的所有状态团购
    @GetMapping("/groupons")
    public ReturnObject retrieveByShopId(@PathVariable Long shopId,
                                         @RequestParam Long productId,
                                         @RequestParam Long onsaleId,
                                         @RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        return new ReturnObject(grouponActService.retrieveByShopIdAndProductIdAndOnsaleId(shopId, productId, onsaleId, page, pageSize));
    }



    /**
     *  管理员新增团购活动
     * @param shopId
     * @param pid
     * @param body
     * @param userDto
     * @return
     */
    @Audit(departName = "shops")
    @PostMapping("/onsales/{pid}/groupons")
    public ReturnObject createGrouponAct(@PathVariable Long shopId, @PathVariable Long pid, @Validated @RequestBody GrouponActVo body, @LoginUser UserDto userDto){
        SimpleGrouponActDto grouponAct = grouponActService.createGrouponAct(shopId, pid, body.getName(),body.getStrategy(), userDto);
        return new ReturnObject(grouponAct);
    }



    /**
     * 管理员修改团购活动
     * @param shopId
     * @param id
     * @param grouponActVo
     * @param userDto
     * @return
     */
    @Audit(departName = "shops")
    @PutMapping("/groupons/{id}")
    public ReturnObject updateGrouponAct(@PathVariable Long shopId, @PathVariable Long id, @RequestBody GrouponActVo grouponActVo,UserDto userDto){
        return grouponActService.updateById(shopId,id, grouponActVo.getName(),grouponActVo.getStrategy(), userDto);
    }

    /**
     * 管理员取消团购活动
     * @param shopId
     * @param id
     * @return
     */
    @Audit(departName = "shops")
    @DeleteMapping("/groupons/{id}")
    public ReturnObject deleteGrouponAct(@PathVariable Long shopId, @PathVariable Long id, UserDto userDto){
        return new ReturnObject(grouponActService.cancelById(shopId,id,userDto));
    }


}
