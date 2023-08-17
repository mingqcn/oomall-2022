package cn.edu.xmu.oomall.payment.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.dao.*;
import cn.edu.xmu.oomall.payment.dao.bo.*;
import cn.edu.xmu.oomall.payment.service.channel.PayAdaptorFactory;
import cn.edu.xmu.oomall.payment.service.channel.PayAdaptor;
import cn.edu.xmu.oomall.payment.service.channel.dto.GetDivRefundAdaptorDto;
import cn.edu.xmu.oomall.payment.service.channel.dto.PostRefundAdaptorDto;
import cn.edu.xmu.oomall.payment.service.dto.DivRefundTransDto;
import cn.edu.xmu.oomall.payment.service.dto.RefundTransDto;
import cn.edu.xmu.oomall.payment.service.dto.SimpleChannelDto;
import cn.edu.xmu.oomall.payment.service.dto.SimpleUserDto;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.*;
import static cn.edu.xmu.javaee.core.util.Common.*;

/**
 * 退款服务
 */
@Service
public class RefundService {

    private static  final Logger logger = LoggerFactory.getLogger(RefundService.class);

    private DivRefundTransDao divRefundTransDao;
    private ShopChannelDao shopChannelDao;
    private PayTransDao payTransDao;
    private RefundTransDao refundTransDao;
    private PayAdaptorFactory factory;


    @Autowired
    public RefundService(PayAdaptorFactory factory, DivRefundTransDao divRefundTransDao, ShopChannelDao shopChannelDao, PayTransDao payTransDao, RefundTransDao refundTransDao) {
        this.divRefundTransDao = divRefundTransDao;
        this.shopChannelDao = shopChannelDao;
        this.payTransDao = payTransDao;
        this.refundTransDao = refundTransDao;
        this.factory = factory;
    }

    private RefundTransDto getDto(RefundTrans refund, ShopChannel shopChannel) {
        RefundTransDto dto = cloneObj(refund, RefundTransDto.class);
        if (null != shopChannel)
            dto.setChannel(cloneObj(shopChannel.getChannel(), SimpleChannelDto.class));
        dto.setCreator(new SimpleUserDto(refund.getCreatorId(), refund.getCreatorName()));
        dto.setModifier(new SimpleUserDto(refund.getModifierId(), refund.getModifierName()));
        dto.setAdjustor(new SimpleUserDto(refund.getAdjustId(), refund.getAdjustName()));
        return dto;
    }


    @Transactional
    public PageDto<DivRefundTransDto> retrieveDivRefunds(Long shopId, LocalDateTime beginTime, LocalDateTime endTime, Long shopChannelId, Integer page, Integer pageSize) {
        ShopChannel shopChannel = null;
        try {
            logger.debug("retrieveDivRefunds: shopId = {}, shopChannelId = {}", shopId, shopChannelId);
            shopChannel = this.shopChannelDao.findById(shopChannelId);
        }catch (BusinessException e) {
            if (e.getErrno() == ReturnNo.RESOURCE_ID_NOTEXIST) {
                return new PageDto<>(new ArrayList<>(), 0, 0);
            }else {
                throw e;
            }
        }

        if (null == shopChannel){
            return new PageDto<>(new ArrayList<>(), 0, 0);
        }

        if (shopId != shopChannel.getShopId()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺渠道", shopChannelId, shopId));
        }

        PageInfo<DivRefundTrans> refundPage = this.divRefundTransDao.retrieveByShopChannelId(shopChannel.getId(), beginTime, endTime, page, pageSize);
        return new PageDto<>(refundPage.getList().stream().map(divrefund -> {
            DivRefundTransDto dto = cloneObj(divrefund, DivRefundTransDto.class);
            dto.setChannel(cloneObj(divrefund.getShopChannel().getChannel(), SimpleChannelDto.class));
            return dto;
        }).collect(Collectors.toList()), page, pageSize);

    }

    /**
     * 退款
     * @author Ming Qiu
     * <p>
     * date: 2022-11-12 19:58
     * @param shopId
     * @param paymentId
     * @param amount
     * @param user
     * @return
     */
    @Transactional
    public RefundTransDto createRefund(Long shopId, Long paymentId, Long amount, long divAmount, UserDto user) {
        //获得支付交易
        PayTrans payTrans = this.payTransDao.findById(paymentId);
        logger.debug("createRefund: payTrans = {}",payTrans);
        try {
            PayTrans temp = new PayTrans();
            temp.setId(payTrans.getId());
            temp.setInRefund(PayTrans.REFUNDING);
            clearFields(temp, "id", "inRefund");
            this.payTransDao.saveById(temp, null);
        }catch (BusinessException e){
            if  (ReturnNo.RESOURCE_ID_NOTEXIST == e.getErrno()){
                logger.error("createRefund: 回写支付对象失败。");
                throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR);
            }else{
                throw e;
            }
        }

