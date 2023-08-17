//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo.divide;

import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;

public class BackPackAlgorithmTest {

    @Test
    public void pack1(){
        /**
         * 本算法：装入第 1 2 3 4 个item，总重量为 10
         * 剩余：第 5 6 7 件商品
         * simple算法：装入第 1 2 3 4 个item，总重量为 10
         */
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
        Collection<Item> items2 = new ArrayList<>(items);

        int maxWeight = 10;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(7, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(10, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());

        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());

    }

    @Test
    public void pack2(){
        /**
         * 本算法：装入第 1 2 3 个item，总重量为 10
         * 剩余：第 4 5 6 7 件商品
         * simple算法：装入第 1 2 3 个item，总重量为 10
         */
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
        Collection<Item> items2 = new ArrayList<>(items);
        int maxWeight = 10;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(7, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(10, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());

    }

    @Test
    public void pack3(){
        /**
         * 本算法：装入第 1 2 6 个item，总重量为 10
         * 剩余：第 3 4 5 7 件商品
         * simple算法：装入第 1 2 3 个item，总重量为 9
         */
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
        Collection<Item> items2 = new ArrayList<>(items);
        int maxWeight = 10;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(7, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity() ).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(10, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());

    }

    @Test
    public void pack4(){
        /**
         * 本算法：装入第 1 5 个item，总重量为 10
         * 剩余：第 2 3 4 6 7 件商品
         * simple算法：装入第 1 2 3 6 个item，总重量为 8
         */
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
        Collection<Item> items2 = new ArrayList<>(items);
        int maxWeight = 10;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(8, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(10, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());

    }

    @Test
    public void pack5(){
        /**
         * 本算法：装入第 1 4 个item，总重量为 5
         * 剩余：第 2 3 5 6 件商品
         * simple算法：装入第 1 2 6 个item，总重量为 4
         */
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(1L, 1L, 1));
                add(new Item(2L, 2L, 2));
                add(new Item(3L, 3L, 3));
                add(new Item(4L, 4L, 4));
                add(new Item(5L, 5L, 3));
                add(new Item(6L, 6L, 1));
            }
        };
        Collection<Item> items2 = new ArrayList<>(items);
        int maxWeight = 5;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        System.out.println(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(6, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(5, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());

    }

    @Test
    public void pack6(){
        /**
         * 本算法：装入第 1 2 3 4 5 个item，总重量为 5
         * 剩余：第 6 7 8 9 10 件商品
         * simple算法：装入第 1 2 3 4 5 个item，总重量为 5
         */
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(1L, 1L, 1));
                add(new Item(2L, 2L, 1));
                add(new Item(3L, 3L, 1));
                add(new Item(4L, 4L, 1));
                add(new Item(5L, 5L, 1));
                add(new Item(6L, 6L, 4));
                add(new Item(7L, 7L, 4));
                add(new Item(8L, 8L, 2));
                add(new Item(9L, 9L, 3));
                add(new Item(10L, 10L, 2));
            }
        };
        Collection<Item> items2 = new ArrayList<>(items);
        int maxWeight = 5;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        System.out.println(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(10, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(5, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());

    }

    @Test
    public void pack7(){
        /**
         * 本算法：装入第 2 3 5 个item，总重量为 11
         * 剩余：第 1 4 6 件商品
         * simple算法：装入第 2 3 5 个item，总重量为 11
         */
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(1L, 1L, 19));
                add(new Item(2L, 2L, 9));
                add(new Item(3L, 3L, 1));
                add(new Item(4L, 4L, 7));
                add(new Item(5L, 5L, 1));
                add(new Item(6L, 6L, 5));
            }
        };
        Collection<Item> items2 = new ArrayList<>(items);
        int maxWeight = 11;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        System.out.println(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(6, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(11, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());

    }

    @Test
    public void pack8(){
        /**
         * 本算法：装入第 2 3 4 个item，总重量为 9
         * 剩余：第 1 5 6 件商品
         * simple算法：装入第 2 3 4 个item，总重量为 9
         */
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(1L, 1L, 19));
                add(new Item(2L, 2L, 3));
                add(new Item(2L, 2L, 3));
                add(new Item(2L, 2L, 3));
                add(new Item(2L, 2L, 3));
                add(new Item(2L, 2L, 3));
            }
        };
        Collection<Item> items2 = new ArrayList<>(items);
        int maxWeight = 11;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        System.out.println(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(6, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(9, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());

    }


    @Test
    public void pack9() {
        /**
         * 本算法：无法装入
         * 剩余：所有商品
         *
         */
        Collection<Item> items = new ArrayList<>() {
            {
                add(new Item(1L, 1L, 19));
                add(new Item(2L, 2L, 3));
                add(new Item(2L, 2L, 3));
                add(new Item(2L, 2L, 3));
                add(new Item(2L, 2L, 3));
                add(new Item(2L, 2L, 3));
            }
        };
        int maxWeight = 2;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        assertEquals(0, pack.size());
        assertEquals(6, items.size());
        //无需对比，因为是特殊情况

    }

    @Test
    public void pack10() {
        /**
         * 本算法：无法装入
         * 剩余：没有商品
         *
         */
        Collection<Item> items = new ArrayList<>();
        int maxWeight = 2;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        assertEquals(0, pack.size());
        assertEquals(0, items.size());
        //无需对比，因为是特殊情况

    }

    @Test
    public void pack11(){
        /**
         * 本算法：装入第 5 6 7 8 9 10 个item，总重量为 16
         * 剩余：第 1 2 3 4 件商品
         * simple算法：装入第 1 2 3 4 5 6 7 8 个item，总重量为 15
         */
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(1L, 1L, 1));
                add(new Item(2L, 2L, 1));
                add(new Item(3L, 3L, 1));
                add(new Item(4L, 4L, 1));
                add(new Item(5L, 5L, 1));
                add(new Item(6L, 6L, 4));
                add(new Item(7L, 7L, 4));
                add(new Item(8L, 8L, 2));
                add(new Item(9L, 9L, 3));
                add(new Item(10L, 10L, 2));
            }
        };
        Collection<Item> items2 = new ArrayList<>(items);
        int maxWeight = 16;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        System.out.println(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(10, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(16, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());

    }

    @Test
    public void pack12(){
        /**
         * 本算法：装入第 6 7 9 11 12 13 个item，总重量为 30
         * 剩余：第 1 2 3 4 5 8 14 15 件商品
         * simple算法：装入第 1 2 3 4 5 6 7 8 9 10 11 14 15 个item，总重量为 26
         */
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(1L, 1L, 1));
                add(new Item(2L, 2L, 1));
                add(new Item(3L, 3L, 1));
                add(new Item(4L, 4L, 1));
                add(new Item(5L, 5L, 1));
                add(new Item(6L, 6L, 4));
                add(new Item(7L, 7L, 4));
                add(new Item(8L, 8L, 2));
                add(new Item(9L, 9L, 3));
                add(new Item(10L, 10L, 2));
                add(new Item(11L, 11L, 4));
                add(new Item(12L, 12L, 7));
                add(new Item(13L, 13L, 8));
                add(new Item(1L, 1L, 1));
                add(new Item(1L, 1L, 1));
            }
        };
        Collection<Item> items2 = new ArrayList<>(items);
        int maxWeight = 30;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        System.out.println(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(15, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(30, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());

    }

    @Test
    public void pack13(){
        /**
         * 本算法：装入第 2 3 4 5 个item，总重量为 11
         * 剩余：第 1 6 件商品
         * simple算法：装入第 2 3 4 5 个item，总重量为 11
         */
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(1L, 1L, 19));
                add(new Item(2L, 2L, 3));
                add(new Item(2L, 2L, 3));
                add(new Item(3L, 3L, 2));
                add(new Item(2L, 2L, 3));
                add(new Item(2L, 2L, 3));
            }
        };
        Collection<Item> items2 = new ArrayList<>(items);
        int maxWeight = 11;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        System.out.println(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(6, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(11, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());

    }

    @Test
    public void pack14(){
        /**
         * 本算法：装入第 6 个item，总重量为 13
         * 剩余：第 1 2 3 4 5 件商品
         * simple算法：装入第 2 5 个item，总重量为 12
         */
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(1L, 1L, 19));
                add(new Item(2L, 2L, 11));
                add(new Item(3L, 3L, 31));
                add(new Item(4L, 4L, 10));
                add(new Item(5L, 5L, 1));
                add(new Item(6L, 6L, 13));
            }
        };
        Collection<Item> items2 = new ArrayList<>(items);
        int maxWeight = 13;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        System.out.println(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(6, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(13, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());
    }

    @Test
    public void pack15(){
        /**
         * 本算法：装入第 2 3 4 5 个item，总重量为 11
         * 剩余：第 1 6 件商品
         * simple算法：装入第 2 3 4 5 个item，总重量为 11
         */
        Collection<Item> items = new ArrayList<>(){
            {
                add(new Item(1L, 1L, 19));
                add(new Item(2L, 2L, 3));
                add(new Item(2L, 2L, 3));
                add(new Item(2L, 2L, 3));
                add(new Item(3L, 3L, 2));
                add(new Item(2L, 2L, 3));
            }
        };
        Collection<Item> items2 = new ArrayList<>(items);
        int maxWeight = 11;
        Collection<Item> pack = new BackPackAlgorithm().pack(items, maxWeight);
        assertNotNull(pack);
        System.out.println(pack);
        //分别检测不丢件 重量不超过上限 重量最终值
        assertEquals(6, items.size() + pack.stream().map(item -> item.getQuantity()).reduce((x,y)->x + y).get());
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get() <= maxWeight);
        assertEquals(11, pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y)->x + y).get());
        //对比simpleAlgorithm
        Collection<Item> pack2 = new SimpleAlgorithm().pack(items2, maxWeight);
        assertNotNull(pack2);
        assertTrue(pack.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get()
                >= pack2.stream().map(item -> item.getCount() * item.getQuantity()).reduce((x,y) -> x+y).get());

    }
}
