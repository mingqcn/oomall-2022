//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.dao.bo.DivPayTrans;
import cn.edu.xmu.oomall.payment.dao.bo.DivRefundTrans;
import cn.edu.xmu.oomall.payment.dao.bo.PayTrans;
import cn.edu.xmu.oomall.payment.mapper.generator.DivPayTransPoMapper;
import cn.edu.xmu.oomall.payment.mapper.generator.po.*;
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
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class DivPayTransDao {

    private static final Logger  logger = LoggerFactory.getLogger(DivPayTransDao.class);

    private  PayTransDao payTransDao;

    private DivPayTransPoMapper divPayTransPoMapper;

    private DivRefundTransDao divRefundTransDao;

    @Autowired
    @Lazy
    public DivPayTransDao(PayTransDao payTransDao, DivRefundTransDao divRefundTransDao, DivPayTransPoMapper divPayTransPoMapper) {
        this.payTransDao = payTransDao;
        this.divPayTransPoMapper = divPayTransPoMapper;
        this.divRefundTransDao = divRefundTransDao;
    }

    /**
     * 由po对象构造bo对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-06 22:59
     * @param po
     * @return
     */
    private DivPayTrans getBo(DivPayTransPo po) {
        DivPayTrans ret;
        ret = cloneObj(po, DivPayTrans.class);
        ret.setPayTransDao(this.payTransDao);
        ret.setDivRefundTransDao(this.divRefundTransDao);
        return ret;
    }

    /**
     * 用id返回DivPayTrans对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-06 23:00
     * @param id 分账交易对象id
     * @return 分账交易对象
     * @throws RuntimeException
     * @throws BusinessException 无此对象时
     */
    public DivPayTrans findById(Long id) throws RuntimeException{
        DivPayTrans ret = null;
        if (null != id) {
            DivPayTransPo po = divPayTransPoMapper.selectByPrimaryKey(id);
            if (null == po) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "分账交易", id));
            }
            ret = getBo(po);
        }
        return ret;
    }


    /**
     * 查找支付交易对象对应的分账对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-06 23:01
     * @param id 支付交易对象id
     * @return 分账交易对象，无则返回null
     * @throws RuntimeException
     */
    public DivPayTrans retrieveByPayTransId(Long id) throws RuntimeException{
        DivPayTrans ret = null;
        if (null != id) {
            DivPayTransPoExample example = new DivPayTransPoExample();
            DivPayTransPoExample.Criteria criteria = example.createCriteria();
            criteria.andPayTransIdEqualTo(id);
            PageHelper.startPage(1, 1, false);
            List<DivPayTransPo> poList = divPayTransPoMapper.selectByExample(example);
            if (0 == poList.size()) {
                ret = null;
            } else {
                ret = cloneObj(poList.get(0), DivPayTrans.class);
                ret.setPayTransDao(this.payTransDao);
            }
        }
        return ret;
    }

    public void saveById(Long id, UserDto user, LocalDateTime adjustTime) throws BusinessException{
        if (null != id) {
            DivPayTransPo po = new DivPayTransPo();
            po.setId(id);
            //修改交易状态
            putUserFields(po, "adjust", user);
            putUserFields(po, "modifier", user);
            putGmtFields(po, "Modified");
            po.setAdjustTime(adjustTime);

            int ret = this.divPayTransPoMapper.updateByPrimaryKeySelective(po);
            if (0 == ret) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "分账支付", id));
            }
        }
    }

    public List<DivPayTrans> retrieveBetweenSuccessTimeAndShopChannelIdEqualsId(LocalDateTime beginTime, LocalDateTime endTime,Long shopChannel) {
        //根据支付完成时间来筛选交易
        DivPayTransPoExample example = new DivPayTransPoExample();
        DivPayTransPoExample.Criteria criteria = example.createCriteria();
        criteria.andSuccessTimeBetween(beginTime,endTime).andShopChannelIdEqualTo(shopChannel);

        List<DivPayTransPo> divPayTransPos = divPayTransPoMapper.selectByExample(example);;

        List<DivPayTrans>divPayTrans = new ArrayList<DivPayTrans>();
        for(DivPayTransPo po:divPayTransPos)
            divPayTrans.add(cloneObj(po,DivPayTrans.class));
        return divPayTrans;
    }

    public PageInfo<DivPayTrans> retrieveByShopChannelIdAndSuccessTimeBetween(Long shopChannelId, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) throws BusinessException{
        List<DivPayTrans> ret = null;

        DivPayTransPoExample example = new DivPayTransPoExample();
        DivPayTransPoExample.Criteria criteria = example.createCriteria();
        criteria.andShopChannelIdEqualTo(shopChannelId);
        criteria.andSuccessTimeBetween(beginTime, endTime);
        PageHelper.startPage(page, pageSize, false);
        List<DivPayTransPo> poList = divPayTransPoMapper.selectByExample(example);
        if (poList.size() > 0) {
            ret = poList.stream().map(po -> this.getBo(po)).collect(Collectors.toList());
        }else{
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商铺支付" , shopChannelId));
        }
        return new PageInfo<>(ret);
    }


}
