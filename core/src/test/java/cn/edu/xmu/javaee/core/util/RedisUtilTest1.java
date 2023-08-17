package cn.edu.xmu.javaee.core.util;


import cn.edu.xmu.javaee.core.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest
public class RedisUtilTest1 {

    @Autowired
    private RedisUtil redisUtil;

    /**关于bitCount，我这两天搜了一下资料
     * bitCount的操作是不会涉及到底层的位数组的扩充的，也就是说没有最大值，没有限制
     * bitCount一般联合SETBIT一起使用，SETBIT有容量限制
     * SETBIT参考 https://redis.io/commands/setbit/
     * 当前最大的位数为  2^32 (this limits bitmaps to 512MB)，
     * 使用bitSet的时候可以使用更多的key，来支持更多的位数， n*2^32 , N为key的数量
     */

    //@Test
    public void setValues(){

        String bfName="bf001";

        //布隆过滤器判断为【数据存在】 可能数据并不存在，
        //但是如果判断为【数据不存在】那么数据就一定是不存在的

        // 配置错误率和存储空间
//        System.err.println(redisUtil.bfReserve(bfName, (float) 0.001, 5000,false));

        // 添加元素
        redisUtil.bfAdd(bfName, "abc0009");

        // 判断元素是否存在
        assertTrue(redisUtil.bfExist(bfName, "abc0009"));

        assertFalse(redisUtil.bfExist(bfName, "cbc0009"));

    }

    //@Test
    public void testOther(){

        redisUtil.set("aaa","vvv",500);
        redisUtil.expire("aaa",100, TimeUnit.SECONDS);
        redisUtil.del("aaa");

        Long res = redisUtil.bitAnd("bc", "binary");
        long count = redisUtil.bitCount("bc", 3, 5);

    }

}
