//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.dao.bo.Shop;
import cn.edu.xmu.oomall.shop.mapper.ShopPoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.ShopPo;
import cn.edu.xmu.oomall.shop.service.dto.SimpleShopDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

/**
 * 商品Repository
 */
@Repository
public class ShopDao {

    private static final Logger logger = LoggerFactory.getLogger(ShopDao.class);

    public static final String KEY = "S%d";

    @Value("${oomall.shop.shop.timeout}")
    private long timeout;

    private RedisUtil redisUtil;

    private ShopPoMapper shopPoMapper;

    @Autowired
    public ShopDao(RedisUtil redisUtil, ShopPoMapper shopPoMapper) {
        this.redisUtil = redisUtil;
        this.shopPoMapper = shopPoMapper;
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
    private Shop getBo(ShopPo po, String redisKey){
        Shop ret = cloneObj(po, Shop.class);
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
    private void setBo(Shop bo){
        bo.setShopDao(this);
    }


    /**
    * 按照主键获得对象
    *
    * @param id
    * @return Optional<Shop>
    * @author chenyz
    * @date 2022-11-29 9:02
    */
    public Optional<Shop> findById(Long id) throws RuntimeException {
        AtomicReference<Shop> shop = new AtomicReference<>();
        if (null != id) {
            logger.debug("findObjById: id = {}",id);
            String key = String.format(KEY, id);
            if (redisUtil.hasKey(key)) {
                shop.set((Shop) redisUtil.get(key));
            } else {
                Optional<ShopPo> ret = this.shopPoMapper.findById(id);
                ret.ifPresent(po ->{
                    shop.set(cloneObj(po, Shop.class));
                    //永不过期
                    redisUtil.set(key, shop.get(), -1);
                });
                if (ret.isEmpty()) {
                    throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺", id));
                }
            }
        }
        return Optional.of(shop.get());
    }

    /**
    * 按照创建者找到对象
    *
    * @param creatorId
    * @return List<Shop>
    * @author chenyz
    * @date 2022-11-26 13:17
    */
    public PageDto<Shop> retrieveByCreatorId(Long creatorId, Integer page, Integer pageSize) throws RuntimeException{
        List<Shop> ret = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ShopPo> pos =  this.shopPoMapper.findByCreatorId(creatorId, pageable);
        if(null != pos && !pos.isEmpty()) {
            ret = pos.stream().map(po -> cloneObj(po, Shop.class)).collect(Collectors.toList());
            logger.info("bos size:{}", ret.size());
        }
        return new PageDto<>(ret, page, pageSize);
    }

    /**
    * 新增店铺
    *
    * @param shop
    * @param userDto
    * @return SimpleShopDto
    * @author chenyz
    * @date 2022-11-26 14:06
    */
    public Shop insert(Shop shop, UserDto userDto) throws RuntimeException{
        ShopPo po = cloneObj(shop, ShopPo.class);
        putUserFields(po, "creator",userDto);
        putGmtFields(po, "create");
        logger.debug("saveById: po = {}", po);
        this.shopPoMapper.save(po);
        shop.setId(po.getId());
        return cloneObj(po, Shop.class);
    }

    /**
    * 店家修改店铺信息
    *
    * @param shop
    * @param userDto
    * @return
    * @author chenyz
    * @date 2022-11-26 18:09
    */
    public Set<String> save(Shop shop, UserDto userDto) throws RuntimeException{
        Set<String> delKeys = new HashSet<>();
        if (null != shop && null != shop.getId()) {
            String key = String.format(KEY, shop.getId());
            ShopPo po = cloneObj(shop, ShopPo.class);
            if (null != userDto) {
                putUserFields(po, "modifier", userDto);
                putGmtFields(po, "Modified");
            }
            logger.debug("saveById: po = {}", po);
            this.shopPoMapper.save(po);
            delKeys.add(key);
            redisUtil.del(key);
        }
        return delKeys;
    }

    /**
    * 顾客查询店铺信息(只返回上线和下线状态的商铺)
    *
    * @param type 店铺类型
    * @param name 店铺名称
    * @return PageInfo<SimpleShopDto>
    * @author chenyz
    * @date 2022-11-26 18:10
    */
    public PageDto<Shop> retrieveValidByTypeAndName(Byte type, String name, Integer page, Integer pageSize) throws RuntimeException{
        List<Shop> ret = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<ShopPo> shopPos = null;
        if(null == type && null == name)
            shopPos = this.shopPoMapper.findByStatusNotAndStatusNot(Shop.NEW, Shop.ABANDON, pageable);
        else if(null != type && null == name)
            shopPos = this.shopPoMapper.findByTypeAndStatusNotAndStatusNot(type, Shop.NEW, Shop.ABANDON, pageable);
        else if (null == type)
            shopPos = this.shopPoMapper.findByNameAndStatusNotAndStatusNot(name, Shop.NEW, Shop.ABANDON, pageable);
        else
            shopPos = this.shopPoMapper.findByNameAndTypeAndStatusNotAndStatusNot(name, type, Shop.NEW, Shop.ABANDON, pageable);
        if(null != shopPos && shopPos.getSize() > 0) {
            ret = shopPos.stream().map(po -> cloneObj(po, Shop.class)).collect(Collectors.toList());
        }
        return new PageDto<>(ret, page, pageSize);
    }

    /**
     * 管理员查询店铺信息(会返回所有状态的商铺)
     *
     * @param type
     * @param status
     * @param name
     * @return PageDto<SimpleShopDto>
     * @author chenyz
     * @date 2022-11-26 20:54
     */
    public PageDto<Shop> retrieveByTypeAndStatusAndName(Byte type, Byte status, String name, Integer page, Integer pageSize) throws RuntimeException {
        List<Shop> ret = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<ShopPo> shopPos;
        if(null != type && null != status && null != name)
            shopPos = this.shopPoMapper.findByNameAndTypeAndStatus(name, type, status, pageable);
        else if(null != type && null != status)
            shopPos = this.shopPoMapper.findByTypeAndStatus(type, status, pageable);
        else if(null != type&& null != name)
            shopPos = this.shopPoMapper.findByNameAndType(name, type, pageable);
        else if(null == type && null != status && null != name)
            shopPos = this.shopPoMapper.findByNameAndStatus(name, status, pageable);
        else if(null != type)
            shopPos = this.shopPoMapper.findByType(type, pageable);
        else if(null != status)
            shopPos = this.shopPoMapper.findByStatus(status, pageable);
        else if(null != name)
            shopPos = this.shopPoMapper.findByName(name, pageable);
        else
            shopPos = this.shopPoMapper.findAll(pageable);
        if(null != shopPos && shopPos.getSize() > 0) {
            ret = shopPos.stream().map(po -> cloneObj(po, Shop.class)).collect(Collectors.toList());
        }
        return new PageDto<>(ret, page, pageSize);
    }
}
