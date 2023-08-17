//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.dao.bo.Channel;
import cn.edu.xmu.oomall.payment.dao.bo.ShopChannel;
import cn.edu.xmu.oomall.payment.mapper.generator.ShopChannelPoMapper;
import cn.edu.xmu.oomall.payment.mapper.generator.po.ShopChannelPo;
import cn.edu.xmu.oomall.payment.mapper.generator.po.ShopChannelPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

/**
 * ShopChannel的dao对象
 */
@Repository
public class ShopChannelDao{

    private static final Logger logger = LoggerFactory.getLogger(ShopChannelDao.class);

    private static final String KEY = "SC%d";

    @Value("${oomall.payment.shopchannel.timeout}")
    private long timeout;

    private ShopChannelPoMapper shopChannelPoMapper;

    private RedisUtil redisUtil;

    private ChannelDao channelDao;

    @Autowired
    public ShopChannelDao(ShopChannelPoMapper shopChannelPoMapper, RedisUtil redisUtil, ChannelDao channelDao) {
        this.shopChannelPoMapper = shopChannelPoMapper;
        this.redisUtil = redisUtil;
        this.channelDao = channelDao;
    }

    /**
     * 获得bo对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 11:46
     * @param po
     * @param redisKey
     * @return
     */
    private ShopChannel getBo(ShopChannelPo po, String redisKey){
        ShopChannel ret = cloneObj(po, ShopChannel.class);
        if (null != redisKey) {
            redisUtil.set(redisKey, ret, timeout);
        }
        this.setBo(ret);
        return ret;
    }

    /**
     * 把bo中设置dao
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 11:46
     * @param bo
     */
    private void setBo(ShopChannel bo){
        bo.setChannelDao(this.channelDao);
    }
    /**
     * 由id获得对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-07 0:38
     * @param id
     * @return
     * @throws RuntimeException
     */
    public ShopChannel findById(Long id) throws RuntimeException {
        ShopChannel ret = null;
        if (null != id) {
            String key = String.format(KEY, id);
            if (redisUtil.hasKey(key)) {
                ret = (ShopChannel) redisUtil.get(key);
                this.setBo(ret);
            } else {
                ShopChannelPo po = shopChannelPoMapper.selectByPrimaryKey(id);
                if(null == po){
                    throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺支付渠道", id));
                }
                ret = this.getBo(po, key);
            }
            ret.setChannelDao(this.channelDao);
        }
        logger.debug("findObjById: id = " + id + " ret = " + ret);
        return ret;
    }

    /**
     * 按照id更新ShopChannel
     * @author Ming Qiu
     * <p>
     * date: 2022-11-10 6:07
     * @param shopChannel
     * @return 要删除的key
     * @throws RuntimeException
     */
    public Set<String> saveById(ShopChannel shopChannel) throws RuntimeException {
        Set<String> delKeys = new HashSet<>();
        if (null != shopChannel && null != shopChannel.getId()){
            String key = String.format(KEY, shopChannel.getId());
            ShopChannelPo po = cloneObj(shopChannel, ShopChannelPo.class);
            shopChannelPoMapper.updateByPrimaryKeySelective(po);
            delKeys.add(key);
        }
        return delKeys;
    }

    public ShopChannel findByShopIdAndChannelId(Long shopId, Long channelId) throws RuntimeException{
        ShopChannel ret = null;
        if (null != shopId && null != channelId) {
            ShopChannelPoExample example = new ShopChannelPoExample();
            ShopChannelPoExample.Criteria criteria = example.createCriteria();
            criteria.andChannelIdEqualTo(channelId);
            List<ShopChannelPo> poList1 = this.shopChannelPoMapper.selectByExample(example);

            criteria.andShopIdEqualTo(shopId);
            PageHelper.startPage(1,1,false);
            List<ShopChannelPo> poList2 = this.shopChannelPoMapper.selectByExample(example);
            if (poList2.size() > 0){
                ret = cloneObj(poList2.get(0), ShopChannel.class);
                ret.setChannelDao(this.channelDao);
            }else {
                if (poList1.size()>0)
                    throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE);
                else
                    throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
        }
        return ret;
    }

    //判断是否存在某个商家支付渠道
    public boolean hasByShopIdAndChannelId(Long shopId, Long channelId) throws RuntimeException{
        boolean ret = false;

        ShopChannelPoExample example = new ShopChannelPoExample();
        ShopChannelPoExample.Criteria criteria = example.createCriteria();
        criteria.andChannelIdEqualTo(channelId);
        criteria.andShopIdEqualTo(shopId);
        List<ShopChannelPo> poList = this.shopChannelPoMapper.selectByExample(example);
        if (poList.size() > 0)
            ret = true;
        return ret;
    }


    /**
     * 获得商铺所有的支付渠道(有效和无效)
     * @param shopId 商铺Id
     * @param page  页码
     * @param pageSize 页大小
     * @param flag 是否要分页(复用代码)
     */
    public PageInfo<ShopChannel> retrieveByShopId(Long shopId, Integer page, Integer pageSize, boolean flag) throws RuntimeException{
        List<ShopChannel> ret = null;
        if(null != shopId){
            ShopChannelPoExample shopChannelPoExample = new ShopChannelPoExample();
            ShopChannelPoExample.Criteria criteria = shopChannelPoExample.createCriteria();
            criteria.andShopIdEqualTo(shopId);
            if(flag){
                PageHelper.startPage(page, pageSize, false);
            }
            List<ShopChannelPo> shopChannelPoList = shopChannelPoMapper.selectByExample(shopChannelPoExample);
            if(shopChannelPoList.size() > 0){
                ret = shopChannelPoList.stream()
                        .map(po -> this.getBo(po, String.format(ShopChannelDao.KEY, po.getId())))
                        .collect(Collectors.toList());
            } else {
                ret = new ArrayList<>();
            }
        }
        return new PageInfo<>(ret);
    }

    /**
     * 商家签约支付渠道
     * @param bo 新增的渠道信息
     * @param user 登录用户
     */
    public ShopChannel save(ShopChannel bo, UserDto user) {
        ShopChannelPo shopChannelPo = cloneObj(bo, ShopChannelPo.class);
        putUserFields(shopChannelPo, "creator", user);
        putGmtFields(shopChannelPo, "create");
        shopChannelPoMapper.insertSelective(shopChannelPo);

        return cloneObj(shopChannelPo, ShopChannel.class);
    }

    /**
     * 解约店铺的账号
     */
    public ReturnObject delById(ShopChannel shopChannel){
        redisUtil.del(String.format(ShopChannelDao.KEY, shopChannel.getId()));
        shopChannelPoMapper.deleteByPrimaryKey(shopChannel.getId());

        return new ReturnObject(ReturnNo.OK);
    }


    public List<ShopChannel> findByShopId(Long shopId) {
        List<ShopChannel> ret = new ArrayList<>();
        if (null != shopId) {
            ShopChannelPoExample example = new ShopChannelPoExample();
            ShopChannelPoExample.Criteria criteria = example.createCriteria();
            criteria.andShopIdEqualTo(shopId);
            List<ShopChannelPo> poList = this.shopChannelPoMapper.selectByExample(example);
            if (poList.size() > 0){
                poList.forEach(po->{
                    ret.add(cloneObj(po, ShopChannel.class));
                });
            }else {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
        }
        return ret;
    }
}
