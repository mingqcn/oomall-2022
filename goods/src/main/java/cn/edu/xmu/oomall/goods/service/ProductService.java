package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.BloomFilter;
import cn.edu.xmu.javaee.core.model.Constants;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.goods.controller.vo.CreateCategoryVo;
import cn.edu.xmu.oomall.goods.controller.vo.ProductModVo;
import cn.edu.xmu.oomall.goods.controller.vo.UpdateCategoryVo;
import cn.edu.xmu.oomall.goods.dao.CategoryDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.dao.ProductDraftDao;
import cn.edu.xmu.oomall.goods.dao.bo.*;
import cn.edu.xmu.oomall.goods.dao.bo.SimpleProductDto;
import cn.edu.xmu.oomall.goods.mapper.po.GoodsPo;
import cn.edu.xmu.oomall.goods.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static cn.edu.xmu.javaee.core.util.Common.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private static final Long NO_RELATE = 0L;

    private static final Long PUBLIC_PID = 0L;

    private CategoryDao categoryDao;

    private ProductDao productDao;

    private ProductDraftDao productDraftDao;

    private RedisUtil redisUtil;

    @Autowired
    public ProductService(ProductDao productDao, RedisUtil redisUtil, CategoryDao categoryDao, ProductDraftDao productDraftDao) {
        this.productDao = productDao;
        this.categoryDao = categoryDao;
        this.redisUtil = redisUtil;
        this.productDraftDao = productDraftDao;
    }

    /**
     * 顾客获取某个商品信息，仅展示相关内容
     *
     * @param id 商品id
     * @return 商品对象
     */
    @Transactional
    public ProductDto getCustomerProductById(Long id) throws BusinessException {
        logger.debug("getCustomerProductById: productId = {}", id);
        Product bo = this.findProductById(id);
        List<Product> other = bo.getOtherProduct();
        List<IdNameTypeDto> actList = bo.getActList().stream().map(al -> {
            return IdNameTypeDto.builder().id(al.getId()).name(al.getName()).build();
        }).collect(Collectors.toList());
        Shop shop = bo.getShop();
        ProductDto dto = ProductDto.builder().id(bo.getId()).barcode(bo.getBarcode()).beginTime(bo.getBeginTime()).endTime(bo.getEndTime())
                .maxQuantity(bo.getMaxQuantity()).quantity(bo.getQuantity()).name(bo.getName()).weight(bo.getWeight()).unit(bo.getUnit())
                .originalPrice(bo.getOriginalPrice()).price(bo.getPrice()).originPlace(bo.getOriginPlace()).skuSn(bo.getSkuSn()).status(bo.getStatus())
                .otherProduct(other.stream().map(product -> SimpleProductDto.builder().id(product.getId()).status(product.getStatus()).price(product.getPrice()).quantity(product.getQuantity()).name(product.getName()).build()).collect(Collectors.toList()))
                .shop(Shop.builder().id(shop.getId()).name(shop.getName()).type(shop.getType()).build()).category(IdNameDto.builder().id(bo.getCategory().getId()).name(bo.getCategory().getName()).build())
                .actList(actList).freeThreshold(bo.getFreeThreshold()).build();
        logger.debug("getCustomerProductById: dto = {}", dto);
        return dto;
    }


    /**
     * 店家查看货品信息详情
     *
     * @param shopId
     * @param id
     * @return
     */
    public FullProductDto getAdminProductById(Long shopId, Long id) {
        logger.debug("getAdminProductById: productId = {}", id);
        //查询Product
        Product productById = this.findProductById(id);

        if (!Objects.equals(productById.getShopId(), shopId) && shopId != Constants.PLATFORM) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商品", id, shopId));
        }
        //创建返回对象
        FullProductDto fullProductDto = cloneObj(productById, FullProductDto.class);

        //设置返回对象属性
        fullProductDto.setShop(cloneObj(productById.getShop(), IdNameTypeDto.class));

        fullProductDto.setOtherProducts(productById.getOtherProduct().stream().map(o -> {
            return cloneObj(o, IdNameDto.class);
        }).collect(Collectors.toList()));

        fullProductDto.setCategory(cloneObj(productById.getCategory(), IdNameDto.class));

        fullProductDto.setTemplate(cloneObj(productById.getTemplate(), IdNameDto.class));

        fullProductDto.setCreator(new IdNameDto(productById.getCreatorId(), productById.getCreatorName()));
        fullProductDto.setModifier(new IdNameDto(productById.getModifierId(), productById.getModifierName()));
        fullProductDto.setFreeThreshold(productById.getFreeThreshold());
        fullProductDto.setCommissionRatio(productById.getCommissionRatio());
        return fullProductDto;
    }


    /**
     * get product by id
     *
     * @param id
     * @return
     * @throws BusinessException
     */
    private Product findProductById(Long id) throws BusinessException {
        logger.debug("findProductById: id = {}", id);

        String key = BloomFilter.PRETECT_FILTERS.get("ProductId");
        if (redisUtil.bfExist(key, id)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "产品", id));
        }

        Product bo = null;
        try {
            bo = productDao.findProductById(id);
        } catch (BusinessException e) {
            if (ReturnNo.RESOURCE_ID_NOTEXIST == e.getErrno()) {
                redisUtil.bfAdd(key, id);
            }
            throw e;
        }
        return bo;
    }

    /**
     * 查找正式商品
     *
     * @param shopId
     * @param barCode
     * @param page
     * @param pageSize
     * @return
     * @author wuzhicheng
     */
    public PageDto<SimpleProductDto> getSkuList(Long shopId, String barCode, Integer page, Integer pageSize) {
        logger.debug("getSkuList: shopId = {}, barCode = {}", shopId, barCode);
        PageDto<Product> products = this.productDao.retrieveByShopIdAndBarCode(shopId, barCode, page, pageSize);
        List<Product> list = products.getList();
        List<SimpleProductDto> ret = new ArrayList<>();
        list.forEach(o -> {
            //禁售商品不返回
            if (!Objects.equals(o.getStatus(), Product.BANNED)) {
                ret.add(cloneObj(o, SimpleProductDto.class));
            }
        });
        return new PageDto<>(ret, page, pageSize);
    }

    /**
     * 查询商品的全部状态
     *
     * @return
     */
    public List<StateDto> getStates() {
        logger.debug("getStates");
        Map<Byte, String> statusnames = Product.STATUSNAMES;
        List<StateDto> stateDtos = new ArrayList<>();
        statusnames.forEach((k, v) -> {
            StateDto stateDto = new StateDto();
            stateDto.setName(v);
            stateDto.setCode(k);
            stateDtos.add(stateDto);
        });
        return stateDtos;
    }


    /**
     * 店家修改货品信息
     *
     * @param shopId
     * @param id
     * @param user
     * @param productModVo
     * @author wuzhicheng
     */
    public void updateProduct(Long shopId, Long id, UserDto user, ProductModVo productModVo) {
        logger.debug("updateProduct: productId = {}", id);
        //查询Product
        Product productById = this.productDao.findProductById(id);

        if (productById.getShopId() != shopId && shopId != Constants.PLATFORM) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商品", id, shopId));
        }

        if (productModVo.getCommissionRatio() != null && shopId != Constants.PLATFORM) {
            //修改了commissionratio
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商品", id, shopId));
        }

        //不能是一级目录
        if (productModVo.getCategoryId() != null) {
            Long categoryId = productModVo.getCategoryId();
            Category category = this.categoryDao.findById(categoryId);

            if (category.getParent() == null || category.getParent().getId() == PUBLIC_PID) {
                throw new BusinessException(ReturnNo.CATEGORY_NOTALLOW, String.format(ReturnNo.CATEGORY_NOTALLOW.getMessage(), categoryId));
            }
            productById.setCategoryId(categoryId);
        }

        if (productModVo.getName() != null || productModVo.getOriginPlace() != null || productModVo.getOriginalPrice() != null || productModVo.getCategoryId() != null) {
            //需要重新审核
            ProductDraft productDraft = cloneObj(productModVo, ProductDraft.class);
            productDraft.setProductId(id);
            productDraft.setShopId(productById.getShopId());
            productDraft.setName(productById.getName());
            productDraftDao.insert(productDraft, user);
            return;
        }


        Product build = Product.builder().id(id).skuSn(productModVo.getSkuSn()).name(productModVo.getName()).originalPrice(productModVo.getOriginalPrice()).weight(productModVo.getWeight())
                .barcode(productModVo.getBarcode()).unit(productModVo.getUnit()).originalPrice(productModVo.getOriginalPrice()).shopLogisticId(productModVo.getShopLogisticsId())
                .templateId(productModVo.getTemplateId()).commissionRatio(productModVo.getCommissionRatio()).build();

        String key = this.productDao.save(build, user);
        redisUtil.del(key);
    }

    /**
     * 查询商品的运费模板
     *
     * @param shopId
     * @param id
     * @return
     * @author wuzhicheng
     */
    public Template getProductTemplate(Long shopId, Long id) {
        logger.debug("getProductTemplate: productId = {}", id);
        Product productById = this.productDao.findProductById(id);

        if (!Objects.equals(productById.getShopId(), shopId) && shopId != Constants.PLATFORM) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商品", id, shopId));
        }
        return productById.getTemplate();
    }

    /**
     * 管理员查看运费模板用到的商品
     *
     * @param shopId
     * @param id
     * @param page
     * @param pageSize
     * @return
     * @author wuzhicheng
     */
    public PageDto<IdNameDto> getTemplateProduct(Long shopId, Long id, Integer page, Integer pageSize) {
        logger.debug("getTemplateProduct: templateId = {}", id);
        if (shopId != Constants.PLATFORM) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "运费模板", id, shopId));
        }
        PageDto<Product> list = this.productDao.retrieveProductByTemplateId(id, page, pageSize);
        List<Product> products = list.getList();
        List<IdNameDto> collect = products.stream().map(o -> {
            return cloneObj(o, IdNameDto.class);
        }).collect(Collectors.toList());
        PageDto<IdNameDto> idNameDtoPageDto = new PageDto<>(collect, page, pageSize);
        return idNameDtoPageDto;
    }


    /**
     * 管理员解禁商品
     *
     * @param shopId
     * @param id
     * @param user
     * @return
     */
    public void allowGoods(Long shopId, Long id, UserDto user) {
        logger.debug("allowGoods: productId = {}", id);
        Product productById = this.productDao.findProductById(id);
        if (shopId != Constants.PLATFORM) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "上架商品", id, shopId));
        }
        if (Objects.equals(productById.getStatus(), Product.BANNED)) {
            productById.setStatus(Product.OFFSHELF);
            String key = this.productDao.save(productById, user);
            redisUtil.del(key);
        } else {
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "商品", id, "非禁止状态"));
        }
    }

    /**
     * 管理员禁售商品
     *
     * @param shopId
     * @param id
     * @param user
     * @return
     */
    public void prohibitGoods(Long shopId, Long id, UserDto user) {
        logger.debug("prohibitGoods: productId = {}", id);
        Product productById = this.productDao.findProductById(id);
        if (shopId != Constants.PLATFORM) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "上架商品", id, shopId));
        }
        if (!Objects.equals(productById.getStatus(), Product.BANNED)) {
            productById.setStatus(Product.BANNED);
            String key = this.productDao.save(productById, user);
            redisUtil.del(key);
        } else {
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "商品", id, "禁止状态"));
        }
    }

    /**
     * 将两个商品设置为相关
     *
     * @param shopId
     * @param id
     * @param productId
     * @return
     */
    public void relateProductId(Long shopId, Long id, Long productId, UserDto user) {
        Product product1 = this.productDao.findProductById(id);
        Product product2 = this.productDao.findProductById(productId);

        if (shopId != Constants.PLATFORM) {
            if (product1.getShopId() != shopId)
                throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商品", product1.getId(), shopId));
            if (product2.getShopId() != shopId)
                throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商品", product2.getId(), shopId));
        }
        String key1 = null;
        String key2 = null;
        if (product1.getGoodsId() == NO_RELATE && product2.getGoodsId() == NO_RELATE) {
            //两个都需要更新
            GoodsPo save = productDao.insertGoods();
            product1.setGoodsId(save.getId());
            product2.setGoodsId(save.getId());
            key1 = this.productDao.save(product1, user);
            key2 = this.productDao.save(product2, user);
        } else if (product1.getGoodsId() == NO_RELATE) {
            //更新前面的
            product1.setGoodsId(product2.getGoodsId());
            key1 = this.productDao.save(product1, user);
        } else {
            //更新后面的
            product2.setGoodsId(product1.getGoodsId());
            key2 = this.productDao.save(product2, user);
        }
        if (key1 != null) {
            redisUtil.del(key1);
        }
        if (key2 != null) {
            redisUtil.del(key2);
        }
    }

    /**
     * 取消两个商品的相关
     *
     * @param shopId
     * @param id
     * @param productId
     * @return
     */
    public void delRelateProduct(Long shopId, Long id, Long productId, UserDto user) {
        Product product1 = this.productDao.findProductById(id);
        Product product2 = this.productDao.findProductById(productId);

        if (shopId != Constants.PLATFORM) {
            if (product1.getShopId() != shopId)
                throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商品", product1.getId(), shopId));
            if (product2.getShopId() != shopId)
                throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商品", product2.getId(), shopId));
        }

        product2.setGoodsId(NO_RELATE);
        String key2 = this.productDao.save(product2, user);
        if (redisUtil.hasKey(key2)) {
            redisUtil.del(key2);
        }
    }


    /**
     * 删除商品的运费模板
     *
     * @param templateId
     */
    public void deleteTemplateByTemplateId(Long templateId, UserDto userDto) {
        List<Product> productList = this.productDao.findProductByTemplateId(templateId);
        List<String> keys = new ArrayList<>();
        productList.stream().peek(o -> o.setTemplateId(null)).forEach(o -> {
            String save = this.productDao.save(o, userDto);
            keys.add(save);
        });
        keys.forEach(redisUtil::del);
    }

    /**
     * 获取某个商品的历史销售信息
     *
     * @param id onsaleid
     * @return 商品对象
     */
    @Transactional
    public ProductDto findOnsaleById(Long id) throws BusinessException {
        logger.debug("findOnsaleById: id = {}", id);
        String key = BloomFilter.PRETECT_FILTERS.get("OnsaleId");
        if (redisUtil.bfExist(key, id)) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "销售", id));
        }
        ProductDto dto = null;
        try {
            Product bo = productDao.findProductByOnsaleId(id);
            List<Product> other = bo.getOtherProduct();
            List<Activity> actList = bo.getActList();
            List<Activity> share = actList.stream().filter(act -> act.getClass().equals(ShareAct.class)).collect(Collectors.toList());
            dto = ProductDto.builder().id(bo.getId()).barcode(bo.getBarcode()).beginTime(bo.getBeginTime()).endTime(bo.getEndTime()).
                    category(IdNameDto.builder().name(bo.getCategory().getName()).id(bo.getCategory().getId()).build()).freeThreshold(bo.getFreeThreshold()).
                    actList(actList.stream().map(activity -> IdNameTypeDto.builder().id(activity.getId()).name(activity.getName()).type(bo.getValidOnsale().getType()).build()).collect(Collectors.toList()))
                    .shareable((share.size() == 0) ? Boolean.FALSE : Boolean.TRUE)
                    .maxQuantity(bo.getMaxQuantity()).quantity(bo.getQuantity()).name(bo.getName()).weight(bo.getWeight()).unit(bo.getUnit())
                    .originalPrice(bo.getOriginalPrice()).price(bo.getPrice()).originPlace(bo.getOriginPlace()).skuSn(bo.getSkuSn()).status(bo.getStatus())
                    .otherProduct(other.stream().map(product -> SimpleProductDto.builder().id(product.getId()).status(product.getStatus()).price(product.getPrice()).quantity(product.getQuantity()).name(product.getName()).build()).collect(Collectors.toList())).build();
            logger.debug("findOnsaleById: dto = {}", dto);
        } catch (BusinessException e) {
            if (ReturnNo.RESOURCE_ID_NOTEXIST == e.getErrno()) {
                redisUtil.bfAdd(key, id);
            }
            throw e;
        }
        return dto;
    }
}
