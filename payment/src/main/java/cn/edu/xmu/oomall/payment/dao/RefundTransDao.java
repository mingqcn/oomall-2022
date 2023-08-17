//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.dao.bo.DivRefundTrans;
import cn.edu.xmu.oomall.payment.dao.bo.PayTrans;
import cn.edu.xmu.oomall.payment.dao.bo.RefundTrans;
import cn.edu.xmu.oomall.payment.mapper.generator.RefundTransPoMapper;
import cn.edu.xmu.oomall.payment.mapper.generator.po.RefundTransPo;
import cn.edu.xmu.oomall.payment.mapper.generator.po.RefundTransPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;
import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class RefundTransDao {
    private static final Logger logger = LoggerFactory.getLogger(PayTransDao.class);

    private PayTransDao payTransDao;

    private RefundTransPoMapper refundTransPoMapper;
    private ShopChannelDao shopChannelDao;
    private DivRefundTransDao divRefundTransDao;

    @Autowired
    @Lazy
    public RefundTransDao(PayTransDao payTransDao, RefundTransPoMapper refundTransPoMapper, ShopChannelDao shopChannelDao, DivRefundTransDao divRefundTransDao) {
        this.payTransDao = payTransDao;
        this.refundTransPoMapper = refundTransPoMapper;
        this.shopChannelDao = shopChannelDao;
        this.divRefundTransDao = divRefundTransDao;
    }

    private RefundTrans getBo(RefundTransPo po) {
        RefundTrans ret;
        ret = cloneObj(po, RefundTrans.class);
        ret.setPayTransDao(this.payTransDao);
        ret.setShopChannelDao(this.shopChannelDao);
        ret.setDivRefundTransDao(this.divRefundTransDao);
        return ret;
    }

    /**
     * 根据支付交易的id，查找对应的退款交易
     * @author Ming Qiu
     * <p>
     * date: 2022-11-06 21:56
     * @param id 支付交易id
     * @return 退款交易对象， 无则返回null
     * @throws RuntimeException
     */
    public PageInfo<RefundTrans> retrieveByPayTransId(Long id, Integer page, Integer pageSize) throws RuntimeException{
        List<RefundTrans> ret = null;

        if (null != id) {
            RefundTransPoExample example = new RefundTransPoExample();
            RefundTransPoExample.Criteria criteria = example.createCriteria();
            criteria.andPayTransIdEqualTo(id);
            PageHelper.startPage(page, pageSize, false);
            List<RefundTransPo> poList = this.refundTransPoMapper.selectByExample(example);
            if (poList.size() > 0) {
                ret = poList.stream().map(po -> this.getBo(po)).collect(Collectors.toList());
            }else{
                ret = new ArrayList<>();
            }
        }
        return new PageInfo<>(ret);
    }

    /**
     * 根据id返回退款交易对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-06 23:27
     * @param id
     * @return
     * @throws RuntimeException
     * @throws BusinessException 无此对象
     */
    public RefundTrans findById(Long id) throws RuntimeException{
        RefundTrans ret = null;
        if (null != id ){
            RefundTransPo po = this.refundTransPoMapper.selectByPrimaryKey(id);
            if (null == po){
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "退款交易", id));
            }
            ret = getBo(po);
        }
        return ret;
    }


    /**
     * 插入对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-12 20:05
     * @param trans
     * @param user
     * @throws RuntimeException
     */
    public void save(RefundTrans trans, UserDto user) throws RuntimeException {
        logger.debug("insertObj: obj = {}", trans);
        RefundTransPo po = cloneObj(trans, RefundTransPo.class);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");

        refundTransPoMapper.insertSelective(po);
        trans.setId(po.getId());
    }

    public void saveById(RefundTrans trans, UserDto user) throws RuntimeException {
        RefundTransPo po = cloneObj(trans, RefundTransPo.class);
        if (null != user) {
            putUserFields(po, "modifier", user);
            putGmtFields(po, "modified");
        }

        int ret = refundTransPoMapper.updateByPrimaryKeySelective(po);
        if (0 == ret) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"退款交易", po.getId()));
        }
    }
    public void saveById(Long id, UserDto user, LocalDateTime adjustTime) throws RuntimeException{
        if (null != id) {
            RefundTransPo po = new RefundTransPo();
            po.setId(id);

            //修改交易状态
            putUserFields(po, "adjust", user);
            putUserFields(po, "modifier", user);
            putGmtFields(po, "Modified");
            po.setAdjustTime(adjustTime);
            po.setStatus(RefundTrans.CHECKED);

            int ret = this.refundTransPoMapper.updateByPrimaryKeySelective(po);
            if (0 == ret) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "退回", id));
            }
        }
    }

    public List<RefundTrans> retrieveBetweenSuccessTimeAndShopChannelIdEqualsIdAndStatusEqualsSUCCESS(LocalDateTime beginTime, LocalDateTime endTime,long shopChannel) {
        //根据支付完成时间来筛选交易
        RefundTransPoExample example = new RefundTransPoExample();
        RefundTransPoExample.Criteria criteria = example.createCriteria();
        criteria.andSuccessTimeBetween(beginTime,endTime).andStatusEqualTo(RefundTrans.SUCCESS).andShopChannelIdEqualTo(shopChannel);

        List<RefundTransPo> refundTransPos = refundTransPoMapper.selectByExample(example);

        List<RefundTrans>refundTrans = new ArrayList<RefundTrans>();
        for(RefundTransPo po:refundTransPos)
            refundTrans.add(cloneObj(po,RefundTrans.class));
        return refundTrans;
    }

    public void saveById(Long id, Byte Status,UserDto userDto) {
        if(id!=null&&Status!=null) {
            RefundTransPo po = new RefundTransPo();
            po.setId(id);
            po.setStatus(Status);
            putUserFields(po, "modifier", userDto);
            putGmtFields(po, "Modified");
            int ret = refundTransPoMapper.updateByPrimaryKeySelective(po);
            if (0 == ret) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "退回", id));
            }
        }
    }
}
