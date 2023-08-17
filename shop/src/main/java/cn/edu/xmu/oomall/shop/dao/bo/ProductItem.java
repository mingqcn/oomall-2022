//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ProductItem implements Cloneable{

    private Long id;
    /**
     * 产品Id
     */
    private Long productId;
    /**
     * 单价
     */
    @JsonIgnore
    private Long price;
    /**
     * 单重
     */
    private Integer weight;
    /**
     * 免邮门槛
     */
    @JsonIgnore
    private Long freeShipThreshold;
    /**
     * 数量
     */
    private Integer quantity;

    public ProductItem clone() throws CloneNotSupportedException {
        return (ProductItem) super.clone();
    }

    /**
     * clone一个新ProductItem，设置新的数量
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 10:29
     * @param quantity
     * @return
     * @throws CloneNotSupportedException
     */
    public ProductItem cloneWithQuantity(Integer quantity) throws CloneNotSupportedException {
        ProductItem ret = this.clone();
        ret.setQuantity(quantity);
        return ret;
    }
}
