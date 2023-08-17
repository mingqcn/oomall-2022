package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.Constants;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.controller.vo.ProductDraftVo;
import cn.edu.xmu.oomall.goods.dao.CategoryDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.dao.ProductDraftDao;
import cn.edu.xmu.oomall.goods.dao.bo.Category;
import cn.edu.xmu.oomall.goods.dao.bo.Product;
import cn.edu.xmu.oomall.goods.dao.bo.ProductDraft;
import cn.edu.xmu.oomall.goods.dao.bo.Shop;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

/**
 * @author wuzhicheng
 * @create 2022-12-03 23:49
 */
@Service
@Transactional
public class ProductDraftService {

    private Logger logger = LoggerFactory.getLogger(ProductDraftService.class);

    private static final Long NO_RELATE_PRODUCT=0L;
    private static final Long NO_RELATE = 0L;
    private static final Long PUBLIC_PID = 0L;

    private ProductDraftDao productDraftDao;

    private CategoryDao categoryDao;

    private ProductDao productDao;

    public ProductDraftService(ProductDraftDao productDraftDao, CategoryDao categoryDao, ProductDao productDao, RedisUtil redisUtil) {
        this.productDraftDao = productDraftDao;
        this.categoryDao = categoryDao;
        this.productDao = productDao;
        this.redisUtil = redisUtil;
    }

    private RedisUtil redisUtil;


    /**
     * 商铺管理员申请增加新的Product
     * @author wuzhicheng
     * @param shopId
     * @param name
     * @param originalPrice
     * @param categoryId
     * @param originPlace
     * @param user
     * @return
     */
    public SimpleProductDraftDto createSku(Long shopId, String name, Long originalPrice, Long categoryId, String originPlace, UserDto user) {

        logger.debug("createSku: shopId = {}", shopId);
        Category category = this.categoryDao.findById(categoryId);

        if(category.getParent()==null || category.getParent().getId()==PUBLIC_PID){
            throw new BusinessException(ReturnNo.CATEGORY_NOTALLOW, String.format(ReturnNo.CATEGORY_NOTALLOW.getMessage(), categoryId));
        }

        ProductDraft productDraft = ProductDraft.builder().shopId(shopId).name(name).originalPrice(originalPrice).categoryId(categoryId).originPlace(originPlace).productId(NO_RELATE_PRODUCT).build();
        ProductDraft save=this.productDraftDao.insert(productDraft, user);
        SimpleProductDraftDto simpleProductDraftDto = cloneObj(save, SimpleProductDraftDto.class);
        return simpleProductDraftDto;
    }

