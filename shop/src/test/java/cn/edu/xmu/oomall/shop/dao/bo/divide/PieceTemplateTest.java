//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.divide;

import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.dao.bo.template.PieceTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.TemplateResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PieceTemplateTest {

    @Test
    public void toJson(){
        PieceTemplate template = new PieceTemplate(){
            {
                setId(1L);
                setUnit(1);
                setRegionId(112L);
                setFirstItems(1);
                setFirstPrice(10L);
                setAdditionalItems(3);
                setAdditionalPrice(15L);
                setUpperLimit(100);
                setCreatorId(1L);
                setCreatorName("admin");
            }
        };
        assertEquals("{\"id\":1,\"creatorId\":1,\"creatorName\":\"admin\",\"upperLimit\":100,\"unit\":1,\"firstItems\":1,\"firstPrice\":10,\"additionalItems\":3,\"additionalPrice\":15}",JacksonUtil.toJson(template));
    }

    @Test
    public void cacuFreight(){
        PieceTemplate template = new PieceTemplate(){
            {
                setId(1L);
                setUnit(1);
                setRegionId(112L);
                setFirstItems(1);
                setFirstPrice(10L);
                setAdditionalItems(3);
                setAdditionalPrice(15L);
                setUpperLimit(5);
                setCreatorId(1L);
                setCreatorName("admin");
                setTemplateDao("pieceTemplateDao");
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
                    assertTrue(result.getPack().stream().map(item -> item.getQuantity() ).reduce((x,y) -> x+y).get() <= 5);
                }
        );
        System.out.println(results);
        assertEquals(16, results.stream().map(result -> result.getPack().stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get()).reduce((x,y) -> x + y).get());
        assertEquals(8, results.stream().map(result -> result.getPack().size()).reduce((x,y) -> x + y).get());
        assertEquals(4, results.size());
        assertEquals(130, results.stream().map(result -> result.getFee()).reduce((x,y) -> x + y).get());


    }
}
