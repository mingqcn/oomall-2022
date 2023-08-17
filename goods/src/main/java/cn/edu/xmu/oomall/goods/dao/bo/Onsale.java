//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.bo;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.dao.activity.ActivityDao;
import cn.edu.xmu.oomall.goods.dao.openfeign.ShopDao;
import cn.edu.xmu.oomall.goods.service.dto.IdNameDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Onsale extends OOMallObject implements Serializable {
    @ToString.Exclude
    @JsonIgnore
    private final static Logger logger = LoggerFactory.getLogger(Onsale.class);

    @Builder
    public Onsale(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Long price, LocalDateTime beginTime, LocalDateTime endTime,
                  Integer quantity, Byte invalid, Integer maxQuantity, Long shopId, Long productId, List<Activity> actList, ActivityDao activityDao, Byte type) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.price = price;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.quantity = quantity;
        this.invalid = invalid;
        this.maxQuantity = maxQuantity;
        this.shopId = shopId;
        this.productId = productId;
        this.actList = actList;
        this.activityDao = activityDao;
        this.type = type;
    }

    /**
     * 正常
     */
    @ToString.Exclude
    @JsonIgnore
    public static final Byte NORMAL = 0;
    /**
     * 秒杀
     */
    @ToString.Exclude
    @JsonIgnore
    public static final Byte SECONDKILL = 1;
    /**
     * 团购
     */
    @ToString.Exclude
    @JsonIgnore
    public static final Byte GROUPON = 2;
    /**
     * 预售
     */
    @ToString.Exclude
    @JsonIgnore
    public static final Byte ADVSALE = 3;



    @Setter
    @Getter
    private Long price;

    @Setter
    @Getter
    private LocalDateTime beginTime;

    @Setter
    @Getter
    private LocalDateTime endTime;

    @Setter
    @Getter
    private Integer quantity;

    @Setter
    @Getter
    private Byte invalid;

    @Setter
    @Getter
    private Integer maxQuantity;

    @Setter
    @Getter
    private Long shopId;

    @Setter
    @JsonIgnore
    private ShopDao shopDao;

    @JsonIgnore
    private Shop shop;

    public Shop getShop(){
        if (null == this.shop && null != this.shopDao){
            InternalReturnObject<Shop> ret = this.shopDao.getShopById(this.shopId);
            logger.debug("getShop: ret ={}", ret);
            if (ReturnNo.OK == ReturnNo.getByCode(ret.getErrno())){
                this.shop = ret.getData();
            }
        }
        return this.shop;
    }

    @Setter
    @Getter
    private Long productId;

    @Setter
    @JsonIgnore
    private ProductDao productDao;

    @JsonIgnore
    @ToString.Exclude
    private Product product;

    public Product getProduct(){
        if (null == this.product && null != this.productDao){
            this.product = this.productDao.findProductById(this.productId);
        }
        return this.product;
    }


    @JsonIgnore
    private List<Activity> actList;

    @Setter
    @JsonIgnore
    private ActivityDao activityDao;

    public List<Activity> getActList(){
        if (null == this.actList && null != this.activityDao){
            this.actList = this.activityDao.retrieveByOnsaleId(this.id);
            logger.debug("getActList: actList = {}", actList);
        }
        logger.debug("getActList: actList = {}", this.actList);
        return this.actList;

    }

    @Setter
    private Byte type;

    public Byte getType(){
        if (null  != this.getActList()) {
            List<Activity> acts = this.getActList().stream().filter(act -> act instanceof AdvanceSaleAct || act instanceof GrouponAct).limit(1).collect(Collectors.toList());
            if (acts.size() > 0) {
                if (acts.get(0) instanceof AdvanceSaleAct) {
                    return ADVSALE;
                } else {
                    return GROUPON;
                }
            }
        }
        return this.type;
    }
}
