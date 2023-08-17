//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.goods.dao;


import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.functional.GetBo;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.dao.bo.Onsale;
import cn.edu.xmu.oomall.goods.dao.bo.Product;
import cn.edu.xmu.oomall.goods.dao.onsale.OnsaleExecutor;
import cn.edu.xmu.oomall.goods.dao.onsale.SpecOnSaleExecutor;
import cn.edu.xmu.oomall.goods.dao.onsale.ValidOnsaleExecutor;
import cn.edu.xmu.oomall.goods.dao.onsale.*;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.mapper.jpa.GoodsPoMapper;
import cn.edu.xmu.oomall.goods.mapper.jpa.ProductPoMapper;
import cn.edu.xmu.oomall.goods.mapper.po.GoodsPo;
import cn.edu.xmu.oomall.goods.mapper.po.ProductPo;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.util.Common.*;

/**
 * @author Ming Qiu
 **/
@Repository
@RefreshScope
public class ProductDao {

    private final static Logger logger = LoggerFactory.getLogger(ProductDao.class);

    private final static String KEY = "P%d";

    private final static String OTHER_KEY = "PO%d";

    @Value("${oomall.product.timeout}")
    private int timeout;


    private ProductPoMapper productPoMapper;
    private GoodsPoMapper goodsPoMapper;

    private OnsaleDao onsaleDao;
    private CategoryDao categoryDao;
    private ShopDao shopDao;

    private RedisUtil redisUtil;


    @Autowired
    public ProductDao(ProductPoMapper productPoMapper, GoodsPoMapper goodsPoMapper, OnsaleDao onSaleDao, CategoryDao categoryDao, ShopDao shopDao, RedisUtil redisUtil) {
        this.productPoMapper = productPoMapper;
        this.goodsPoMapper = goodsPoMapper;
        this.onsaleDao = onSaleDao;
        this.redisUtil = redisUtil;
        this.categoryDao = categoryDao;
        this.shopDao = shopDao;
    }

    /**
     * 用id查找对象
     *
     * @param id
     * @return product对象
     */
    public Product findProductById(Long id) throws RuntimeException {
        logger.debug("findProductById: id = {}", id);
        if (null == id){
            return null;
        }

        String key = String.format(KEY, id);
        ValidOnsaleProductFactory factory = new ValidOnsaleProductFactory(id);
        if (redisUtil.hasKey(key)){
            Product bo = (Product) redisUtil.get(key);
            factory.setBo(bo);
            return bo;
        }
        return findWithGetBo(id, factory);
    }

    /**
     * 用onsaleid查找Product对象
     * 不缓存onsale对象
     *
     * @param onsaleId
     * @return product对象
     */
    public Product findProductByOnsaleId(Long onsaleId) throws RuntimeException {
        logger.debug("findOnsaleById: id = {}", onsaleId);
        if (null == onsaleId){
            return null;
        }
        Onsale onsale = this.onsaleDao.findById(onsaleId);
        SpecOnsaleProductFactory factory = new SpecOnsaleProductFactory(onsaleId);
        return findWithGetBo(onsale.getProductId(), factory);
    }

    /**
     * 用beginTime和endTime查找在这段时间内有效的商品
     * @author Ming Qiu
     * <p>
     * date: 2022-12-10 19:33
     * @param beginTime
     * @param endTime
     * @return
     * @throws RuntimeException
     */
    public Product findProductByBeginEnd(Long productId, LocalDateTime beginTime, LocalDateTime endTime) throws RuntimeException{
        logger.debug("findProbuctByBeginEnd: beginTime = {}, endTime = {}", beginTime, endTime);
        if (null == productId){
            return null;
        }
        OverlapOnsaleProductFactory factory = new OverlapOnsaleProductFactory(productId, beginTime, endTime);
        return findWithGetBo(productId, factory);
    }

