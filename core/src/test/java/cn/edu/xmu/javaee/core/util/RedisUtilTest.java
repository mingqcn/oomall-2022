package cn.edu.xmu.javaee.core.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest
public class RedisUtilTest {

    @Resource
    private RedisUtil redisUtil;
//
//    private java.lang.reflect.Method randomizeTimeout;
//
//    @BeforeEach
//    public void initializeRandomizeTimeout() throws NoSuchMethodException {
//        redisUtil = new RedisUtil(new RedisTemplate<>());
//        randomizeTimeout = redisUtil.getClass().getDeclaredMethod("randomizeTimeout", long.class);
//        randomizeTimeout.setAccessible(true);
//    }
//
//    /**
//     * 测试 randomizeTimeout 方法生成的数是否在指定范围内
//     */
//    @Test
//    public void randomizeTimeout1() {
//        long originalTimeout = 10;
//        // 样本数
//        long numSamples = 10000;
//        Stream.generate(() -> {
//                    try {
//                        return randomizeTimeout.invoke(redisUtil, originalTimeout);
//                    } catch (IllegalAccessException | InvocationTargetException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .limit(numSamples)
//                .forEach((timeout) -> {
//                    assertTrue(originalTimeout <= (long) timeout && (long) timeout < originalTimeout + originalTimeout / 5.0);
//                });
//    }
//
//    /**
//     * 测试 randomizeTimeout 方法生成的数是否均匀
//     */
//    @Test
//    public void randomizeTimeout2() {
//        long originalTimeout = 3600;
//        // 样本数
//        long numSamples = 10000;
//        double upperProbability = 0.2;
//        Stream.generate(() -> {
//                    try {
//                        return randomizeTimeout.invoke(redisUtil, originalTimeout);
//                    } catch (IllegalAccessException | InvocationTargetException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .limit(numSamples)
//                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
//                .values()
//                .forEach((count) -> {
//                    assertTrue((double) count / numSamples < upperProbability);
//                });
//    }

    //@Test
    public void setValues(){

        //过滤器名字
        String bfName="bf006";

        //布隆过滤器判断为【数据存在】 可能数据并不存在，
        //但是如果判断为【数据不存在】那么数据就一定是不存在的

        // 配置错误率和存储空间
        // System.err.println(redisUtil.bfReserve(bfName, (float) 0.001, 5000,false));
        // 添加元素
        redisUtil.bfAdd(bfName, "abc0009");
        assertTrue(redisUtil.bfExist(bfName, "abc0009"));
        System.err.println();
        // 判断元素是否存在
        System.err.println(redisUtil.bfExist(bfName, "abc000912"));

    }

    //@Test
    public void testOther(){

        redisUtil.set("aaa","vvv",500);
        redisUtil.expire("aaa",100, TimeUnit.SECONDS);
        redisUtil.del("aaa");

        Long res = redisUtil.bitAnd("bc", "binary");
        System.out.println(res);
        long count = redisUtil.bitCount("bc", 3, 5);
        System.out.println("count:"+count);

    }

}
