package cn.edu.xmu.oomall.shop.dao.bo.divide;
import cn.edu.xmu.oomall.shop.dao.bo.ProductItem;
import cn.edu.xmu.oomall.shop.dao.bo.template.PieceTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.WeightTemplate;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
public class GreedyAverageDivideStrategyTest {
    @Test
    public void pack1(){
        PackAlgorithm algorithm = new SimpleAlgorithm();
        DivideStrategy divideStrategy = new GreedyAverageDivideStrategy(null);

        DivideStrategy average=new AverageDivideStrategy(algorithm);
        DivideStrategy max=new MaxDivideStrategy((algorithm));
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
                setUpperLimit(12);
                setTemplateDao("weightTemplateDao");
            }
        }, items);
        int baseline=0;
        for(Collection<ProductItem> pack:packs){
            baseline+=pack.stream().mapToInt(ProductItem::getWeight).sum();
        }
        baseline=(baseline% packs.size()==0)?baseline/ packs.size():(baseline/ packs.size()+1);
        assertNotNull(packs);
        packs.stream().forEach(pack ->{
                    assertTrue(pack.stream().map(item -> item.getWeight() * item.getQuantity() ).reduce((x,y) -> x+y).get() <= 12);
                }
        );
        assertEquals(64, packs.stream().map(pack -> pack.stream().map(item -> item.getWeight() * item.getQuantity()).reduce((x,y)->x + y).get()).reduce((x,y) -> x + y).get());
        assertEquals(16, packs.stream().map(pack -> pack.size()).reduce((x,y) -> x + y).get());
        assertEquals(6, packs.size());

    }


    @Test
    public void pack2(){
        PackAlgorithm algorithm = new SimpleAlgorithm();
        DivideStrategy divideStrategy = new GreedyAverageDivideStrategy(null);

        DivideStrategy average=new AverageDivideStrategy(algorithm);
        DivideStrategy max=new MaxDivideStrategy((algorithm));
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
                setUpperLimit(6);
                setTemplateDao("pieceTemplateDao");
            }
        }, items);
        int baseline=0;
        for(Collection<ProductItem> pack:packs){
            baseline+=pack.stream().mapToInt(ProductItem::getWeight).sum();
        }
        baseline=(baseline% packs.size()==0)?baseline/ packs.size():(baseline/ packs.size()+1);
        assertNotNull(packs);
        packs.stream().forEach(pack ->{
                    assertTrue(pack.stream().map(item -> item.getWeight() * item.getQuantity() ).reduce((x,y) -> x+y).get() <= 26);
                }
        );
        assertEquals(64, packs.stream().map(pack -> pack.stream().map(item -> item.getWeight() * item.getQuantity()).reduce((x,y)->x + y).get()).reduce((x,y) -> x + y).get());
        assertEquals(16, packs.stream().map(pack -> pack.size()).reduce((x,y) -> x + y).get());
        assertEquals(3, packs.size());

    }


    /*和maxDivideStrategy比较，限制最大重量*/
    @Test
    public void pack3(){
        PackAlgorithm algorithm = new SimpleAlgorithm();
        DivideStrategy divideStrategy = new GreedyAverageDivideStrategy(null);

        DivideStrategy max=new MaxDivideStrategy((algorithm));
        Random random=new Random();
        int greedy_win=0;
        int max_win=0;
        for(int i=0;i<10000;i++){
            List<ProductItem> items = new ArrayList<>(){
                {
                    add(new ProductItem(1L,1L,100L,random.nextInt(3),1L,random.nextInt(10)));
                    add(new ProductItem(2L,2L,100L,random.nextInt(3)+3,1L,random.nextInt(10)));
                    add(new ProductItem(3L,3L,100L,random.nextInt(3)+6,1L,random.nextInt(10)));
                    add(new ProductItem(4L,4L,100L,random.nextInt(3)+9,1L,random.nextInt(10)));
                    add(new ProductItem(5L,5L,100L,random.nextInt(3)+12,1L,random.nextInt(10)));
                    add(new ProductItem(6L,6L,100L,random.nextInt(3)+15,1L,random.nextInt(10)));
                    add(new ProductItem(7L,7L,100L,random.nextInt(3)+18,1L,random.nextInt(10)));
                }
            };
            Collection<Collection<ProductItem>> packs = divideStrategy.divide(new WeightTemplate(){
                {
                    setUpperLimit(20);
                    setTemplateDao("weightTemplateDao");
                }
            }, items);
            int baseline=0;
            for(Collection<ProductItem> pack:packs){
                baseline+=pack.stream().mapToInt(ProductItem::getWeight).sum();
            }
            baseline=(baseline% packs.size()==0)?baseline/ packs.size():(baseline/ packs.size()+1);
            assertNotNull(packs);
            //和MaxDivide进行比较

            Collection<Collection<ProductItem>> packs2 = max.divide(new WeightTemplate(){
                {
                    setUpperLimit(20);
                    setTemplateDao("weightTemplateDao");
                }
            }, items);
            int loss_greedyaverage=0;
            int loss_average=0;
            for(Collection<ProductItem> pack:packs){
                loss_greedyaverage+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            for(Collection<ProductItem> pack:packs2){
                loss_average+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            if(packs.size()< packs2.size()){
                greedy_win+=1;//我们的算法得到的包裹数小于其他算法
            }else if(packs.size()>packs2.size()){
                max_win+=1;
            }else{
                if(loss_greedyaverage<loss_average){
                    greedy_win+=1;//贪心重量算法获胜
                }else if(loss_average<loss_greedyaverage){
                    max_win+=1;//最大重量贪心获胜
                }
            }
        }
        //可能存在两种算法得到同样的loss
        System.out.println(greedy_win);
        System.out.println(max_win);
    }

    /*和maxDivideStrategy比较，限制最大包裹数*/
    @Test
    public void pack4(){
        PackAlgorithm algorithm = new SimpleAlgorithm();
        DivideStrategy divideStrategy = new GreedyAverageDivideStrategy(null);

        DivideStrategy max=new MaxDivideStrategy((algorithm));
        Random random=new Random();
        int greedy_win=0;
        int max_win=0;
        for(int i=0;i<10000;i++){
            List<ProductItem> items = new ArrayList<>(){
                {
                    add(new ProductItem(1L,1L,100L,random.nextInt(3),1L,random.nextInt(10)));
                    add(new ProductItem(2L,2L,100L,random.nextInt(3)+3,1L,random.nextInt(10)));
                    add(new ProductItem(3L,3L,100L,random.nextInt(3)+6,1L,random.nextInt(10)));
                    add(new ProductItem(4L,4L,100L,random.nextInt(3)+9,1L,random.nextInt(10)));
                    add(new ProductItem(5L,5L,100L,random.nextInt(3)+12,1L,random.nextInt(10)));
                    add(new ProductItem(6L,6L,100L,random.nextInt(3)+15,1L,random.nextInt(10)));
                    add(new ProductItem(7L,7L,100L,random.nextInt(3)+18,1L,random.nextInt(10)));
                }
            };
            Collection<Collection<ProductItem>> packs = divideStrategy.divide(new PieceTemplate(){
                {
                    setUpperLimit(6);
                    setTemplateDao("pieceTemplateDao");
                }
            }, items);
            int baseline=0;
            for(Collection<ProductItem> pack:packs){
                baseline+=pack.stream().mapToInt(ProductItem::getWeight).sum();
            }
            baseline=(baseline% packs.size()==0)?baseline/ packs.size():(baseline/ packs.size()+1);
            assertNotNull(packs);
            //和MaxDivide进行比较

            Collection<Collection<ProductItem>> packs2 = max.divide(new PieceTemplate(){
                {
                    setUpperLimit(6);
                    setTemplateDao("pieceTemplateDao");
                }
            }, items);
            int loss_greedyaverage=0;
            int loss_average=0;
            for(Collection<ProductItem> pack:packs){
                loss_greedyaverage+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            for(Collection<ProductItem> pack:packs2){
                loss_average+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            if(packs.size()< packs2.size()){
                greedy_win+=1;//我们的算法得到的包裹数小于其他算法
            }else if(packs.size()>packs2.size()){
                max_win+=1;
            } else{
                if(loss_greedyaverage<loss_average){
                    greedy_win+=1;//贪心重量算法获胜
                }else if(loss_average<loss_greedyaverage){
                    max_win+=1;//最大重量贪心获胜
                }
            }
        }
        //可能存在两种算法得到同样的loss
        System.out.println(greedy_win);
        System.out.println(max_win);
    }

    /*和averageDivideStrategy比较*/
    @Test
    public void pack5(){
        PackAlgorithm algorithm = new SimpleAlgorithm();
        DivideStrategy divideStrategy = new GreedyAverageDivideStrategy(null);

        DivideStrategy average=new AverageDivideStrategy((algorithm));
        Random random=new Random();
        int greedy_win=0;
        int average_win=0;
        for(int i=0;i<10000;i++){
            List<ProductItem> items = new ArrayList<>(){
                {
                    add(new ProductItem(1L,1L,100L,random.nextInt(3),1L,random.nextInt(10)));
                    add(new ProductItem(2L,2L,100L,random.nextInt(3)+3,1L,random.nextInt(10)));
                    add(new ProductItem(3L,3L,100L,random.nextInt(3)+6,1L,random.nextInt(10)));
                    add(new ProductItem(4L,4L,100L,random.nextInt(3)+9,1L,random.nextInt(10)));
                    add(new ProductItem(5L,5L,100L,random.nextInt(3)+12,1L,random.nextInt(10)));
                    add(new ProductItem(6L,6L,100L,random.nextInt(3)+15,1L,random.nextInt(10)));
                    add(new ProductItem(7L,7L,100L,random.nextInt(3)+18,1L,random.nextInt(10)));
                }
            };
            Collection<Collection<ProductItem>> packs = divideStrategy.divide(new PieceTemplate(){
                {
                    setUpperLimit(6);
                    setTemplateDao("pieceTemplateDao");
                }
            }, items);
            int baseline=0;
            for(Collection<ProductItem> pack:packs){
                baseline+=pack.stream().mapToInt(ProductItem::getWeight).sum();
            }
            baseline=(baseline% packs.size()==0)?baseline/ packs.size():(baseline/ packs.size()+1);
            assertNotNull(packs);
            //和AverageDivide进行比较

            Collection<Collection<ProductItem>> packs2 = average.divide(new PieceTemplate(){
                {
                    setUpperLimit(6);
                    setTemplateDao("pieceTemplateDao");
                }
            }, items);
            int loss_greedyaverage=0;
            int loss_average=0;
            for(Collection<ProductItem> pack:packs){
                loss_greedyaverage+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            for(Collection<ProductItem> pack:packs2){
                loss_average+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            if(packs.size()< packs2.size()){
                greedy_win+=1;//我们的算法得到的包裹数小于其他算法
            }else{
                if(loss_greedyaverage<loss_average){
                    greedy_win+=1;//贪心重量算法获胜
                }else if(loss_average<loss_greedyaverage){
                    average_win+=1;//最大重量贪心获胜
                }
            }
        }
        //可能存在两种算法得到同样的loss
        System.out.println(greedy_win);
        System.out.println(average_win);
    }

    /*和averageDivideStrategy比较*/
    @Test
    public void pack6(){
        PackAlgorithm algorithm = new SimpleAlgorithm();
        DivideStrategy divideStrategy = new GreedyAverageDivideStrategy(null);

        DivideStrategy average=new AverageDivideStrategy((algorithm));
        Random random=new Random();
        int greedy_win=0;
        int average_win=0;
        for(int i=0;i<10000;i++){
            List<ProductItem> items = new ArrayList<>(){
                {
                    add(new ProductItem(1L,1L,100L,random.nextInt(3),1L,random.nextInt(10)));
                    add(new ProductItem(2L,2L,100L,random.nextInt(3)+3,1L,random.nextInt(10)));
                    add(new ProductItem(3L,3L,100L,random.nextInt(3)+6,1L,random.nextInt(10)));
                    add(new ProductItem(4L,4L,100L,random.nextInt(3)+9,1L,random.nextInt(10)));
                    add(new ProductItem(5L,5L,100L,random.nextInt(3)+12,1L,random.nextInt(10)));
                    add(new ProductItem(6L,6L,100L,random.nextInt(3)+15,1L,random.nextInt(10)));
                    add(new ProductItem(7L,7L,100L,random.nextInt(3)+18,1L,random.nextInt(10)));
                }
            };
            Collection<Collection<ProductItem>> packs = divideStrategy.divide(new WeightTemplate(){
                {
                    setUpperLimit(20);
                    setTemplateDao("weightTemplateDao");
                }
            }, items);
            int baseline=0;
            for(Collection<ProductItem> pack:packs){
                baseline+=pack.stream().mapToInt(ProductItem::getWeight).sum();
            }
            baseline=(baseline% packs.size()==0)?baseline/ packs.size():(baseline/ packs.size()+1);
            assertNotNull(packs);
            //和AverageDivide进行比较

            Collection<Collection<ProductItem>> packs2 = average.divide(new WeightTemplate(){
                {
                    setUpperLimit(20);
                    setTemplateDao("weightTemplateDao");
                }
            }, items);
            int loss_greedyaverage=0;
            int loss_average=0;
            for(Collection<ProductItem> pack:packs){
                loss_greedyaverage+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            for(Collection<ProductItem> pack:packs2){
                loss_average+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            if(packs.size()< packs2.size()){
                greedy_win+=1;//我们的算法得到的包裹数小于其他算法
            }else{
                if(loss_greedyaverage<loss_average){
                    greedy_win+=1;//贪心重量算法获胜
                }else if(loss_average<loss_greedyaverage){
                    average_win+=1;//最大重量贪心获胜
                }
            }
        }
        //可能存在两种算法得到同样的loss
        System.out.println(greedy_win);
        System.out.println(average_win);
    }

    /*和averageDivideStrategy比较,基于BackPackAlgorithm*/
    @Test
    public void pack7(){
        PackAlgorithm algorithm = new BackPackAlgorithm();
        DivideStrategy divideStrategy = new GreedyAverageDivideStrategy(null);

        DivideStrategy average=new AverageDivideStrategy((algorithm));
        Random random=new Random();
        int greedy_win=0;
        int average_win=0;
        for(int i=0;i<10000;i++){
            List<ProductItem> items = new ArrayList<>(){
                {
                    add(new ProductItem(1L,1L,100L,random.nextInt(3),1L,random.nextInt(10)));
                    add(new ProductItem(2L,2L,100L,random.nextInt(3)+3,1L,random.nextInt(10)));
                    add(new ProductItem(3L,3L,100L,random.nextInt(3)+6,1L,random.nextInt(10)));
                    add(new ProductItem(4L,4L,100L,random.nextInt(3)+9,1L,random.nextInt(10)));
                    add(new ProductItem(5L,5L,100L,random.nextInt(3)+12,1L,random.nextInt(10)));
                    add(new ProductItem(6L,6L,100L,random.nextInt(3)+15,1L,random.nextInt(10)));
                    add(new ProductItem(7L,7L,100L,random.nextInt(3)+18,1L,random.nextInt(10)));
                }
            };
            Collection<Collection<ProductItem>> packs = divideStrategy.divide(new WeightTemplate(){
                {
                    setUpperLimit(20);
                    setTemplateDao("weightTemplateDao");
                }
            }, items);
            int baseline=0;
            for(Collection<ProductItem> pack:packs){
                baseline+=pack.stream().mapToInt(ProductItem::getWeight).sum();
            }
            baseline=(baseline% packs.size()==0)?baseline/ packs.size():(baseline/ packs.size()+1);
            assertNotNull(packs);
            //和AverageDivide进行比较

            Collection<Collection<ProductItem>> packs2 = average.divide(new WeightTemplate(){
                {
                    setUpperLimit(20);
                    setTemplateDao("weightTemplateDao");
                }
            }, items);
            int loss_greedyaverage=0;
            int loss_average=0;
            for(Collection<ProductItem> pack:packs){
                loss_greedyaverage+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            for(Collection<ProductItem> pack:packs2){
                loss_average+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            if(packs.size()< packs2.size()){
                greedy_win+=1;//我们的算法得到的包裹数小于其他算法
            }else{
                if(loss_greedyaverage<loss_average){
                    greedy_win+=1;//贪心重量算法获胜
                }else if(loss_average<loss_greedyaverage){
                    average_win+=1;//最大重量贪心获胜
                }
            }
        }
        //可能存在两种算法得到同样的loss
        System.out.println(greedy_win);
        System.out.println(average_win);
    }

    /*和averageDivideStrategy比较,基于BackPackAlgorithm*/
    @Test
    public void pack8(){
        PackAlgorithm algorithm = new BackPackAlgorithm();
        DivideStrategy divideStrategy = new GreedyAverageDivideStrategy(null);

        DivideStrategy average=new AverageDivideStrategy((algorithm));
        Random random=new Random();
        int greedy_win=0;
        int average_win=0;
        for(int i=0;i<10000;i++){
            List<ProductItem> items = new ArrayList<>(){
                {
                    add(new ProductItem(1L,1L,100L,random.nextInt(3),1L,random.nextInt(10)));
                    add(new ProductItem(2L,2L,100L,random.nextInt(3)+3,1L,random.nextInt(10)));
                    add(new ProductItem(3L,3L,100L,random.nextInt(3)+6,1L,random.nextInt(10)));
                    add(new ProductItem(4L,4L,100L,random.nextInt(3)+9,1L,random.nextInt(10)));
                    add(new ProductItem(5L,5L,100L,random.nextInt(3)+12,1L,random.nextInt(10)));
                    add(new ProductItem(6L,6L,100L,random.nextInt(3)+15,1L,random.nextInt(10)));
                    add(new ProductItem(7L,7L,100L,random.nextInt(3)+18,1L,random.nextInt(10)));
                }
            };
            Collection<Collection<ProductItem>> packs = divideStrategy.divide(new PieceTemplate(){
                {
                    setUpperLimit(6);
                    setTemplateDao("pieceTemplateDao");
                }
            }, items);
            int baseline=0;
            for(Collection<ProductItem> pack:packs){
                baseline+=pack.stream().mapToInt(ProductItem::getWeight).sum();
            }
            baseline=(baseline% packs.size()==0)?baseline/ packs.size():(baseline/ packs.size()+1);
            assertNotNull(packs);
            //和AverageDivide进行比较

            Collection<Collection<ProductItem>> packs2 = average.divide(new PieceTemplate(){
                {
                    setUpperLimit(6);
                    setTemplateDao("pieceTemplateDao");
                }
            }, items);
            int loss_greedyaverage=0;
            int loss_average=0;
            for(Collection<ProductItem> pack:packs){
                loss_greedyaverage+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            for(Collection<ProductItem> pack:packs2){
                loss_average+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            if(packs.size()< packs2.size()){
                greedy_win+=1;//我们的算法得到的包裹数小于其他算法
            }else{
                if(loss_greedyaverage<loss_average){
                    greedy_win+=1;//贪心重量算法获胜
                }else if(loss_average<loss_greedyaverage){
                    average_win+=1;//最大重量贪心获胜
                }
            }
        }
        //可能存在两种算法得到同样的loss
        System.out.println(greedy_win);
        System.out.println(average_win);
    }

    /*和maxDivideStrategy比较，限制最大重量*/
    @Test
    public void pack9(){
        PackAlgorithm algorithm = new BackPackAlgorithm();
        DivideStrategy divideStrategy = new GreedyAverageDivideStrategy(null);

        DivideStrategy max=new MaxDivideStrategy((algorithm));
        Random random=new Random();
        int greedy_win=0;
        int max_win=0;
        for(int i=0;i<10000;i++){
            List<ProductItem> items = new ArrayList<>(){
                {
                    add(new ProductItem(1L,1L,100L,random.nextInt(3),1L,random.nextInt(10)));
                    add(new ProductItem(2L,2L,100L,random.nextInt(3)+3,1L,random.nextInt(10)));
                    add(new ProductItem(3L,3L,100L,random.nextInt(3)+6,1L,random.nextInt(10)));
                    add(new ProductItem(4L,4L,100L,random.nextInt(3)+9,1L,random.nextInt(10)));
                    add(new ProductItem(5L,5L,100L,random.nextInt(3)+12,1L,random.nextInt(10)));
                    add(new ProductItem(6L,6L,100L,random.nextInt(3)+15,1L,random.nextInt(10)));
                    add(new ProductItem(7L,7L,100L,random.nextInt(3)+18,1L,random.nextInt(10)));
                }
            };
            Collection<Collection<ProductItem>> packs = divideStrategy.divide(new WeightTemplate(){
                {
                    setUpperLimit(20);
                    setTemplateDao("weightTemplateDao");
                }
            }, items);
            int baseline=0;
            for(Collection<ProductItem> pack:packs){
                baseline+=pack.stream().mapToInt(ProductItem::getWeight).sum();
            }
            baseline=(baseline% packs.size()==0)?baseline/ packs.size():(baseline/ packs.size()+1);
            assertNotNull(packs);
            //和MaxDivide进行比较

            Collection<Collection<ProductItem>> packs2 = max.divide(new WeightTemplate(){
                {
                    setUpperLimit(20);
                    setTemplateDao("weightTemplateDao");
                }
            }, items);
            int loss_greedyaverage=0;
            int loss_average=0;
            for(Collection<ProductItem> pack:packs){
                loss_greedyaverage+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            for(Collection<ProductItem> pack:packs2){
                loss_average+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            if(packs.size()< packs2.size()){
                greedy_win+=1;//我们的算法得到的包裹数小于其他算法
            }else if(packs.size()>packs2.size()){
                max_win+=1;
            }else{
                if(loss_greedyaverage<loss_average){
                    greedy_win+=1;//贪心重量算法获胜
                }else if(loss_average<loss_greedyaverage){
                    max_win+=1;//最大重量贪心获胜
                }
            }
        }
        //可能存在两种算法得到同样的loss
        System.out.println(greedy_win);
        System.out.println(max_win);
    }

    /*和maxDivideStrategy比较，限制最大包裹数*/
    @Test
    public void pack10(){
        PackAlgorithm algorithm = new BackPackAlgorithm();
        DivideStrategy divideStrategy = new GreedyAverageDivideStrategy(null);

        DivideStrategy max=new MaxDivideStrategy((algorithm));
        Random random=new Random();
        int greedy_win=0;
        int max_win=0;
        for(int i=0;i<10000;i++){
            List<ProductItem> items = new ArrayList<>(){
                {
                    add(new ProductItem(1L,1L,100L,random.nextInt(3),1L,random.nextInt(10)));
                    add(new ProductItem(2L,2L,100L,random.nextInt(3)+3,1L,random.nextInt(10)));
                    add(new ProductItem(3L,3L,100L,random.nextInt(3)+6,1L,random.nextInt(10)));
                    add(new ProductItem(4L,4L,100L,random.nextInt(3)+9,1L,random.nextInt(10)));
                    add(new ProductItem(5L,5L,100L,random.nextInt(3)+12,1L,random.nextInt(10)));
                    add(new ProductItem(6L,6L,100L,random.nextInt(3)+15,1L,random.nextInt(10)));
                    add(new ProductItem(7L,7L,100L,random.nextInt(3)+18,1L,random.nextInt(10)));
                }
            };
            Collection<Collection<ProductItem>> packs = divideStrategy.divide(new PieceTemplate(){
                {
                    setUpperLimit(6);
                    setTemplateDao("pieceTemplateDao");
                }
            }, items);
            int baseline=0;
            for(Collection<ProductItem> pack:packs){
                baseline+=pack.stream().mapToInt(ProductItem::getWeight).sum();
            }
            baseline=(baseline% packs.size()==0)?baseline/ packs.size():(baseline/ packs.size()+1);
            assertNotNull(packs);
            //和MaxDivide进行比较

            Collection<Collection<ProductItem>> packs2 = max.divide(new PieceTemplate(){
                {
                    setUpperLimit(6);
                    setTemplateDao("pieceTemplateDao");
                }
            }, items);
            int loss_greedyaverage=0;
            int loss_average=0;
            for(Collection<ProductItem> pack:packs){
                loss_greedyaverage+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            for(Collection<ProductItem> pack:packs2){
                loss_average+=Math.abs(pack.stream().mapToInt(ProductItem::getWeight).sum()-baseline);
            }
            if(packs.size()< packs2.size()){
                greedy_win+=1;//我们的算法得到的包裹数小于其他算法
            }else if(packs.size()>packs2.size()){
                max_win+=1;
            } else{
                if(loss_greedyaverage<loss_average){
                    greedy_win+=1;//贪心重量算法获胜
                }else if(loss_average<loss_greedyaverage){
                    max_win+=1;//最大重量贪心获胜
                }
            }
        }
        //可能存在两种算法得到同样的loss
        System.out.println(greedy_win);
        System.out.println(max_win);
    }
}
