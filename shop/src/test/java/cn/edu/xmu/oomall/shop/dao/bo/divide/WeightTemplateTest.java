//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.divide;

import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.dao.bo.template.TemplateResult;
import cn.edu.xmu.oomall.shop.dao.bo.template.WeightTemplate;
import cn.edu.xmu.oomall.shop.mapper.po.WeightThresholdPo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WeightTemplateTest {

    @Test
    public void toJson(){
        WeightTemplate template = new WeightTemplate(){
            {
                setId(1L);
                setUnit(1);
                setRegionId(112L);
                setFirstWeight(1);
                setFirstWeightPrice(100L);
                setUpperLimit(100);
                setCreatorId(1L);
                setCreatorName("admin");
                List<WeightThresholdPo> threshods = new ArrayList<>(){
                    {
                        add(new WeightThresholdPo(100, 100L));
                        add(new WeightThresholdPo(10, 10L));
                        add(new WeightThresholdPo(200, 200L));
                    }
                };
                setThresholds(threshods);
            }
        };
        assertEquals("{\"id\":1,\"creatorId\":1,\"creatorName\":\"admin\",\"upperLimit\":100,\"unit\":1,\"firstWeight\":1,\"firstWeightPrice\":100,\"thresholds\":[{\"below\":10,\"price\":10},{\"below\":100,\"price\":100},{\"below\":200,\"price\":200}]}",JacksonUtil.toJson(template));
    }

    @Test
    public void cacuFreight(){
        WeightTemplate template = new WeightTemplate() {
            {
                setId(1L);
                setUnit(2);
                setRegionId(112L);
                setFirstWeight(3);
                setFirstWeightPrice(5L);
                setUpperLimit(20);
                setCreatorId(1L);
                setCreatorName("admin");
                List<WeightThresholdPo> threshods = new ArrayList<>() {
                    {
                        add(new WeightThresholdPo(7, 10L));
                        add(new WeightThresholdPo(13, 15L));
                        add(new WeightThresholdPo(20, 20L));
                    }
                };
                setThresholds(threshods);
                setTemplateDao("weightTemplateDao");
            }
        };

        PackAlgorithm algorithm = new SimpleAlgorithm();
        DivideStrategy strategy = new MaxDivideStrategy(algorithm);
        List<ProductItem> items = new ArrayList<>(){
            {
                add(new ProductItem(1L,1L,100L,1,1L,1));
                add(new ProductItem(2L,2L,100L,2,1L,2));
                add(new ProductItem(3L,3L,100L,3,1L,3));
                add(new ProductItem(4L,4L,100L,4,1L,4));
                add(new ProductItem(5L,5L,100L,5,1L,3));
                add(new ProductItem(6L,6L,100L,6,1L,2));
                add(new ProductItem(7L,7L,100L,7,1L,1));
            }
        };

        template.setStrategy(strategy);
        Collection<TemplateResult> results = template.calculate(items);
        assertNotNull(results);
        results.stream().forEach(result ->{
                    assertTrue(result.getPack().stream().map(item -> item.getWeight() * item.getQuantity() ).reduce((x,y) -> x+y).get() <= 20);
                }
        );
        assertEquals(64, results.stream().map(result -> result.getPack().stream().map(item -> item.getWeight() * item.getQuantity()).reduce((x,y)->x + y).get()).reduce((x,y) -> x + y).get());
        assertEquals(10, results.stream().map(result -> result.getPack().size()).reduce((x,y) -> x + y).get());
        assertEquals(4, results.size());
        assertEquals(420, results.stream().map(result -> result.getFee()).reduce((x,y) -> x + y).get());


    }
}
