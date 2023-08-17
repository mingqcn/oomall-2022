//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.dao.bo.DivPayTrans;
import cn.edu.xmu.oomall.payment.dao.bo.DivRefundTrans;
import cn.edu.xmu.oomall.payment.dao.bo.PayTrans;
import cn.edu.xmu.oomall.payment.mapper.generator.PayTransPoMapper;
import cn.edu.xmu.oomall.payment.mapper.generator.po.DivPayTransPo;
import cn.edu.xmu.oomall.payment.mapper.generator.po.DivPayTransPoExample;
import cn.edu.xmu.oomall.payment.mapper.generator.po.PayTransPo;
import cn.edu.xmu.oomall.payment.mapper.generator.po.PayTransPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class PayTransDao {

    private static final Logger logger = LoggerFactory.getLogger(PayTransDao.class);

    private ShopChannelDao shopChannelDao;

    private RefundTransDao refundTransDao;
    private DivPayTransDao divPayTransDao;

    private PayTransPoMapper payTransPoMapper;

    @Autowired
    @Lazy
    public PayTransDao(ShopChannelDao shopChannelDao, RefundTransDao refundTransDao, PayTransPoMapper payTransPoMapper, DivPayTransDao divPayTransDao) {
        this.shopChannelDao = shopChannelDao;
        this.refundTransDao = refundTransDao;
        this.payTransPoMapper = payTransPoMapper;
        this.divPayTransDao = divPayTransDao;
    }

    private PayTrans getBo(PayTransPo po) {
        PayTrans ret;
        ret = cloneObj(po, PayTrans.class);
        ret.setRefundTransDao(this.refundTransDao);
        ret.setShopChannelDao(this.shopChannelDao);
        ret.setDivPayTransDao(this.divPayTransDao);
        return ret;
    }

    public void save(PayTrans obj, UserDto user) throws RuntimeException{
        logger.debug("insertObj: obj = {}", obj);
        PayTransPo po = cloneObj(obj, PayTransPo.class);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        logger.debug("insertObj: po = {}", po);
        payTransPoMapper.insertSelective(po);
        obj.setId(po.getId());
        obj.setShopChannelDao(this.shopChannelDao);
    }

    public void saveById(PayTrans obj, UserDto user) throws RuntimeException{
        PayTransPo po = cloneObj(obj, PayTransPo.class);
        if (null != user) {
            putUserFields(po, "modifier", user);
            putGmtFields(po, "Modified");
        }
        logger.debug("saveById: po = {}", po);
        int ret = payTransPoMapper.updateByPrimaryKeySelective(po);
        if (0 == ret){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "支付交易",po.getId()));
        }
    }

    /**
     * 由id返回支付交易对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-06 23:31
     * @param id
     * @return
     * @throws RuntimeException
     * @throws BusinessException 无此对象
     */
    public PayTrans findById(Long id) throws RuntimeException{
        PayTrans ret = null;
        if (null != id) {
            PayTransPo po = payTransPoMapper.selectByPrimaryKey(id);
            if (null == po) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "支付交易", id));
            }
            ret = getBo(po);
        }
        return ret;
    }

    public void saveById(Long id, UserDto user, LocalDateTime adjustTime) throws RuntimeException{
        if (null != id) {
            PayTransPo po = new PayTransPo();
            po.setId(id);

            //修改交易状态
            putUserFields(po, "adjust", user);
            putUserFields(po, "modifier", user);
            putGmtFields(po, "Modified");
            po.setAdjustTime(adjustTime);
            po.setStatus(PayTrans.CHECKED);

            int ret = this.payTransPoMapper.updateByPrimaryKeySelective(po);
            if (0 == ret) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "支付交易", id));
            }
        }
    }

    public List<PayTrans> retrieveBetweenSuccessTimeAndShopChannelIdEqualsIdAndStatusEqualsSUCCESS(LocalDateTime beginTime, LocalDateTime endTime,Long shopChannel)throws RuntimeException{
        //根据支付完成时间来筛选交易
        PayTransPoExample example = new PayTransPoExample();
        PayTransPoExample.Criteria criteria = example.createCriteria();
        criteria.andSuccessTimeBetween(beginTime,endTime).andStatusEqualTo(PayTrans.SUCCESS).andShopChannelIdEqualTo(shopChannel);

        List<PayTransPo> payTransPos = payTransPoMapper.selectByExample(example);

        List<PayTrans> payTrans = new ArrayList<PayTrans>();

        for(PayTransPo po:payTransPos)
            payTrans.add(getBo(po));
        return payTrans;
    }

    public void saveById(Long id,Byte Status,UserDto userDto)throws RuntimeException{
        if(id!=null&&Status!=null){
            logger.debug("entersaveById");
            PayTransPo po = new PayTransPo();
            po.setId(id);
            po.setStatus(Status);
            int ret = payTransPoMapper.updateByPrimaryKeySelective(po);
            if (0 == ret) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "支付交易", id));
            }
        }
    }

    public PageInfo<PayTrans> retrieveByShopChannelId(Long shopChannelId, String transNo, Long adjustId, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) throws RuntimeException{
        List<PayTrans> ret = null;

        PayTransPoExample example = new PayTransPoExample();
        PayTransPoExample.Criteria criteria = example.createCriteria();
        criteria.andShopChannelIdEqualTo(shopChannelId);
        if (null != transNo) {
            criteria.andTransNoEqualTo(transNo);
        }
        if (null != adjustId) {
            criteria.andAdjustIdEqualTo(adjustId);
        }
        if (null != beginTime) {
            criteria.andTimeBeginGreaterThan(beginTime);
        }
        if (null != endTime) {
            criteria.andTimeExpireLessThan(endTime);
        }

        PageHelper.startPage(page, pageSize, false);
        List<PayTransPo> poList = payTransPoMapper.selectByExample(example);
        if (poList.size() > 0) {
            ret = poList.stream().map(po -> this.getBo(po)).collect(Collectors.toList());
        }else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺支付" , shopChannelId));
        }
        return new PageInfo<>(ret);
    }

    public PayTrans findByOutNo(String outNo){
        if (null != outNo) {
            PayTrans ret = null;
            PayTransPoExample example = new PayTransPoExample();
            PayTransPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutNoEqualTo(outNo);
            List<PayTransPo> poList = this.payTransPoMapper.selectByExample(example);

            if(poList.isEmpty()) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "支付交易", Integer.parseInt((outNo))));
            }

            return getBo(poList.get(0));
        }
        else{
            throw new BusinessException(ReturnNo.PARAMETER_MISSED);
        }
    }

    public List<PayTrans> retrieveBySuccessTimeBetween(LocalDateTime beginTime, LocalDateTime endTime) throws RuntimeException {
        List<PayTrans> ret = null;

        PayTransPoExample example = new PayTransPoExample();
        PayTransPoExample.Criteria criteria = example.createCriteria();
        criteria.andSuccessTimeBetween(beginTime, endTime);
        List<PayTransPo> poList = this.payTransPoMapper.selectByExample(example);

        ret = poList.stream().map(this::getBo).collect(Collectors.toList());
        return ret;
    }
}
