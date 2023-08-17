package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.goods.dao.bo.ProductDraft;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.mapper.jpa.ProductDraftPoMapper;
import cn.edu.xmu.oomall.goods.mapper.po.ProductDraftPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.util.Common.*;

/**
 * @author wuzhicheng
 * @create 2022-12-03 23:44
 */
@Repository
public class ProductDraftDao {
    private Logger logger = LoggerFactory.getLogger(ProductDraftDao.class);


    private ProductDraftPoMapper productDraftPoMapper;
    private CategoryDao categoryDao;
    private ProductDao productDao;
    private ShopDao shopDao;

    @Autowired
    public ProductDraftDao(ProductDraftPoMapper productDraftPoMapper, CategoryDao categoryDao, ProductDao productDao, ShopDao shopDao) {
        this.productDraftPoMapper = productDraftPoMapper;
        this.categoryDao = categoryDao;
        this.productDao = productDao;
        this.shopDao = shopDao;
    }

    private ProductDraft getBo(ProductDraftPo po){
        ProductDraft bo= ProductDraft.builder().id(po.getId()).creatorId(po.getCreatorId()).creatorName(po.getCreatorName()).modifierId(po.getModifierId()).modifierName(po.getModifierName())
                .gmtCreate(po.getGmtCreate()).gmtModified(po.getGmtModified()).shopId(po.getShopId()).productId(po.getProductId()).categoryId(po.getCategoryId()).name(po.getName()).originalPrice(po.getOriginalPrice()).originPlace(po.getOriginPlace()).build();
        this.setBo(bo);
        return bo;
    }

    private void setBo(ProductDraft bo){
        bo.setCategoryDao(this.categoryDao);
        bo.setProductDao(this.productDao);
        bo.setShopDao(this.shopDao);
    }


    /**
     * 插入商品
     * @param productDraft
     * @param user
     * @return
     */
    public ProductDraft insert(ProductDraft productDraft, UserDto user){
        ProductDraftPo productDraftPo = cloneObj(productDraft, ProductDraftPo.class);
        putGmtFields(productDraftPo, "create");
        putUserFields(productDraftPo, "creator", user);
        productDraft.setId(null);
        ProductDraftPo save = this.productDraftPoMapper.save(productDraftPo);
        ProductDraft draft = cloneObj(save, ProductDraft.class);
        return draft;
    }

    /**
     * 更新
     * @author wuzhicheng
     * @param productDraft
     * @param user
     * @return
     */
    public void save(ProductDraft productDraft, UserDto user) {
        ProductDraftPo productDraftPo = cloneObj(productDraft, ProductDraftPo.class);
        putGmtFields(productDraftPo, "modified");
        putUserFields(productDraftPo, "modifier", user);
        ProductDraftPo save = this.productDraftPoMapper.save(productDraftPo);
        if(save.getId()==null){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(),"草稿商品", productDraft.getId()));
        }
    }

    /**
     * 根据id查询
     * @author wuzhicheng
     * @param id
     * @return
     */
    public ProductDraft findById(Long id) {
        logger.debug("findById: id ={}", id);
        if (null == id) {
            return null;
        }
        Optional<ProductDraftPo> retObj = this.productDraftPoMapper.findById(id);
        if (retObj.isEmpty() ){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "草稿商品", id));
        }else{
            ProductDraftPo po = retObj.get();
            return this.getBo(po);
        }
    }

    /**
     * 根据id物理删除
     * @author wuzhicheng
     * @param id
     */
    public void deleteById(Long id) {
        this.productDraftPoMapper.deleteById(id);
    }

    /**
     * 根据shopid查询草稿商品
     * @param shopId
     * @param page
     * @param pageSize
     * @return
     */
    public PageDto<ProductDraft> retrieveProductDraftByShopId(Long shopId, Integer page, Integer pageSize) {
        List<ProductDraft> ret = new ArrayList<>();
        Pageable pageable = PageRequest.of(page-1, pageSize);
        Page<ProductDraftPo> pos =  this.productDraftPoMapper.findByShopIdEquals(shopId, pageable);
        if(null != pos && !pos.isEmpty()) {
            ret = pos.stream().map(o->getBo(o)).collect(Collectors.toList());
            logger.debug("bos size:{}", ret.size());
        }
        return new PageDto<ProductDraft>(ret, page, pageSize);
    }

    /**
     * 查询所有草稿商品
     * @param page
     * @param pageSize
     * @return
     */
    public PageDto<ProductDraft> retrieveProductDraft(Integer page, Integer pageSize) {
        List<ProductDraft> ret = new ArrayList<>();
        Pageable pageable = PageRequest.of(page-1, pageSize);
        Page<ProductDraftPo> pos =  this.productDraftPoMapper.findAll(pageable);
        if(null != pos && !pos.isEmpty()) {
            ret = pos.stream().map(o->getBo(o)).collect(Collectors.toList());
            logger.debug("bos size:{}", ret.size());
        }
        return new PageDto<ProductDraft>(ret, page, pageSize);
    }

    /*
     * 根据Category Id更新ProductDraft
     * @param categoryId 商品类目Id
     * @param productDraft 新的ProductDraft，空属性不更新
     */
    public void saveByCategoryId(Long categoryId, ProductDraft productDraft) throws RuntimeException {
        ProductDraftPo po = cloneObj(productDraft, ProductDraftPo.class);
        this.productDraftPoMapper.findByCategoryIdEquals(categoryId, PageRequest.of(0, MAX_RETURN)).forEach(productDraftPo -> {
            po.setId(productDraftPo.getId());
            this.productDraftPoMapper.save(po);
        });
    }
}
