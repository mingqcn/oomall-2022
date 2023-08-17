//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.goods.dao.CategoryDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@ToString(callSuper = true)
public class ProductDraft extends OOMallObject implements Serializable {

    @ToString.Exclude
    @JsonIgnore
    private  final static Logger logger = LoggerFactory.getLogger(Product.class);

    @ToString.Exclude
    @JsonIgnore
    private final static Long NO_RELATE_PRODUCT = 0L;

    @Setter
    @JsonIgnore
    private ProductDao productDao;

    @Setter
    @JsonIgnore
    private CategoryDao categoryDao;

    @Setter
    @JsonIgnore
    private ShopDao shopDao;

    @Builder
    public ProductDraft(ProductDao productDao, CategoryDao categoryDao, ShopDao shopDao, Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, String name, Long originalPrice, String originPlace, Long shopId, Long categoryId, Long productId) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.name = name;
        this.originalPrice = originalPrice;
        this.originPlace = originPlace;
        this.shopId = shopId;
        this.categoryId = categoryId;
        this.productId = productId;
        this.productDao=productDao;
        this.categoryDao=categoryDao;
        this.shopDao=shopDao;
    }

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Long originalPrice;

    @Getter
    @Setter
    private String originPlace;


    @Setter
    @Getter
    private Long shopId;

    @JsonIgnore
    @ToString.Exclude
    private Shop shop;

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
    private Long categoryId;

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
    @ToString.Exclude
    private Category category;

    @Setter
    @Getter
    private Long productId;

    @JsonIgnore
    @ToString.Exclude
    private Product product;

    public Product getProduct(){
        Product product = Product.builder().id(this.productId).name(this.name).shopId(this.shopId).categoryId(this.categoryId).originalPrice(this.originalPrice).originPlace(this.originPlace).build();
        this.product=product;
        return this.product;
    }

}
