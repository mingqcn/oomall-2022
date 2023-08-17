package cn.edu.xmu.oomall.shop.dao.bo.template;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.mapper.po.WeightThresholdPo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ziyi guo
 * @date 2021/11/16
 */
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeightTemplate extends RegionTemplate implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(WeightTemplate.class);

    /**
     * 首重
     */
    @Getter
    @Setter
    private Integer firstWeight;
    /**
     * 首重以下均为此费用
     */
    @Getter
    @Setter
    private Long firstWeightPrice;


    @Getter
    private List<WeightThresholdPo> thresholds;

    public void setThresholds(Collection<WeightThresholdPo> thresholds){
        this.thresholds = thresholds.stream().sorted(Comparator.comparing(WeightThresholdPo::getBelow)).collect(Collectors.toList());
    }

    @Override
    public Long cacuFreight(Collection<ProductItem> pack){
        Long result = this.firstWeightPrice;
        Integer weight = pack.stream().map(item -> item.getWeight() * item.getQuantity()).reduce((x,y) -> x+y).get();
        logger.debug("cacuFreight: weight = {}", weight);
        Integer prevThreshold = this.firstWeight;
        if (weight - this.firstWeight > 0) {
            for (WeightThresholdPo threshold : this.thresholds) {
                Integer upper = weight - threshold.getBelow() > 0 ? threshold.getBelow() : weight;
                //计算有多少个计价单位
                long num = (upper - prevThreshold) / this.unit;
                logger.debug("cacuFreight: upper = {}, prevThreshold = {},  {}",upper, prevThreshold, (upper - prevThreshold) % this.unit);
                if (0 != ((upper - prevThreshold) % this.unit)) {
                    num += 1;
                }
                result += threshold.getPrice() * num;
                prevThreshold = threshold.getBelow();
                logger.debug("cacuFreight: result = {}, threshold = {}, num = {}",result, threshold, num);
                if (upper == weight){
                    break;
                }
            }
        }
        return result;
    }
}
