//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.divide;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.dao.bo.template.PieceTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.Weight;
import cn.edu.xmu.oomall.shop.dao.bo.template.WeightTemplate;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MaxDivideStrategyTest {

    @Test
    public void divide1(){
        PackAlgorithm algorithm = new SimpleAlgorithm();
        DivideStrategy divideStrategy = new MaxDivideStrategy(algorithm);
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
        Collection<Collection<ProductItem>> packs = divideStrategy.divide(new WeightTemplate(){
            {
                setUpperLimit(10);
                setTemplateDao("weightTemplateDao");
            }
        }, items);
        assertNotNull(packs);
        packs.stream().forEach(pack ->{
                    assertTrue(pack.stream().map(item -> item.getWeight() * item.getQuantity() ).reduce((x,y) -> x+y).get() <= 10);
                }
        );
        assertEquals(64, packs.stream().map(pack -> pack.stream().map(item -> item.getWeight() * item.getQuantity()).reduce((x,y)->x + y).get()).reduce((x,y) -> x + y).get());
        assertEquals(12, packs.stream().map(pack -> pack.size()).reduce((x,y) -> x + y).get());
        assertEquals(8, packs.size());
    }

    @Test
    public void divide2(){
        PackAlgorithm algorithm = new SimpleAlgorithm();
        DivideStrategy divideStrategy = new MaxDivideStrategy(algorithm);
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
        Collection<Collection<ProductItem>> packs = divideStrategy.divide(new PieceTemplate(){
            {
                setUpperLimit(5);
                setTemplateDao("pieceTemplateDao");
            }
        }, items);
        assertNotNull(packs);
        packs.stream().forEach(pack ->{
                    assertTrue(pack.stream().map(item -> item.getQuantity() ).reduce((x,y) -> x+y).get() <= 5);
                }
        );
        assertEquals(16, packs.stream().map(pack -> pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get()).reduce((x,y) -> x + y).get());
        assertEquals(8, packs.stream().map(pack -> pack.size()).reduce((x,y) -> x + y).get());
        assertEquals(4, packs.size());
    }

}
