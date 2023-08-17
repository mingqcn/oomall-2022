package cn.edu.xmu.oomall.shop.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.oomall.shop.controller.vo.FreightPriceVo;
import cn.edu.xmu.oomall.shop.controller.vo.ProductItemVo;
import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.TemplateResult;
import cn.edu.xmu.oomall.shop.dao.template.RegionTemplateDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FreightService {

    private final Logger logger = LoggerFactory.getLogger(FreightService.class);

    private RegionTemplateDao regionTemplateDao;


    @Autowired
    public FreightService(
            RegionTemplateDao regionTemplateDao
    ){
        this.regionTemplateDao=regionTemplateDao;
    }

    /**
    * 计算一批商品的运费
    * @author Zhanyu Liu
    * @date 2022/12/2 13:46
    * @param boList
    * @param tid 上级模板id
    * @param rid 地区id
    */
    public FreightPriceVo getFreight(List<ProductItem> boList, Long tid, Long rid){
        RegionTemplate regionTemplate=regionTemplateDao.findByTemplateIdAndRegionId(tid,rid);
        /*
         *  如果rid对应的运费模板存在,那么optional.get()得到的就是该运费模板
         *  否则,optional.get()得到的就是与rid关系最近的上级地区模板
         */
        logger.debug("getFreight: regionTemplate={}",regionTemplate);
        Collection<TemplateResult> ret=regionTemplate.calculate(boList);
        List<ProductItem> productItemList=new ArrayList<>();
        long fee=0L;
        fee=ret.stream().mapToLong(bo->{productItemList.addAll(bo.getPack());return bo.getFee();}).sum();
        List<ProductItemVo> productItemVoList=productItemList.stream().map(bo->ProductItemVo.builder().orderItemId(bo.getId())
                .productId(bo.getProductId()).quantity(bo.getQuantity()).weight(bo.getWeight()).build()).collect(Collectors.toList());

        return FreightPriceVo.builder().freightPrice(fee).pack(productItemVoList).build();
    }
}
