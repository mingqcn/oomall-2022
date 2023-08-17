//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.dao.bo.Shop;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.GoodsDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Product;
import cn.edu.xmu.oomall.shop.dao.bo.ShopServiceProduct;
import cn.edu.xmu.oomall.shop.mapper.ShopServiceProductPoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.ShopPo;
import cn.edu.xmu.oomall.shop.mapper.po.ShopServiceProductPo;
import cn.edu.xmu.oomall.shop.service.dto.IdNameDto;
import cn.edu.xmu.oomall.shop.service.dto.SimpleShopDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

/**
 * 服务商服务的商品
 */
@Repository
public class ShopServiceProductDao {

    private static final Logger logger = LoggerFactory.getLogger(ShopServiceProductDao.class);

    public static final String KEY = "SP%d";

    @Value("${oomall.shop.shop.timeout}")
    private long timeout;

    private RedisUtil redisUtil;

    private ShopServiceProductPoMapper shopServiceProductPoMapper;

    private GoodsDao goodsDao;

    private FreightDao freightDao;

    private ShopDao shopDao;

    @Autowired
    public ShopServiceProductDao(RedisUtil redisUtil, ShopServiceProductPoMapper shopServiceProductPoMapper, GoodsDao goodsDao, FreightDao freightDao, ShopDao shopDao) {
        this.redisUtil = redisUtil;
        this.shopServiceProductPoMapper = shopServiceProductPoMapper;
        this.goodsDao = goodsDao;
        this.freightDao = freightDao;
        this.shopDao = shopDao;
    }

    /**
     * 返回商铺有效的服务商品
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 12:39
     * @param shopId
     * @param page
     * @param pageSize
     * @return
     * @throws RuntimeException
     */
    public List<Product> retrieveValidByShopId(Long shopId, Integer page, Integer pageSize) throws RuntimeException{
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ShopServiceProductPo> ret =  shopServiceProductPoMapper.findByMaintainerIdEqualsAndInvalidEqualsAndBeginTimeBeforeAndEndTimeAfter(shopId, ShopServiceProduct.VALID, now, now, pageable);
        return this.retrieveProduct(ret);
    }

    /**
     * 返回商铺所有的服务商品
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 12:39
     * @param shopId
     * @param page
     * @param pageSize
     * @return
     * @throws RuntimeException
     */
    public List<Product> retrieveByShopId(Long shopId, Integer page, Integer pageSize) throws RuntimeException{
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ShopServiceProductPo> ret =  shopServiceProductPoMapper.findByMaintainerId(shopId, pageable);
        return retrieveProduct(ret);
    }

    /**
     * 将对象转成Product
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 12:40
     * @return
     */
    private List<Product> retrieveProduct(Page<ShopServiceProductPo> pagePos) {
        if (!pagePos.isEmpty()){
            return pagePos.stream().map(po -> po.getProductId()).map(id -> goodsDao.retrieveProductById(id).getData()).collect(Collectors.toList());

        } else{
            return new ArrayList<>();
        }
    }

    private ShopServiceProduct getBo(ShopServiceProductPo po, String redisKey){
        ShopServiceProduct ret = cloneObj(po, ShopServiceProduct.class);
        if (null != redisKey) {
            redisUtil.set(redisKey, ret, timeout);
        }
        this.setBo(ret);
        return ret;
    }

    private void setBo(ShopServiceProduct bo){
        bo.setShopDao(this.shopDao);
        bo.setFreightDao(this.freightDao);
        bo.setGoodsDao(goodsDao);
    }

    /**
    * 按照主键获取对象
    *
    * @param id
    * @return Optional<ShopServiceProduct>
    * @author chenyz
    * @date 2022-11-29 9:05
    */
    public Optional<ShopServiceProduct> findById(Long id) throws RuntimeException {
        ShopServiceProduct serviceProduct = null;
        if (null != id) {
            logger.info("findObjById: id = {}",id);
            String key = String.format(KEY, id);
            if (redisUtil.hasKey(key)) {
                serviceProduct = (ShopServiceProduct) redisUtil.get(key);
                this.setBo(serviceProduct);
            } else {
                Optional<ShopServiceProductPo> ret = this.shopServiceProductPoMapper.findById(id);
                if(ret.isPresent())
                {
                    serviceProduct = getBo(ret.get(), key);
                    logger.info("shopId:", serviceProduct.getId());
                } else {
                    throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺", id));
                }
            }
        }
        return Optional.of(serviceProduct);
    }

