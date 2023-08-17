package cn.edu.xmu.oomall.shop.controller;

import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.oomall.shop.controller.vo.ProductItemVo;
import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.service.FreightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class InternalFreightController {
    private final Logger logger = LoggerFactory.getLogger(InternalFreightController.class);

    private FreightService freightService;

    @Autowired
    public InternalFreightController(FreightService freightService){
        this.freightService=freightService;
    }

    /*
        内部API-计算一批商品的运费
     */
    @PostMapping("/internal/templates/{id}/regions/{rid}/freightprice")
    public ReturnObject getFreight(
            @PathVariable("id")Long tid,
            @PathVariable("rid")Long rid,
            @Validated @RequestBody List<ProductItemVo> voList
    ){
        List<ProductItem> boList=voList.stream().map(vo->ProductItem.builder().id(vo.getOrderItemId()).productId(vo.getProductId()).quantity(vo.getQuantity()).weight(vo.getWeight()).build())
                                                .collect(Collectors.toList());
        return new ReturnObject(freightService.getFreight(boList,tid,rid));
    }
}
