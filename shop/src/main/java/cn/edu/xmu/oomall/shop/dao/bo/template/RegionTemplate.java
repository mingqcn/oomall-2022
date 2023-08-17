package cn.edu.xmu.oomall.shop.dao.bo.template;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.dao.bo.divide.DivideStrategy;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import cn.edu.xmu.oomall.shop.dao.template.TemplateDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 运费模板的父类
 */
@ToString(callSuper = true)
@NoArgsConstructor
public abstract class RegionTemplate extends OOMallObject implements Cloneable, Serializable {

    /**
     * 包裹的件数上限
     */
    @Setter
    @Getter
    protected Integer upperLimit;

    /**
     * 续重或续件计算单位 克或个
     */
    @Setter
    @Getter
    protected Integer unit;

    @Setter
    @Getter
    @JsonIgnore
    protected Long regionId;

    @Setter
    @Getter
    @JsonIgnore
    protected Long templateId;

    @Setter
    @JsonIgnore
    @ToString.Exclude
    protected FreightDao freightDao;

    /**
     * 配送地区
     */
    @JsonIgnore
    @ToString.Exclude
    protected Region region;

    public Region getRegion(){
        if (null != this.regionId && null == this.region) {
            InternalReturnObject<Region> ret=this.freightDao.findRegionById(this.regionId);
            if (ReturnNo.OK == ReturnNo.getByCode(ret.getErrno())){
                this.region = ret.getData();
            }
        }
        return this.region;
    }

    @Setter
    @JsonIgnore
    protected ObjectId objectId;

    public String getObjectId(){
        return objectId.toString();
    }

    public void setObjectId(String objectId){
        this.objectId = new ObjectId(objectId);
    }

    @JsonIgnore
    protected DivideStrategy strategy;

    public void setStrategy(DivideStrategy strategy){
        this.strategy = strategy;
    }
    /**
     * 计算包裹运费
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 15:56
     * @param productItems
     * @return
     */
    public Collection<TemplateResult> calculate(Collection<ProductItem> productItems){
        assert null != this.strategy;
        return this.strategy.divide(this, productItems).stream().map(pack ->{
            Long fee = cacuFreight(pack);
            return new TemplateResult(fee, pack);
        }).collect(Collectors.toList());
    }

    /**
     * 模板类名
     */
    @Getter
    @Setter
    protected String templateDao;

    public TemplateType gotType(){
        assert this.templateDao != null;

        return TemplateDao.TYPE.get(this.templateDao);
    }

    /**
     * 根据包裹里的商品计算运费
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 15:54
     * @param pack
     * @return
     */
    public abstract Long cacuFreight(Collection<ProductItem> pack);

    /**
     * 用不同的regionId克隆模板
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 14:48
     * @param regionId
     * @return
     * @throws CloneNotSupportedException
     */
    public RegionTemplate cloneWithRegion(Long regionId) throws CloneNotSupportedException {
        RegionTemplate template = (RegionTemplate) super.clone();
        template.setRegionId(regionId);
        return template;
    }
}
