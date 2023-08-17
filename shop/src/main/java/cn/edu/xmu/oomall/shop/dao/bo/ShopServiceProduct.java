//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.shop.dao.ShopDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Product;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString(callSuper = true)
@NoArgsConstructor
public class ShopServiceProduct extends OOMallObject implements Serializable {
    /**
     * 无效
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte INVALID = 1;
    /**
     * 有效
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte VALID = 0;

    @Setter
    @Getter
    private Long id;

    @Setter
    @Getter
    private Long productId;

    @ToString.Exclude
    @JsonIgnore
    private Product product;

    @Setter
    @Getter
    private Long maintainerId;

    @ToString.Exclude
    @JsonIgnore
    private Shop shop;

    @Setter
    @Getter
    private Long regionId;

    @ToString.Exclude
    @JsonIgnore
    private Region region;

    @Setter
    @JsonIgnore
    @ToString.Exclude
    private ShopDao shopDao;

    public Shop getShop() {
        if (null != this.maintainerId && null == this.shop) {
            this.shop = this.shopDao.findById(this.maintainerId).orElse(null);
        }
        return this.shop;
    }

    @Setter
    @JsonIgnore
    @ToString.Exclude
    private GoodsDao goodsDao;

    public Product getProduct() {
        if (null != this.productId && null == this.product) {
            InternalReturnObject<Product> ret=this.goodsDao.retrieveProductById(this.productId);
            if (ReturnNo.OK == ReturnNo.getByCode(ret.getErrno())){
                this.product = ret.getData();
            }
        }
        return this.product;
    }

    @Setter
    @JsonIgnore
    @ToString.Exclude
    private FreightDao freightDao;

    public Region getRegion() {
        if (null != this.regionId && null == this.region) {
            InternalReturnObject<Region> ret=this.freightDao.findRegionById(this.regionId);
            if (ReturnNo.OK == ReturnNo.getByCode(ret.getErrno())){
                this.region = ret.getData();
            }
        }
        return this.region;
    }


    /**
     * 开始时间
     */
    @Setter
    @Getter
    private LocalDateTime beginTime;

    /**
     * 终止时间
     */
    @Setter
    @Getter
    private LocalDateTime endTime;

    /**
     * 有效 0 无效 1
     */
    @Setter
    @Getter
    private Byte invalid;

    /**
     * 优先级 0 最高
     */
    @Setter
    @Getter
    private Integer priority;

    public ShopServiceProduct(LocalDateTime beginTime, LocalDateTime endTime, Byte invalid, Integer priority, Long productId, Long maintainerId, Long regionId){
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.invalid = invalid;
        this.priority = priority;
        this.productId = productId;
        this.maintainerId = maintainerId;
        this.regionId = regionId;
    }
}
