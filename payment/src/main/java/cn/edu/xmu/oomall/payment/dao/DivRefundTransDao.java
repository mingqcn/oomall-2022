//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.dao.bo.*;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.dao.bo.DivRefundTrans;
import cn.edu.xmu.oomall.payment.mapper.generator.DivRefundTransPoMapper;
import cn.edu.xmu.oomall.payment.mapper.generator.po.*;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class DivRefundTransDao {

    private static  final Logger logger = LoggerFactory.getLogger(DivRefundTransDao.class);


    private DivRefundTransPoMapper divRefundTransPoMapper;

    private RefundTransDao refundTransDao;

    private ShopChannelDao shopChannelDao;

    @Autowired
    @Lazy
    public DivRefundTransDao(DivRefundTransPoMapper divRefundTransPoMapper, RefundTransDao refundTransDao, ShopChannelDao shopChannelDao) {
        this.divRefundTransPoMapper = divRefundTransPoMapper;
        this.refundTransDao = refundTransDao;
        this.shopChannelDao = shopChannelDao;
    }

    /**
     * 获得bo对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-06 23:20
     * @param po
     * @return
     */
    private DivRefundTrans getBo(DivRefundTransPo po) {
        DivRefundTrans ret;
        logger.debug("getBo po = {}", po);
        ret = cloneObj(po, DivRefundTrans.class);
        ret.setRefundTransDao(this.refundTransDao);
        ret.setShopChannelDao(this.shopChannelDao);
        logger.debug("getBo ret = {}", ret);
        return ret;
    }

    /**
     * 由id返回分账退回对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-06 23:13
     * @param id 分账退回对象id
     * @return 账退回对象
     * @throws RuntimeException
     * @throws BusinessException 无此对象
     */
    public DivRefundTrans findById(Long id) throws RuntimeException{
        DivRefundTrans ret = null;
        if (null != id) {
            DivRefundTransPo po = divRefundTransPoMapper.selectByPrimaryKey(id);
            if (null == po) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "分账回退", id));
            }

            ret = this.getBo(po);
        }
        return ret;
    }

    /**
     * 根据退款交易id查找分账退回对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-06 23:22
     * @param id 退款交易id
     * @return 分账退回对象，无则返回null
     * @throws RuntimeException
     */
    public DivRefundTrans findByRefundTransId(Long id) throws RuntimeException{
        DivRefundTrans ret = null;
        if (null != id) {
            DivRefundTransPoExample example = new DivRefundTransPoExample();
            DivRefundTransPoExample.Criteria criteria= example.createCriteria();
            criteria.andRefundTransIdEqualTo(id);
            PageHelper.startPage(1,1,false);
            List<DivRefundTransPo> poList = divRefundTransPoMapper.selectByExample(example);
            if (poList.size() > 0){
                ret = this.getBo(poList.get(0));
            }
        }
        return ret;
    }

    /**
     * 查询商铺渠道的退款回帐记录
     * @author Ming Qiu
     * <p>
     * date: 2022-11-12 23:17
     * @param shopChannelId
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    public PageInfo<DivRefundTrans> retrieveByShopChannelId(Long shopChannelId, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize) throws RuntimeException {

        List<DivRefundTrans> boList = null;
        logger.debug("retrieveObjByShopChannelId: shopChannelId = {}, beginTime = {}, endTime = {}, page = {}, pageSize = {}", shopChannelId, beginTime, endTime, page, pageSize);

        DivRefundTransPoExample example = new DivRefundTransPoExample();
        DivRefundTransPoExample.Criteria criteria = example.createCriteria();
        criteria.andShopChannelIdEqualTo(shopChannelId);
        criteria.andSuccessTimeBetween(beginTime, endTime);
        PageHelper.startPage(page,pageSize,false);
        List<DivRefundTransPo> poList = divRefundTransPoMapper.selectByExample(example);
        if (poList.size() > 0) {
            boList = poList.stream().map(po -> this.getBo(po)).collect(Collectors.toList());
        }else {
            boList = new ArrayList<>();
        }
        logger.debug("retrieveObjByShopChannelId: boList = {}", boList);
        return new PageInfo<>(boList);
    }

    /**
     * 插入分账回退对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 20:28
     * @param divRefundTrans
     * @throws RuntimeException
     */
    public void save(DivRefundTrans divRefundTrans, UserDto userDto) throws RuntimeException{
        DivRefundTransPo po = cloneObj(divRefundTrans, DivRefundTransPo.class);
        putUserFields(po, "creator",userDto);
        putGmtFields(po, "create");
        this.divRefundTransPoMapper.insertSelective(po);
        divRefundTrans.setId(po.getId());
    }

    /**
     * 更新分账退回对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 20:46
     * @param divRefundTrans
     * @param userDto
     * @throws RuntimeException
     */
    public void saveById(DivRefundTrans divRefundTrans, UserDto userDto) throws RuntimeException{
        DivRefundTransPo po = cloneObj(divRefundTrans, DivRefundTransPo.class);
        if (null != userDto) {
            putUserFields(po, "modifier", userDto);
            putGmtFields(po, "modified");
        }
        int ret = this.divRefundTransPoMapper.updateByPrimaryKeySelective(po);
        if (0 == ret) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "分账退回", divRefundTrans.getId()));
        }
    }

    /**
     * 根据分账的id查询分账退回对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-15 15:17
     * @param divPayTransId
     * @return
     * @throws RuntimeException
     */
    public List<DivRefundTrans> retrieveByDivPayTransId(Long divPayTransId) throws RuntimeException{
        List<DivRefundTrans> ret = null;
        if ( null != divPayTransId) {
            DivRefundTransPoExample example = new DivRefundTransPoExample();
            DivRefundTransPoExample.Criteria criteria = example.createCriteria();
            criteria.andDivPayTransIdEqualTo(divPayTransId);
            PageHelper.startPage(1,MAX_RETURN,false);
            List<DivRefundTransPo> poList = divRefundTransPoMapper.selectByExample(example);
            if (poList.size() > 0 ){
                ret = poList.stream().map(po -> getBo(po)).collect(Collectors.toList());
            }else {
                ret = new ArrayList<>(0);
            }
        }
        return null;
    }
    public void saveById(Long id, UserDto user, LocalDateTime adjustTime) throws BusinessException{
        int ret = 0;
        if (null != id) {
            DivRefundTransPo po = new DivRefundTransPo();
            po.setId(id);
            //修改交易状态
            putUserFields(po, "adjust", user);
            putUserFields(po, "modifier", user);
            putGmtFields(po, "Modified");
            po.setAdjustTime(adjustTime);

            ret = this.divRefundTransPoMapper.updateByPrimaryKeySelective(po);
            if (0 == ret) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "分账退回", id));
            }
        }
    }

    public List<DivRefundTrans> retrieveBetweenSuccessTimeAndShopChannelIdEqualsId(LocalDateTime beginTime, LocalDateTime endTime,Long shopChannel) {
        //根据支付完成时间来筛选交易
        DivRefundTransPoExample example = new DivRefundTransPoExample();
        DivRefundTransPoExample.Criteria criteria = example.createCriteria();
        criteria.andSuccessTimeBetween(beginTime,endTime).andShopChannelIdEqualTo(shopChannel);

        List<DivRefundTransPo> divPayTransPos = divRefundTransPoMapper.selectByExample(example);

        List<DivRefundTrans>divRefundTrans = new ArrayList<DivRefundTrans>();
        for(DivRefundTransPo po:divPayTransPos)
            divRefundTrans.add(cloneObj(po,DivRefundTrans.class));
        return divRefundTrans;
    }
}