        //判断支付交易是否是同一商铺
        ShopChannel shopChannel = payTrans.getShopChannel();
        if (PLATFORM != shopId && shopId != shopChannel.getShopId()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "退款对应的支付交易", paymentId, shopId));
        }

        //创建退款交易
        RefundTrans newTrans = new RefundTrans(shopChannel, payTrans, amount, divAmount);

        this.refundTransDao.save(newTrans, user);

        RefundTrans refundTrans;
        try {
            refundTrans = this.refundTransDao.findById(newTrans.getId());
        }catch (BusinessException e){
            if  (ReturnNo.RESOURCE_ID_NOTEXIST == e.getErrno()){
                logger.error("createRefund: 回写退款对象失败。");
                throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR);
            }else{
                throw e;
            }
        }

        PayAdaptor payAdaptor = this.factory.createPayAdaptor(shopChannel);

        //查询是否分账
        if (PayTrans.DIV == payTrans.getStatus()) {
            DivPayTrans divPayTrans = payTrans.getDivTrans();
            //需要先调用分账回退API
            DivRefundTrans divRefundTrans = new DivRefundTrans(refundTrans, divPayTrans, shopChannel);
            this.divRefundTransDao.save(divRefundTrans, user);
            DivRefundTrans newDivRefundTrans;
            try{
                newDivRefundTrans = this.divRefundTransDao.findById(divRefundTrans.getId());
            }catch (BusinessException e){
                if  (ReturnNo.RESOURCE_ID_NOTEXIST == e.getErrno()){
                    logger.error("createRefund: 回写分账回退对象失败。");
                    throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR);
                }else{
                    throw e;
                }
            }

            GetDivRefundAdaptorDto dto = payAdaptor.returnDivRefund(newDivRefundTrans);
            newDivRefundTrans.setTransNo(dto.getTransNo());
            newDivRefundTrans.setStatus(dto.getStatus());
            newDivRefundTrans.setAmount(dto.getAmount());
            newDivRefundTrans.setSuccessTime(dto.getSuccessTime());
            try {
                this.divRefundTransDao.saveById(newDivRefundTrans, null);
            }catch (BusinessException e){
                if  (ReturnNo.RESOURCE_ID_NOTEXIST == e.getErrno()){
                    logger.error("createRefund: 回写分账回退对象失败。");
                    throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR);
                }else{
                    throw e;
                }
            }
        }

        //内部交易号一律设置为交易的id，方便对账
        refundTrans.setOutNo(newTrans.getId().toString());
        PostRefundAdaptorDto dto = payAdaptor.createRefund(newTrans);

        refundTrans.setTransNo(dto.getTransNo());
        refundTrans.setAmount(dto.getAmount());
        refundTrans.setUserReceivedAccount(dto.getUserReceivedAccount());
        refundTrans.setSuccessTime(dto.getSuccessTime());
        refundTrans.setStatus(dto.getStatus());

        try {
            this.refundTransDao.saveById(refundTrans, null);
        }catch (BusinessException e){
            if  (ReturnNo.RESOURCE_ID_NOTEXIST == e.getErrno()){
                logger.error("createRefund: 回写退款对象失败。");
                throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR);
            }else{
                throw e;
            }
        }
//        return cloneObj(refundTrans, RefundTransDto.class);
        return getDto(refundTrans, shopChannel);
    }

    /**
     * 根据id返回退款对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-12 21:12
     * @param shopId
     * @param id
     * @return
     */
    @Transactional
    public RefundTransDto findRefundById(Long shopId, Long id) {
        RefundTrans refund = this.refundTransDao.findById(id);

        ShopChannel shopChannel = refund.getShopChannel();
        if (PLATFORM != shopId && shopId != shopChannel.getShopId()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "退款交易", id, shopId));
        }

        return getDto(refund, shopChannel);
    }

    /**
     * 取消退款
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:47
     * @param shopId
     * @param id
     * @param user
     */
    @Transactional
    public void cancelRefundById(Long shopId, Long id, UserDto user) {
        RefundTrans refund = this.refundTransDao.findById(id);
        ShopChannel shopChannel = refund.getShopChannel();
        if (PLATFORM != shopId && shopId != shopChannel.getShopId()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "退款交易", id, shopId));
        }

        if (!refund.allowStatus(RefundTrans.CANCEL)){
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), refund.getStatusName()));
        }

        RefundTrans updateObj = new RefundTrans();
        updateObj.setId(id);
        updateObj.setStatus(RefundTrans.CANCEL);
        this.refundTransDao.saveById(updateObj, user);
    }
}
