//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.dao.bo.DivRefundTrans;
import cn.edu.xmu.oomall.payment.dao.bo.Ledger;
import cn.edu.xmu.oomall.payment.dao.bo.PayTrans;
import cn.edu.xmu.oomall.payment.mapper.generator.LedgerPoMapper;
import cn.edu.xmu.oomall.payment.mapper.generator.po.DivRefundTransPo;
import cn.edu.xmu.oomall.payment.mapper.generator.po.LedgerPo;
import cn.edu.xmu.oomall.payment.mapper.generator.po.LedgerPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;


/**
 * 台账dao对象
 */
@Repository
public class LedgerDao {

    private static final Logger logger = LoggerFactory.getLogger(LedgerDao.class);

    private LedgerPoMapper ledgerPoMapper;
    private TransactionDao transactionDao;
    private ShopChannelDao shopChannelDao;

    @Autowired
    public LedgerDao(LedgerPoMapper ledgerPoMapper, TransactionDao transactionDao, ShopChannelDao shopChannelDao) {
        this.ledgerPoMapper = ledgerPoMapper;
        this.transactionDao = transactionDao;
        this.shopChannelDao = shopChannelDao;
    }

    private Ledger getBo(LedgerPo po) {
        Ledger bo = cloneObj(po, Ledger.class);
        bo.setTransactionDao(this.transactionDao);
        bo.setShopChannelDao(this.shopChannelDao);
        return bo;
    }

    public PageInfo<Ledger> retrieveLedgersByShopChannelAndBetweenSuccessTime(LocalDateTime beginTime, LocalDateTime endTime, Byte type, Long shopChannelId, Integer page, Integer pageSize) throws RuntimeException{
        LedgerPoExample example = new LedgerPoExample();
        LedgerPoExample.Criteria criteria = example.createCriteria();
        criteria.andSuccessTimeBetween(beginTime,endTime);
        criteria.andShopChannelIdEqualTo(shopChannelId);
        logger.debug("type={}",type.toString());
        if (!type.equals(Ledger.ALL_TYPE)){
            criteria.andTypeEqualTo(type);
        }

        PageHelper.startPage(page, pageSize, false);
        List<LedgerPo> poList = ledgerPoMapper.selectByExample(example);

        List<Ledger> ret =  poList.stream().map(po -> getBo(po)).collect(Collectors.toList());
        return new PageInfo<>(ret);
    }

    public PageInfo<Ledger> retrieveLedgersInShopChannelAndBetweenSuccessTime(LocalDateTime beginTime, LocalDateTime endTime, Byte type, List<Long> shopChannelIds, Integer page, Integer pageSize) {
        LedgerPoExample example = new LedgerPoExample();
        LedgerPoExample.Criteria criteria = example.createCriteria();
        criteria.andSuccessTimeBetween(beginTime,endTime);
        logger.debug("InshopChannelIds:{}", shopChannelIds);
        criteria.andShopChannelIdIn(shopChannelIds);

        if (!type.equals(Ledger.ALL_TYPE)){
            criteria.andTypeEqualTo(type);
        }

        PageHelper.startPage(page, pageSize, false);
        List<LedgerPo> poList = ledgerPoMapper.selectByExample(example);

        List<Ledger> ret =  poList.stream().map(po -> getBo(po)).collect(Collectors.toList());
        return new PageInfo<>(ret);
   }
    public void saveById(Ledger obj, UserDto user) throws RuntimeException{
        if (null != user) {
            LedgerPo po = cloneObj(obj, LedgerPo.class);
            putUserFields(po, "adjust", user);
            putUserFields(po, "modifier", user);
            putGmtFields(po, "Modified");
            po.setAdjustTime(po.getGmtModified());
            int retL = ledgerPoMapper.updateByPrimaryKeySelective(po);
            if (retL==0){
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "台账",po.getId()));
            }
        }
    }
    public void save(Ledger ledger,UserDto user) throws RuntimeException{
        logger.debug("insertObj: ledger = {}", ledger);
        LedgerPo po = cloneObj(ledger,LedgerPo.class);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        logger.debug("insertObj: po .status {}", po.getStatus());
        logger.debug("insertObj: po .creator {}", user.getId());
        int ret = ledgerPoMapper.insert(po);
        if(ret==0){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "台账", ledger.getId()));
        }
    }

    public Ledger findById(Long id)  throws RuntimeException{
        Ledger ret = null;
        if (null != id) {
            LedgerPo po = ledgerPoMapper.selectByPrimaryKey(id);
            if (null == po) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "台账", id));
            }
            ret = cloneObj(po, Ledger.class);
            ret.setAdjust_id(po.getAdjustId());
            ret.setAdjust_name(po.getAdjustName());
            ret.setTransactionDao(this.transactionDao);
            ret.setShopChannelDao(this.shopChannelDao);
        }
        return ret;
    }
}