    /**
    * 定义服务商在某个地区服务的商品
    *
    * @param serviceProduct
    * @return SimpleProductServiceDto
    * @author chenyz
    * @date 2022-11-26 23:41
    */
    public ShopServiceProduct insert(ShopServiceProduct serviceProduct, UserDto userDto) throws RuntimeException{
        ShopServiceProductPo po = cloneObj(serviceProduct, ShopServiceProductPo.class);
        putUserFields(po, "creator",userDto);
        putGmtFields(po, "create");
        logger.debug("saveById: po = {}", po);
        this.shopServiceProductPoMapper.save(po);
        ShopServiceProduct bo = cloneObj(po, ShopServiceProduct.class);
        this.setBo(bo);
        return bo;
    }

    /**
     * 更改商品服务信息
     *
     * @param serviceProduct 商品服务信息
     * @return
     * @author chenyz
     * @date 2022-11-27 0:33
     */
    public Set<String> save(ShopServiceProduct serviceProduct, UserDto userDto) throws RuntimeException{
        Set<String> delKeys = new HashSet<>();
        if (null != serviceProduct && null != serviceProduct.getId()) {
            String key = String.format(KEY, serviceProduct.getId());
            ShopServiceProductPo po = cloneObj(serviceProduct, ShopServiceProductPo.class);
            if (null != userDto) {
                putUserFields(po, "modifier", userDto);
                putGmtFields(po, "Modified");
            }
            logger.debug("saveById: po = {}", po);
            this.shopServiceProductPoMapper.save(po);
            delKeys.add(key);
            redisUtil.del(key);
        }
        return delKeys;
    }

    /**
    * 根据主键删除
    *
    * @param id
    * @return Set<String>
    * @author chenyz
    * @date 2022-12-28 13:33
    */
    public Set<String> deleteById(Long id) throws RuntimeException{
        Set<String> delKeys = new HashSet<>();
        if (null != id) {
            String key = String.format(KEY, id);
            logger.debug("deleteById: id = {}", id);
            this.shopServiceProductPoMapper.deleteById(id);
            delKeys.add(key);
            redisUtil.del(key);
        }
        return delKeys;
    }

    public static final Byte ALL = 1;
    
    public static final Byte VALID = 0;

    /**
     * 通过商品和地区找到服务商(按照优先级排序)
     *
     * @param pid 商品id
     * @param rid 地区id
     * @return
     * @author chenyz
     * @date 2022-11-27 1:16
     */
    public PageDto<SimpleShopDto> retrieveProductServiceByProductIdAndRegionId(Long pid, Long rid, Byte all, Integer page, Integer pageSize){
        List<SimpleShopDto> ret = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<ShopServiceProductPo> pos = null;
        if(ShopServiceProductDao.ALL == all)
            pos = this.shopServiceProductPoMapper.findByProductIdAndRegionIdOrderByPriority(pid, rid, pageable);
        else if (ShopServiceProductDao.VALID == all){
            LocalDateTime now = LocalDateTime.now();
            pos = this.shopServiceProductPoMapper.findByProductIdAndRegionIdAndInvalidAndBeginTimeBeforeAndEndTimeAfter(pid, rid, ShopServiceProduct.VALID, now, now, pageable);
        }
        if(null != pos && pos.getSize() > 0) {
            ret = pos.stream().map(ShopServiceProductPo::getMaintainerId).map(id -> shopDao.findById(id).orElse(null)).map(shop -> cloneObj(shop, SimpleShopDto.class)).collect(Collectors.toList());
        }
        return new PageDto<>(ret, page, pageSize);
    }

    /**
    * 根据服务商id找到所有的地区服务商品
    *
    * @param
    * @return
    * @author chenyz
    * @date 2022-12-28 13:15
    */
    public List<Long> retrieveProductServiceByMaintainerId(Long mid){
        List<ShopServiceProductPo> pos = this.shopServiceProductPoMapper.findByMaintainerId(mid);
        return pos.stream().map(po -> po.getId()).collect(Collectors.toList());
    }
}
