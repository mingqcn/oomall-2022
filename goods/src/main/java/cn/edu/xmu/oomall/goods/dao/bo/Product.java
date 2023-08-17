//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.goods.dao.CategoryDao;
import cn.edu.xmu.oomall.goods.dao.OnsaleDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.dao.onsale.OnsaleExecutor;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.service.dto.IdNameDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product extends OOMallObject implements Serializable {

    @ToString.Exclude
    @JsonIgnore
    private  final static Logger logger = LoggerFactory.getLogger(Product.class);

    /**
     * 共三种状态
     */
    //禁售中
    @ToString.Exclude
    @JsonIgnore
    public static final  Byte BANNED = 0;
    //下架
    @ToString.Exclude
    @JsonIgnore
    public static final  Byte OFFSHELF  = 1;
    //上架
    @ToString.Exclude
    @JsonIgnore
    public static final  Byte ONSHELF  = 2;

    /**
     * 状态和名称的对应
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Map<Byte, String> STATUSNAMES = new HashMap(){
        {
            put(OFFSHELF, "下架");
            put(ONSHELF, "上架");
            put(BANNED, "禁售");
        }
    };
    /**
     * 获得当前状态名称
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:43
     * @return
     */
    @JsonIgnore
    public String getStatusName(){
        return STATUSNAMES.get(this.status);
    }

    @Builder
    public Product(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, String skuSn, String name, Long originalPrice, Long weight, String barcode, String unit, String originPlace, Long shopLogisticId, Integer commissionRatio, Byte status, Long goodsId, List<Product> otherProduct, ProductDao productDao, Onsale validOnsale, OnsaleExecutor onsaleExecutor, Long categoryId, Category category, CategoryDao categoryDao, Long shopId, Long templateId) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.skuSn = skuSn;
        this.name = name;
        this.originalPrice = originalPrice;
        this.weight = weight;
        this.barcode = barcode;
        this.unit = unit;
        this.originPlace = originPlace;
        this.shopLogisticId = shopLogisticId;
        this.commissionRatio = commissionRatio;
        this.status = status;
        this.goodsId = goodsId;
        this.otherProduct = otherProduct;
        this.productDao = productDao;
        this.validOnsale = validOnsale;
        this.onsaleExecutor = onsaleExecutor;
        this.categoryId = categoryId;
        this.category = category;
        this.categoryDao = categoryDao;
        this.shopId = shopId;
        this.templateId = templateId;
    }

    @Getter
    @Setter
    private String skuSn;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Long originalPrice;

    @Getter
    @Setter
    private Long weight;

    @Getter
    @Setter
    private String barcode;

    @Getter
    @Setter
    private String unit;

    @Getter
    @Setter
    private String originPlace;

    @Getter
    @Setter
    private Long shopLogisticId;

    @Setter
    private Integer commissionRatio;

    public Integer getCommissionRatio(){
        if (null == this.commissionRatio && null != this.getCategory()){
            this.commissionRatio = this.getCategory().getCommissionRatio();
        }
        return this.commissionRatio;
    }

    @Setter
    private Byte status;

    /**
     * 获得商品状态
     * @return
     */
    public Byte getStatus() {
        logger.debug("getStatus: id ={}",this.id);
        LocalDateTime now = LocalDateTime.now();
        if ((Product.BANNED == this.status)) {
            return Product.BANNED;
        }else{
            if (null == this.getValidOnsale()){
                return Product.OFFSHELF;
            }else{
                if (this.getValidOnsale().getBeginTime().isBefore(now) && this.getValidOnsale().getEndTime().isAfter(now)) {
                    return Product.ONSHELF;
                }else{
                    return Product.OFFSHELF;
                }
            }
        }
    }

    @Setter
    @Getter
    private Long goodsId;
    /**
     * 相关商品
     */
    @JsonIgnore
    @ToString.Exclude
    private List<Product> otherProduct;

    @Setter
    @JsonIgnore
    private ProductDao productDao;


    public List<Product> getOtherProduct(){
        if (null == this.otherProduct && null != this.productDao){
            this.otherProduct = this.productDao.retrieveOtherProductById(this.goodsId);
        }
        return this.otherProduct;
    }
    /**
     * 有效上架， 包括即将上架
     */
    @JsonIgnore
    @Setter
    private Onsale validOnsale;

    @Setter
    @JsonIgnore
    private OnsaleExecutor onsaleExecutor;

    /**
     * 采用command模式获取不同的onsale
     *
     * @author Ming Qiu
     * <p>
     * date: 2022-12-04 19:21
     * @return
     */
    public Onsale getValidOnsale(){
        if (null == this.validOnsale && null != this.onsaleExecutor){
            logger.debug("getValidOnsale: onsaleExecutor = {}", this.onsaleExecutor);
            this.validOnsale = this.onsaleExecutor.execute();
        }

        logger.debug("getValidOnsale: validOnsale = {}", this.validOnsale);
        if (this.validOnsale.getId().equals(OnsaleDao.NOTEXIST.getId())){
            return null;
        }
        return this.validOnsale;
    }

    @JsonIgnore
    public Long getPrice() {
        if (null != this.getValidOnsale()) {
            return this.validOnsale.getPrice();
        } else {
            return null;
        }
    }

    @JsonIgnore
    public Integer getQuantity() {
        if (null != this.getValidOnsale()) {
            return this.validOnsale.getQuantity();
        } else {
            return null;
        }
    }

    @JsonIgnore
    public LocalDateTime getBeginTime() {
        if (null != this.getValidOnsale()) {
            return this.validOnsale.getBeginTime();
        } else {
            return null;
        }
    }

    @JsonIgnore
    public LocalDateTime getEndTime() {
        if (null != this.getValidOnsale()) {
            return this.validOnsale.getEndTime();
        } else {
            return null;
        }
    }

    @JsonIgnore
    public Integer getMaxQuantity() {
        if (null != this.getValidOnsale()) {
            return this.validOnsale.getMaxQuantity();
        } else {
            return null;
        }
    }

    @Setter
    @Getter
    private Long categoryId;
    /**
     * 所属分类
     */
    @JsonIgnore
    private Category category;

    @Setter
    @JsonIgnore
    private CategoryDao categoryDao;


    public Category getCategory(){
        if (null == this.category && null != this.categoryDao){
            try {
                this.category = this.categoryDao.findById(this.categoryId);
            }catch (BusinessException e){
                if (ReturnNo.RESOURCE_ID_NOTEXIST == e.getErrno()){
                    this.categoryId = null;
                    logger.error("getCategory: product(id = {})'s categoryId is invalid.", id);
                }
            }
        }
        return this.category;
    }

    @JsonIgnore
    public List<Activity> getActList(){
        if(this.getValidOnsale()!=null){
            return this.getValidOnsale().getActList();
        }
        return new ArrayList<>();
    }

    @Setter
    @Getter
    private Long shopId;

    @JsonIgnore
    private Shop shop;

    @JsonIgnore
    @ToString.Exclude
    @Setter
    private ShopDao shopDao;

    public Shop getShop(){
        if (null == this.shopId){
            return null;
        }

        if (null == this.shop && null != this.shopDao){
            InternalReturnObject<Shop> shopById = this.shopDao.getShopById(this.shopId);
            if(shopById!=null){
                this.shop=shopById.getData();
            }
        }
        return this.shop;
    }

    @Setter
    @Getter
    private Long templateId;

    @JsonIgnore
    private Template template;


    public Template getTemplate() {
        if (null == this.shopId){
            return null;
        }

        if (null == this.template && null != this.shopDao){
            InternalReturnObject<Template> templateById = this.shopDao.getTemplateById(this.shopId, this.templateId);
            if(templateById!=null){
                this.template=templateById.getData();
            }
        }
        return this.template;
    }

    @Getter
    @Setter
    private Integer freeThreshold;
}
