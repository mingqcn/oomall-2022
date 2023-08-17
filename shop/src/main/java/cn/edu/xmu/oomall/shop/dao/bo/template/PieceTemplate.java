package cn.edu.xmu.oomall.shop.dao.bo.template;


import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;


@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PieceTemplate extends RegionTemplate implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PieceTemplate.class);
    /**
     * 首件数目
     */
    @Getter
    @Setter
    private Integer firstItems;

    /**
     * 起步费用
     */
    @Getter
    @Setter
    private Long firstPrice;

    /**
     * 续件
     */
    @Getter
    @Setter
    private Integer additionalItems;

    /**
     * 每增加additionalItems件商品，增加多少费用,小于additionalItems也是同样价钱
     */
    @Getter
    @Setter
    private Long additionalPrice;

    @Override
    public Long cacuFreight(Collection<ProductItem> pack){
        Integer total = pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x+y).get();
        logger.debug("cacuFreight: total = {}, template = {}", total, this);
        Long result = this.firstPrice;
        Integer rest = total - this.firstItems;
        if (rest > 0) {
            result += (rest / this.additionalItems) * this.additionalPrice;
            if (0 != rest % this.additionalItems) {
                result += this.additionalPrice;
            }
            logger.debug("cacuFreight: result = {}", result);
        }
        return result;
    }
}
