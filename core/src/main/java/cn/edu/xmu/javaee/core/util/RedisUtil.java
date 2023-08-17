package cn.edu.xmu.javaee.core.util;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.dynamic.RedisCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.BitOperation;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Redis工具类
 * @author Ming Qiu
 **/
@Component
public class RedisUtil {

    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */

    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            }else{
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Serializable get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @param timeout 过期时间， -1为永不过期
     * @return true成功 false失败
     */
    public  boolean set(String key, Serializable value, long timeout) throws RuntimeException{
        if (timeout > 0) {
            // 为了防止雪崩，随机生成过期时间，范围为 timeout ~ timeout + timeout / 5
            timeout = randomizeTimeout(timeout);
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        }else{
            redisTemplate.opsForValue().set(key, value);
        }
        return true;
    }

    /**
     * 递减
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta > 0) {
            return redisTemplate.opsForValue().increment(key, -delta);
        }else {
            return 0;
        }
    }

    /**
     * 判断redis中是否存在键值
     * @param key   键
     * @return
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置key的过期时间
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public Boolean expire(String key, long timeout, TimeUnit unit){
        return redisTemplate.expire(key,timeout,unit);
    }

    /**
     * 获得redis中set集合
     * @param key   键
     * @return
     */
    public Set<Serializable > getSet(String key) {
        return  redisTemplate.opsForSet().members(key);
    }

    /**
     * 将Redis中key对应的值和集合otherKeys中的所有键对应的值并在destKey所对应的集合中
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long unionAndStoreSet(String key, Collection<String> otherKeys, String destKey){
        return redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 将keys的值并起来存在destKey中
     * @param keys
     * @param destKey
     * @return
     */
    public Long unionAndStoreSet(Collection<String> keys, String destKey){
        return redisTemplate.opsForSet().unionAndStore(keys, destKey);
    }
    /**
     * 将values加入key的集合中
     * @param key
     * @param values
     * @return
     */
    public Long addSet(String key, Serializable... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 判断value是否是key的集合中的一元
     * @param key
     * @param value
     * @return
     */
    public Boolean isMemberSet(String key, Serializable value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }
    /**
     * 获取List中第index元素
     * @param key
     * @param index
     * @return
     */
    public Serializable indexList(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 获取list中start至end的元素
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Serializable> rangeList(String key, long start, long end){
        return redisTemplate.opsForList().range(key, start,end);
    }

    /**
     * 从队列中移除value对象
     * @param key redis的key
     * @param count 前多少个相同的值
     * @param value 对象
     * @return
     */
    public Long removeList(String key, long count, Object value){
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 获取List中元素个数
     * @param key
     * @return
     */
    public Long sizeList(String key){
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 将元素加到队头
     * @param key
     * @param value
     * @return
     */
    public long leftPushList(String key, Serializable value){
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 将多个元素加到队头
     * @param key
     * @param values
     * @return
     */
    public long leftPushAllList(String key, Serializable... values){
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 将元素加到队尾
     * @param key
     * @param value
     * @return
     */
    public long rightPushList(String key, Serializable value){
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将多个元素加到队尾
     * @param key
     * @param values
     * @return
     */
    public long rightPushAllList(String key, Serializable... values){
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    public Serializable leftPopList(String key){
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 执行脚本
     * @param script
     * @param keyList
     * @param values
     * @return
     */
    public <T> T executeScript(DefaultRedisScript<T> script,List<String> keyList, Object... values){
        return redisTemplate.execute(script,keyList,values);
    }

    /**
     * 获得hash值
     * @author Ming Qiu
     * <p>
     * date: 2022-11-09 22:45
     * @param key
     * @param field
     * @param value
     */
    public void setHash(String key, String field, Serializable value){
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 设置哈希值
     * @author Ming Qiu
     * <p>
     * date: 2022-11-09 22:45
     * @param key
     * @param field
     * @return
     */
    public Serializable getHash(String key, String field) {
        return (Serializable) redisTemplate.opsForHash().get(key, field);
    }

    public boolean getBit(String key, long offset){
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    public boolean setBit(String key, long offset, boolean value){
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }


    /**
     * bitCount的操作是不会涉及到底层的位数组的扩充的，也就是说没有最大值，没有限制
     * bitCount一般联合SETBIT一起使用，SETBIT有容量限制
     * SETBIT参考 https://redis.io/commands/setbit/
     * 当前最大的位数为  2^32 (this limits bitmaps to 512MB)，
     * 使用bitSet的时候可以使用更多的key，来支持更多的位数， n*2^32 , N为key的数量
     * @param key
     * @param start
     * @param end
     * @return
     */
    public long bitCount(String key, long start, long end){
        return (long) redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitCount(key.getBytes(), start, end));
    }

    public Long bitAnd(String destKey, String... keys){
        byte[][] bytes = new byte[keys.length][];
        for (int i = 0; i < keys.length; i++) {
            bytes[i] = keys[i].getBytes();
        }
        return redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitOp(BitOperation.AND, destKey.getBytes(), bytes));
    }

    public Long bitOr(String destKey, String... keys){
        byte[][] bytes = new byte[keys.length][];
        for (int i = 0; i < keys.length; i++) {
            bytes[i] = keys[i].getBytes();
        }
        return redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitOp(BitOperation.OR, destKey.getBytes(), bytes));
    }

    /**
     *  Creates an empty Bloom Filter with a single sub-filter for the initial capacity requested and with an upper bound error_rate.
     *  By default, the filter auto-scales by creating additional sub-filters when capacity is reached. The new sub-filter is created with size of the previous sub-filter multiplied by expansion.
     * @author Ming Qiu
     * <p>
     * date: 2022-11-27 9:39
     * @param key
     * @param errorRate
     * @param capacity
     * @param expansion
     * @return
     */

    //不需要修改
    public Object bfReserve(String key, float errorRate, long capacity, boolean expansion){
        byte[][] valueBytes = new byte[5][];
        valueBytes[0]= String.valueOf(key).getBytes();
        valueBytes[1]= String.valueOf(errorRate).getBytes();
        valueBytes[2] = String.valueOf(capacity).getBytes();
        if (expansion){
            valueBytes[3] = "EXPANSION".getBytes();
            valueBytes[4] = String.valueOf(2).getBytes();
        }else{
            valueBytes[3] = "NONSCALING".getBytes();
        }
        return redisTemplate.execute((RedisCallback) connection -> connection.execute("BF.RESERVE", valueBytes));
    }


    /**
     *  往bloom过滤器中增加值
     * @author Ming Qiu
     * <p>
     * date: 2022-11-27 8:26
     * @param key
     * @param value
     * @return
     */
    public Object bfAdd(String key, Serializable value){

        /**
         * 这个版本的redis客户端不能处理bloomfilter，需要扩展command来处理,
         * 使用底层client来扩展处理逻辑
         * tips：redis有数据协议标准 ,每个command的请求响应的数据结构不一样，
         * 经查阅资料可知：现在的客户端不能处理 bf:XXX相关的操作
         */

        return redisTemplate.execute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                RedisAsyncCommandsImpl redisAsyncCommands = (RedisAsyncCommandsImpl) connection.getNativeConnection();
                StatefulConnection connect = redisAsyncCommands.getConnection();
                RedisCommandFactory redisCommandFactory = new RedisCommandFactory(connect);
                BloomFilterCommands bloomFilterCommands = redisCommandFactory.getCommands(BloomFilterCommands.class);
                Boolean bfAdd = bloomFilterCommands.bfAdd(key, (String) value);
                return bfAdd;
            }
        });


//        byte[] byteValue = JacksonUtil.toJson(value).getBytes();
//        LettuceClusterConnection lettuceConnection= (LettuceClusterConnection) redisConnection;
//        StatefulRedisConnection<String, String> connect = lettuceConnection.connect();
//        RedisCommandFactory redisCommandFactory = new RedisCommandFactory(lettuceConnection.g());
//        BloomFilterCommands commands = redisCommandFactory.getCommands(BloomFilterCommands.class);
//        return commands.bfAdd(key, (String) value);
//        return redisTemplate.execute((RedisCallback) connection -> connection.execute("BF.ADD", key.getBytes(), byteValue));
    }

    /**
     * 判断Bloom过滤器中是否存在值
     * https://github.com/RedisBloom/RedisBloom
     * @author Ming Qiu
     * <p>
     * date: 2022-11-27 8:29
     * @param key
     * @param value
     * @return
     */
    public Boolean bfExist(String key, Serializable value){

        /**
         * 这个版本的redis客户端不能处理bloomfilter，需要扩展command来处理,
         * 使用底层client来扩展处理逻辑
         * tips：redis有数据协议标准 ,每个command的请求响应的数据结构不一样，
         * 经查阅资料可知：现在的客户端不能处理 bf:XXX相关的操作
         */
        return (Boolean) redisTemplate.execute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                RedisAsyncCommandsImpl redisAsyncCommands = (RedisAsyncCommandsImpl) connection.getNativeConnection();
                StatefulConnection connect = redisAsyncCommands.getConnection();
                RedisCommandFactory redisCommandFactory = new RedisCommandFactory(connect);
                BloomFilterCommands bloomFilterCommands = redisCommandFactory.getCommands(BloomFilterCommands.class);
                Boolean bfAdd = bloomFilterCommands.bfExists(key, (String) value);
                return bfAdd;
            }
        });

//        String valueString = JacksonUtil.toJson(value);
//        return this.bfExist(key, valueString);
    }

    public Boolean bfExist(String key, Long value){
        String valueString= String.valueOf(value);
        return this.bfExist(key, value.shortValue());
    }

    public Boolean bfExist(String key, String value){

        return (Boolean) redisTemplate.execute(new RedisCallback<Object>() {

            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                RedisAsyncCommandsImpl redisAsyncCommands = (RedisAsyncCommandsImpl) connection.getNativeConnection();
                StatefulConnection connect = redisAsyncCommands.getConnection();
                RedisCommandFactory redisCommandFactory = new RedisCommandFactory(connect);
                BloomFilterCommands bloomFilterCommands = redisCommandFactory.getCommands(BloomFilterCommands.class);
                Boolean bfAdd = bloomFilterCommands.bfExists(key, (String) value);
                return bfAdd;
            }
        });

//        byte[] byteValue = value.getBytes();
//        return (Boolean) redisTemplate.execute((RedisCallback) connection -> connection.execute("BF.EXISTS", key.getBytes(), byteValue));
//        return (Boolean) redisTemplate.execute((RedisCallback) connection -> connection.execute("BF.EXISTS", key, value));
    }

    /**
     * 随机化 key 过期时间
     * @param timeout 过期时间
     * @return 随机化后的过期时间
     */
    private long randomizeTimeout(long timeout) {
        long min = 1;
        long max = timeout / 5;
        return timeout + (long) (new Random().nextDouble() * (max - min));
    }

}
