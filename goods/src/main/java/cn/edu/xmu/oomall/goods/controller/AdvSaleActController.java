package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.goods.controller.vo.*;
import cn.edu.xmu.oomall.goods.service.AdvanceSaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = "application/json;charset=UTF-8")
public class AdvSaleActController {

    private final Logger logger = LoggerFactory.getLogger(cn.edu.xmu.oomall.goods.controller.AdminProductController.class);

    private AdvanceSaleService advanceSaleService;

    @Autowired
    public AdvSaleActController(AdvanceSaleService advanceSaleService) {
        this.advanceSaleService = advanceSaleService;
    }

    @GetMapping("/advancesales")
    public ReturnObject retrieveAllAdvanceSaleAct(@RequestParam(required = false) Long shopId,
                                                  @RequestParam(required = false) Long productId,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return new ReturnObject(advanceSaleService.retrieveAllAdvanceSaleAct(shopId,productId,page,pageSize));
    }

    @GetMapping("/advancesales/{id}")
    public ReturnObject retrieveAdvanceSaleActById(@PathVariable Long id) {
        return new ReturnObject(advanceSaleService.retrieveValidById(id));
    }


    @GetMapping("/shops/{shopId}/advancesales")
    @Audit(departName = "shops")
    public ReturnObject retrieveAdvanceActByShopId(@PathVariable Long shopId,
                                                   @RequestParam(required = false) Long productId,
                                                   @RequestParam(required = false) Long onsaleId,
                                                   @RequestParam(required = false, defaultValue = "1") Integer page,
                                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                   @LoginUser UserDto user) {
        return new ReturnObject(advanceSaleService.retrieveAdvanceSaleActByShopId(shopId, productId, onsaleId, page, pageSize, user));
    }


    @PostMapping("/shops/{shopId}/onsales/{id}/advancesales")
    @Audit(departName = "shops")
    public ReturnObject createAdvanceSaleAct(@PathVariable Long shopId,
                                             @PathVariable Long id,
                                             @Validated @RequestBody NewAdvanceSaleActVo body,
                                             @LoginUser UserDto userDto) {

        return advanceSaleService.createAdvanceSaleAct(shopId,id,body.getName(),body.getPayTime(),body.getAdvancePayPrice(),userDto);
    }

    @GetMapping("/shops/{shopId}/advancesales/{id}")
    @Audit(departName = "shops")
    public ReturnObject retrieveAdvanceSaleActById(@PathVariable Long shopId,
                                                   @PathVariable(required = false) Long id,
                                                   @LoginUser UserDto userDto) {

        return new ReturnObject(advanceSaleService.retrieveById(id,userDto));
    }


    @PutMapping("/shops/{shopId}/advancesales/{id}")
    @Audit(departName = "shops")
    public ReturnObject updateAdvanceSaleActById(@PathVariable Long shopId,
                                                 @PathVariable(required = false) Long id,
                                                 @Validated @RequestBody AdvanceSaleActVo body,
                                                 @LoginUser UserDto userDto) {
        return advanceSaleService.updateAdvanceSaleAct(shopId,id, body,userDto);
    }


    @DeleteMapping("/shops/{shopId}/advancesales/{id}")
    @Audit(departName = "shops")
    public ReturnObject deleteAdvanceSaleActById(@PathVariable Long shopId,
                                                 @PathVariable(required = false) Long id,
                                                 @LoginUser UserDto userDto) {
        return advanceSaleService.deleteAdvanceSaleActById(shopId, id, userDto);
    }

}