    /**
     * 管理员或店家物理删除审核中的Products
     * @author wuzhicheng
     * @param shopId
     * @param id
     * @param user
     */
    public void delProducts(Long shopId, Long id, UserDto user) {
        logger.debug("delProducts: shopId = {}, productId = {}", shopId, id);
        ProductDraft pd=this.productDraftDao.findById(id);
        if(!Objects.equals(pd.getShopId(), shopId) && shopId != Constants.PLATFORM){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "草稿商品", id, shopId));
        }
        this.productDraftDao.deleteById(id);
    }

    /**
     * 店家或管理员修改审核货品信息
     * @param shopId
     * @param id
     * @param productDraftVo
     * @return
     */
    public void modify(Long shopId, Long id, ProductDraftVo productDraftVo, UserDto user) {
        logger.debug("modifySku: shopId = {}, productId = {}", shopId, id);
        ProductDraft byId = this.productDraftDao.findById(id);
        if(!Objects.equals(byId.getShopId(), shopId) && shopId != Constants.PLATFORM){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "草稿商品", id, shopId));
        }

        ProductDraft build = ProductDraft.builder().name(productDraftVo.getName()).originalPrice(productDraftVo.getOriginalPrice()).categoryId(productDraftVo.getCategoryId()).originPlace(productDraftVo.getOriginPlace()).id(id).build();
        Category category = this.categoryDao.findById(build.getCategoryId());
        if(category!=null && category.getParent()==null){
            throw new BusinessException(ReturnNo.CATEGORY_NOTALLOW, String.format(ReturnNo.CATEGORY_NOTALLOW.getMessage(), build.getCategoryId()));
        }
        this.productDraftDao.save(build, user);
    }

    /**
     * 店家查询草稿商品
     * @param shopId
     * @param page
     * @param pageSize
     * @return
     */
    public PageDto<ProductDraftDto> getAllProductDraft(Long shopId, Integer page, Integer pageSize) {
        PageDto<ProductDraft> ret=null;
        if(shopId==Constants.PLATFORM){
            ret=this.productDraftDao.retrieveProductDraft(page, pageSize);
        } else{
            ret=this.productDraftDao.retrieveProductDraftByShopId(shopId, page, pageSize);
        }
        List<ProductDraftDto> collect = new ArrayList<>();
        if(null != ret && ret.getList().size() > 0){
            collect = ret.getList().stream().map(o -> {
                ProductDraftDto productDraftDto = cloneObj(o, ProductDraftDto.class);
                productDraftDto.setCreator(new IdNameDto(o.getCreatorId(), o.getCreatorName()));
                productDraftDto.setModifier(new IdNameDto(o.getModifierId(), o.getModifierName()));
                productDraftDto.setShop(cloneObj(o.getShop(), IdNameTypeDto.class));
                if(o.getCategory()!=null) productDraftDto.setCategory(cloneObj(o.getCategory(), IdNameDto.class));
                return productDraftDto;
            }).collect(Collectors.toList());
        }
        PageDto<ProductDraftDto> productDraftDtoPageDto = new PageDto<>(collect, page, pageSize);
        return productDraftDtoPageDto;
    }

    /**
     * 店家查看草稿货品信息详情
     * @param shopId
     * @param id
     * @return
     */
    public ProductDraftDto getProductDraft(Long shopId, Long id) {
        ProductDraft byId = this.productDraftDao.findById(id);
        if(byId.getShopId()!=shopId && shopId != Constants.PLATFORM){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "草稿商品", id, shopId));
        }
        ProductDraftDto productDraftDto = cloneObj(byId, ProductDraftDto.class);
        productDraftDto.setCreator(new IdNameDto(byId.getCreatorId(), byId.getCreatorName()));
        productDraftDto.setModifier(new IdNameDto(byId.getModifierId(), byId.getModifierName()));
        productDraftDto.setShop(cloneObj(byId.getShop(), IdNameTypeDto.class));
        if(byId.getCategory()!=null) productDraftDto.setCategory(cloneObj(byId.getCategory(), IdNameDto.class));
        return productDraftDto;
    }

    /**
     * 货品发布
     * @param shopId
     * @param id
     * @param user
     * @return
     */
    public IdNameDto putGoods(Long shopId, Long id, UserDto user) {
        logger.debug("putGoods: draftProductId = {}", id);
        if(shopId!=Constants.PLATFORM){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "上架商品", id, shopId));
        }
        ProductDraft productDraft = this.productDraftDao.findById(id);
        Product product = productDraft.getProduct();
        String key=null;
        IdNameDto retDto=null;
        if(product.getId()==NO_RELATE_PRODUCT || product.getId()==null){
            //发布货品
            product.setStatus(Product.OFFSHELF);
            product.setGoodsId(NO_RELATE);
            Product insert = this.productDao.insert(product, user);
            this.productDraftDao.deleteById(id);
            retDto=IdNameDto.builder().id(insert.getId()).name(insert.getName()).build();
        } else{
            //修改货品
            key = this.productDao.save(product, user);
            retDto=IdNameDto.builder().id(id).name(productDraft.getName()).build();
        }
        if (key!=null){
            redisUtil.del(key);
        }
        return retDto;
    }
}