    /**
     * 按照不同的getBo方法获得bo对象
     * @author Ming Qiu
     * <p>
     * date: 2022-12-11 7:05
     * @param productId
     * @param getBoFunc
     * @return
     */
    private Product findWithGetBo(Long productId, GetBo<Product, ProductPo> getBoFunc){
        Optional<ProductPo> ret = productPoMapper.findById(productId);
        String key = String.format(KEY, productId);
        if (ret.isPresent()) {
            return getBoFunc.getBo(ret.get(),  Optional.of(key));
        } else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "产品", productId));
        }
    }



    /**
     * 用GoodsPo对象找Goods对象
     *
     * @param name
     * @return Goods对象列表，带关联的Product返回
     */
    public PageDto<Product> retrieveProductByName(String name, int page, int pageSize) throws RuntimeException {
        List<Product> productList = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ProductPo> pageObj = productPoMapper.findByNameEqualsAndStatusNot(name, Product.BANNED, pageable);
        if (!pageObj.isEmpty()) {
            productList = pageObj.stream().map(po ->{
                ValidOnsaleProductFactory factory = new ValidOnsaleProductFactory(po.getId());
                Product bo = factory.getBo(po, Optional.ofNullable(null));
                return bo;
            }).collect(Collectors.toList());
        } else {
            productList = new ArrayList<>();
        }
        logger.debug("retrieveProductByName: productList = {}", productList);
        return new PageDto<>(productList, page, pageSize);
    }




    /**
     * 用id查找对象
     *
     * @param goodsId goodsId
     * @return product对象
     */
    public List<Product> retrieveOtherProductById(Long goodsId) throws RuntimeException {
        if (null == goodsId){
            return null;
        }

        String key = String.format(OTHER_KEY, goodsId);
        if (redisUtil.hasKey(key)){
            List<Long> otherIds = (List<Long>) redisUtil.get(key);
            List<Product> retList = otherIds.stream().map(productId -> this.findProductById(productId)).filter(obj -> null != obj).collect(Collectors.toList());
            return retList;
        }

        Pageable pageable = PageRequest.of(0, MAX_RETURN);
        Page<ProductPo> ret = productPoMapper.findByGoodsIdEquals(goodsId, pageable);
        if (ret.isEmpty()){
            return new ArrayList<>();
        }else{
            List<Product> retList = ret.stream().map(productPo -> {
                ValidOnsaleProductFactory validOnsaleProductFactory = new ValidOnsaleProductFactory(productPo.getId());
                Product bo = validOnsaleProductFactory.getBo(productPo, Optional.empty());
                return bo;
            }).collect(Collectors.toList());
            redisUtil.set(key, (ArrayList<Long>) retList.stream().map(obj -> obj.getId()).collect(Collectors.toList()), timeout);
            return retList;
        }
    }

    /**
     * 根据templateId找Product
     * @param templateId
     * @return
     */
    public List<Product> findProductByTemplateId(Long templateId) {
        Pageable pageable = PageRequest.of(0, MAX_RETURN);
        Page<ProductPo> ret = this.productPoMapper.findByTemplateIdEquals(templateId, pageable);
        if(ret.isEmpty()){
            return new ArrayList<>();
        } else{
            List<Product> retList = ret.stream().map(productPo -> this.findProductById(productPo.getId())).collect(Collectors.toList());
            return retList;
        }
    }


    @ToString
    @NoArgsConstructor
    public abstract class ProductFactory implements GetBo<Product, ProductPo>{

        public Product getBo(ProductPo po,  Optional<String> redisKey){
            Product bo = Product.builder().id(po.getId()).creatorId(po.getCreatorId()).creatorName(po.getCreatorName()).gmtCreate(po.getGmtCreate()).gmtModified(po.getGmtModified()).modifierId(po.getModifierId()).modifierName(po.getModifierName())
                    .templateId(po.getTemplateId()).shopId(po.getShopId()).shopLogisticId(po.getShopLogisticId()).categoryId(po.getCategoryId()).goodsId(po.getGoodsId())
                    .name(po.getName()).skuSn(po.getSkuSn()).originPlace(po.getOriginPlace()).status(po.getStatus()).unit(po.getUnit()).weight(po.getWeight()).originalPrice(po.getOriginalPrice()).commissionRatio(po.getCommissionRatio()).barcode(po.getBarcode()).build();
            this.setBo(bo);
            redisKey.ifPresent(key -> redisUtil.set(key, bo, timeout));
            return bo;
        }

        public void setBo(Product bo){
            bo.setCategoryDao(categoryDao);
            bo.setProductDao(ProductDao.this);
            bo.setShopDao(shopDao);
            bo.setOnsaleExecutor(this.getExecutor());
        }

        protected abstract OnsaleExecutor getExecutor();
    }

    /**
     * 生产特定历史销售信息Product
     */
    @ToString(callSuper = true)
    public class SpecOnsaleProductFactory extends ProductFactory {

        private Long onsaleId;

        public SpecOnsaleProductFactory(Long onsaleId) {
            super();
            this.onsaleId = onsaleId;
        }

        @Override
        protected OnsaleExecutor getExecutor(){
            return new SpecOnSaleExecutor(onsaleDao, this.onsaleId);
        }
    }

    /**
     * 生产当前有效销售信息Product
     */
    @ToString(callSuper = true)
    public class ValidOnsaleProductFactory extends ProductFactory {

        private Long productId;

        public ValidOnsaleProductFactory(Long productId) {
            super();
            this.productId = productId;
        }

        @Override
        protected OnsaleExecutor getExecutor(){
            return new ValidOnsaleExecutor(onsaleDao, this.productId);
        }
    }

    public class OverlapOnsaleProductFactory extends ProductFactory {

        private Long productId;
        private LocalDateTime beginTime;
        private LocalDateTime endTime;

        public OverlapOnsaleProductFactory(Long productId, LocalDateTime beginTime, LocalDateTime endTime) {
            this.productId = productId;
            this.beginTime = beginTime;
            this.endTime = endTime;
        }

        @Override
        protected OnsaleExecutor getExecutor(){
            return new TimeOverlapOnsaleExecutor(onsaleDao, beginTime, endTime, productId);
        }
    }

    /**
     * 根据店铺id和条形码查找商品
     * @author wuzhicheng
     * @param shopId
     * @param barCode
     * @param page
     * @param pageSize
     * @return
     */
    public PageDto<Product> retrieveByShopIdAndBarCode(Long shopId, String barCode, Integer page, Integer pageSize) {
        List<Product> ret = new ArrayList<>();
        Pageable pageable = PageRequest.of(page-1, pageSize);
        Page<ProductPo> pos=this.productPoMapper.findByShopIdEqualsAndBarcodeEquals(shopId, barCode, pageable);
        ret=pos.stream().map(po -> {
            Long id = po.getId();
            String key = String.format(KEY, id);
            ValidOnsaleProductFactory factory = new ValidOnsaleProductFactory(id);
            Product bo = factory.getBo(po, Optional.empty());
            return bo;
        }).collect(Collectors.toList());
        logger.debug("bos size:{}", ret.size());
        return new PageDto<Product>(ret, page, pageSize);
    }

    /**
     * 插入产品
     * @param product
     * @param user
     * @return
     */
    public Product insert(Product product, UserDto user){
        ProductPo productPo = cloneObj(product, ProductPo.class);
        putGmtFields(productPo, "create");
        putUserFields(productPo, "creator", user);
        productPo.setId(null);
        if(productPo.getFreeThreshold()==null){
            productPo.setFreeThreshold(-1);
        }
        ProductPo save = this.productPoMapper.save(productPo);
        Product productRet = cloneObj(save, Product.class);
        return productRet;
    }

    /**
     * 更新
     * @author wuzhicheng
     * @param product
     * @param user
     * @return
     */
    public String save(Product product, UserDto user) {
        ProductPo productPo = cloneObj(product, ProductPo.class);
        putGmtFields(productPo, "modified");
        putUserFields(productPo, "modifier", user);
        ProductPo save = this.productPoMapper.save(productPo);
        if(save.getId()==-1){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商品", product.getId()));
        }
        return String.format(KEY, product.getId());
    }

    /**
     * 根据模板id查询product
     * @author wuzhicheng
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    public PageDto<Product> retrieveProductByTemplateId(Long id, Integer page, Integer pageSize) {
        List<Product> ret = new ArrayList<>();
        Pageable pageable = PageRequest.of(page-1, pageSize);
        Page<ProductPo> pos =  this.productPoMapper.findByTemplateIdEquals(id, pageable);
        if(null != pos && !pos.isEmpty()) {
            ret = pos.stream().map(po -> {
                Long pid = po.getId();
                String key = String.format(KEY, pid);
                ValidOnsaleProductFactory factory = new ValidOnsaleProductFactory(pid);
                Product bo = factory.getBo(po, Optional.empty());
                return bo;
            }).collect(Collectors.toList());
            logger.debug("bos size:{}", ret.size());
        }
        return new PageDto<Product>(ret, page, pageSize);
    }

    /**
     * 创建新的goods
     * @return
     */
    public GoodsPo insertGoods(){
        GoodsPo goodsPo = new GoodsPo();
        GoodsPo save = goodsPoMapper.save(goodsPo);
        return save;
    }

    /**
     * 根据Category Id更新Product
     * @param categoryId 商品类目Id
     * @param product 新的Product，空属性不更新
     * @param userDto 操作人
     */
    public void saveByCategoryId(Long categoryId, Product product, UserDto userDto) throws RuntimeException {
        ProductPo po = cloneObj(product, ProductPo.class);
        putUserFields(po, "modifier", userDto);
        putGmtFields(po, "modified");
        logger.debug("saveByCategoryId: po = {}", po);
        this.productPoMapper.findByCategoryIdEquals(categoryId, PageRequest.of(0, MAX_RETURN)).forEach(productPo -> {
            po.setId(productPo.getId());
            this.productPoMapper.save(po);
            redisUtil.del(String.format(KEY, productPo.getId()));
        });
    }
}
