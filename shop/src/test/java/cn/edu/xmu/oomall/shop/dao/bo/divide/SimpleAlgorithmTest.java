//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.divide;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleAlgorithmTest {

    @Test
    public void pack1(){
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(1L, 1L, 1));
                add(new Item(2L, 2L, 2));
                add(new Item(3L, 3L, 3));
                add(new Item(4L, 4L, 4));
                add(new Item(5L, 5L, 5));
                add(new Item(6L, 6L, 6));
                add(new Item(7L, 7L, 7));
            }
        };
        Collection<Item> pack = new SimpleAlgorithm().pack(items, 10);
        assertNotNull(pack);
        assertEquals(4, pack.size());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= 10);
        assertEquals(10, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
    }

    @Test
    public void pack2(){
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(4L, 4L, 4));
                add(new Item(1L, 1L, 1));
                add(new Item(5L, 5L, 5));
                add(new Item(3L, 3L, 3));
                add(new Item(6L, 6L, 6));
                add(new Item(2L, 2L, 2));
                add(new Item(7L, 7L, 7));
            }
        };
        Collection<Item> pack = new SimpleAlgorithm().pack(items, 10);
        assertNotNull(pack);
        assertEquals(3, pack.size());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= 10);

        assertEquals(10, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
    }

    @Test
    public void pack3(){
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(4L, 4L, 4));
                add(new Item(4L, 4L, 4));
                add(new Item(1L, 1L, 1));
                add(new Item(5L, 5L, 5));
                add(new Item(6L, 6L, 6));
                add(new Item(2L, 2L, 2));
                add(new Item(7L, 7L, 7));
            }
        };
        Collection<Item> pack = new SimpleAlgorithm().pack(items, 10);
        assertNotNull(pack);
        assertEquals(2, pack.size());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity() ).reduce((x,y) -> x+y).get() <= 10);
        assertEquals(9, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
    }

    @Test
    public void pack4(){
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(4L, 4L, 4));
                add(new Item(1L, 1L, 1));
                add(new Item(1L, 1L, 1));
                add(new Item(5L, 5L, 5));
                add(new Item(6L, 6L, 6));
                add(new Item(2L, 2L, 2));
                add(new Item(7L, 7L, 7));
                add(new Item(11L, 11L, 11));
            }
        };
        Collection<Item> pack = new SimpleAlgorithm().pack(items, 10);
        assertNotNull(pack);
        assertEquals(3, pack.size());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= 10);

        assertEquals(8, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
    }

    @Test
    public void pack5(){
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(1L, 1L, 1));
                add(new Item(2L, 2L, 2));
                add(new Item(3L, 3L, 3));
                add(new Item(4L, 4L, 4));
                add(new Item(5L, 5L, 3));
                add(new Item(6L, 6L, 2));
                add(new Item(7L, 7L, 1));
            }
        };
        Collection<Item> pack = new SimpleAlgorithm().pack(items, 5);
        assertNotNull(pack);
        System.out.println(pack);
        assertEquals(3, pack.size());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= 10);
        assertEquals(5, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
    }
}
