package cn.edu.xmu.oomall.wechatpay.service;

import cn.edu.xmu.javaee.core.util.Common;
import cn.edu.xmu.oomall.wechatpay.dao.WeChatPayDao;
import cn.edu.xmu.oomall.wechatpay.model.bo.*;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayDivPayTransPo;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayReceiverPo;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayRefundPo;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayTransactionPo;
import cn.edu.xmu.oomall.wechatpay.model.vo.*;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayReturnNo;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayReturnObject;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

//import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneObj;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

/**
 * @author ziyi guo
 * @date 2021/12/1
 * @Modifier
 * @data 2022/11/30
 */
@Service
public class WeChatPayService {

    private static final String TRADE_STATE_SUCCESS = "SUCCESS";
    private static final String TRADE_STATE_FAIL = "NOTPAY";
    private static final String TRADE_STATE_CLOSED = "CLOSED";
    private static final String TRADE_STATE_REFUND = "REFUND";
    private static final String REFUND_STATUS_SUCCESS = "SUCCESS";
    private static final String REFUND_STATUS_FAIL = "ABNORMAL";
    private static final String DIVPAY_STATE_FINISHED = "FINISHED";
    private static final String DIVPAY_STATE_PROCESSING = "PROCESSING";
    private static final String DIVREFUND_STATUS_SUCCESS = "SUCCESS";

    private WeChatPayDao weChatPayDao;

    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    public WeChatPayService(WeChatPayDao weChatPayDao, RocketMQTemplate rocketMQTemplate){
        this.weChatPayDao = weChatPayDao;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Transactional(rollbackFor=Exception.class)
    public WeChatPayReturnObject createTransaction(WeChatPayTransaction weChatPayTransaction){

        WeChatPayTransaction transaction = (WeChatPayTransaction) weChatPayDao.getTransactionByOutTradeNo(weChatPayTransaction.getOutTradeNo()).getData();
        if(transaction!=null){
            if(transaction.getTradeState().equals(TRADE_STATE_SUCCESS)){
                return new WeChatPayReturnObject(WeChatPayReturnNo.ORDER_PAID);
            }
            if(transaction.getTradeState().equals(TRADE_STATE_CLOSED)){
                return new WeChatPayReturnObject(WeChatPayReturnNo.ORDER_CLOSED);
            }
            return new WeChatPayReturnObject(WeChatPayReturnNo.OUT_TRADE_NO_USED);
        }

        int random = (int)(Math.random()*4);
        switch (random)
        {
            case 0:
            {
                WeChatPayReturnObject returnObject = paySuccess(weChatPayTransaction);
                if(returnObject.getData()!=null) {
                    rocketMQTemplate.sendOneWay("wechatpaypayment-topic", MessageBuilder.withPayload( new WeChatPayPaymentNotifyRetVo((WeChatPayTransaction)returnObject.getData()) ).build());
                }
                break;
            }
            case 1:
            {
                paySuccess(weChatPayTransaction);
                break;
            }
            case 2:
            {
                WeChatPayReturnObject returnObject = payFail(weChatPayTransaction);
                if(returnObject.getData()!=null) {
                    rocketMQTemplate.sendOneWay("wechatpaypayment-topic", MessageBuilder.withPayload( new WeChatPayPaymentNotifyRetVo((WeChatPayTransaction)returnObject.getData()) ).build());
                }
                break;
            }
            case 3:
            {
                payFail(weChatPayTransaction);
                break;
            }
            default:
                break;
        }
        return new WeChatPayReturnObject(new WeChatPayPrepayRetVo());
    }


    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public WeChatPayReturnObject getTransaction(String outTradeNo){
        WeChatPayReturnObject returnObject = weChatPayDao.getTransactionByOutTradeNo(outTradeNo);
        return returnObject;
    }


    @Transactional(rollbackFor=Exception.class)
    public WeChatPayReturnObject closeTransaction(String outTradeNo){
        WeChatPayTransactionPo weChatPayTransactionPo = new WeChatPayTransactionPo();
        weChatPayTransactionPo.setOutTradeNo(outTradeNo);
        weChatPayTransactionPo.setTradeState(TRADE_STATE_CLOSED);
        WeChatPayReturnObject returnObject = weChatPayDao.updateTransactionByOutTradeNo(weChatPayTransactionPo);
        return returnObject;
    }


    @Transactional(rollbackFor=Exception.class)
    public WeChatPayReturnObject createRefund(WeChatPayRefund weChatPayRefund){

        if(weChatPayRefund.getRefund()<=0){
            return new WeChatPayReturnObject(WeChatPayReturnNo.REFUND_AMOUNT_ERROR);
        }

        WeChatPayRefund refund = (WeChatPayRefund) weChatPayDao.getRefundByOutRefundNo(weChatPayRefund.getOutRefundNo()).getData();
        if(refund!=null){
            return new WeChatPayReturnObject(WeChatPayReturnNo.OUT_REFUND_NO_USED);
        }

        WeChatPayTransaction transaction = (WeChatPayTransaction) weChatPayDao.getTransactionByOutTradeNo(weChatPayRefund.getOutTradeNo()).getData();
        if(transaction==null){
            return new WeChatPayReturnObject(WeChatPayReturnNo.RESOURCE_NOT_EXISTS);
        }
        if( transaction.getTradeState().equals(TRADE_STATE_CLOSED)||transaction.getTradeState().equals(TRADE_STATE_FAIL) ){
            return new WeChatPayReturnObject(WeChatPayReturnNo.REFUND_TRANSACTION_ERROR);
        }
        weChatPayRefund.setPayerTotal(transaction.getPayerTotal());

        List<WeChatPayRefundPo> list = (List<WeChatPayRefundPo>) weChatPayDao.getRefundByOutTradeNo(weChatPayRefund.getOutTradeNo()).getData();
        int totalRefund=0;
        if(list!=null){
            for(WeChatPayRefundPo po:list){
                totalRefund += po.getRefund();
            }
        }
        if(totalRefund + weChatPayRefund.getRefund() > transaction.getPayerTotal()){
            return new WeChatPayReturnObject(WeChatPayReturnNo.REFUND_AMOUNT_ERROR);
        }

        WeChatPayReturnObject returnObject = null;
        int random = (int)(Math.random()*4);
        switch (random)
        {
            case 0:
            {
                returnObject = refundSuccess(weChatPayRefund);
                if(returnObject.getData()!=null) {
                    rocketMQTemplate.sendOneWay("wechatpayrefund-topic", MessageBuilder.withPayload( new WeChatPayRefundNotifyRetVo((WeChatPayRefund)returnObject.getData()) ).build());
                }
                break;
            }
            case 1:
            {
                returnObject = refundSuccess(weChatPayRefund);
                break;
            }
            case 2:
            {
                returnObject = refundFail(weChatPayRefund);
                if(returnObject.getData()!=null) {
                    rocketMQTemplate.sendOneWay("wechatpayrefund-topic", MessageBuilder.withPayload( new WeChatPayRefundNotifyRetVo((WeChatPayRefund)returnObject.getData()) ).build());
                }
                break;
            }
            case 3:
            {
                returnObject = refundFail(weChatPayRefund);
                break;
            }
            default:
                break;
        }
        return returnObject;
    }


    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public WeChatPayReturnObject getRefund(String outRefundNo){
        WeChatPayReturnObject returnObject = weChatPayDao.getRefundByOutRefundNo(outRefundNo);
        return returnObject;
    }

    @Transactional(rollbackFor=Exception.class)
    public WeChatPayReturnObject createDivRefundTrans(WeChatPayDivRefundTrans weChatPayDivRefundTrans){
        WeChatPayDivPayTrans payTrans = (WeChatPayDivPayTrans) weChatPayDao.getDivPayTransByOutOrderNo(weChatPayDivRefundTrans.getOutOrderNo()).getData();
        if(payTrans==null){
            return new WeChatPayReturnObject(WeChatPayReturnNo.RESOURCE_NOT_EXISTS);
        }
        WeChatPayDivRefundTrans refundTrans = (WeChatPayDivRefundTrans) weChatPayDao.getDivRefundTransByOutReturnNo(weChatPayDivRefundTrans.getOutReturnNo()).getData();
        if(refundTrans !=null){
            return new WeChatPayReturnObject(WeChatPayReturnNo.OUT_DIVREFUND_NO_USED);
        }
        //可加随机
        WeChatPayReturnObject returnObject = divRefundSuccess(weChatPayDivRefundTrans);
        return returnObject;
    }

    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public WeChatPayReturnObject getDivRefundTransByOutReturnNo(String outReturnNo){
        WeChatPayReturnObject returnObject = weChatPayDao.getDivRefundTransByOutReturnNo(outReturnNo);
        return returnObject;
    }
    @Transactional(rollbackFor=Exception.class)
    public WeChatPayReturnObject createDivPayTrans(WeChatPayDivPayTrans weChatPayDivPayTrans){
        //查找支付单
        WeChatPayTransaction transaction = (WeChatPayTransaction) weChatPayDao.getTransactionByOutTradeNo(weChatPayDivPayTrans.getTransactionId()).getData();
        if(transaction==null){
            return new WeChatPayReturnObject(WeChatPayReturnNo.RESOURCE_NOT_EXISTS);
        }
        else{
            if(transaction.getTradeState().equals(TRADE_STATE_FAIL)){//没有支付
                return new WeChatPayReturnObject(WeChatPayReturnNo.REFUND_TRANSACTION_ERROR);
            }
            if(transaction.getTradeState().equals(TRADE_STATE_CLOSED)){//关闭
                return new WeChatPayReturnObject(WeChatPayReturnNo.ORDER_CLOSED);
            }
        }
        //查找分账单
        WeChatPayDivPayTrans divPayTrans = (WeChatPayDivPayTrans) weChatPayDao.getDivPayTransByOutOrderNo(weChatPayDivPayTrans.getOutOrderNo()).getData();
        if(divPayTrans!=null){
            return new WeChatPayReturnObject(WeChatPayReturnNo.OUT_DIVPAY_NO_USED);
        }

        boolean flagState = true;
        int availTotal = transaction.getTotal() * 3 / 10;
        int tempTotal = 0;

        for(WeChatPayDivReceiverVo receiver: weChatPayDivPayTrans.getReceivers()){
            WeChatPayDivReceiverRetVo weChatPayDivReceiverRetVo = new WeChatPayDivReceiverRetVo(receiver);
            //weChatPayDivReceiverRetVo.setCreateTime(LocalDateTime.now().toString());
            weChatPayDivReceiverRetVo.setCreateTime("2015-05-20T13:29:35+08:00");
            //weChatPayDivReceiverRetVo.setDetailId(Common.genSeqNum(32));
            weChatPayDivReceiverRetVo.setDetailId("202211292022236EQ5");
            //判断并且设定分账结果、分析失败原因
            tempTotal+=receiver.getAmount();
            if(tempTotal>availTotal){
                return new WeChatPayReturnObject(WeChatPayReturnNo.NOT_ENOUGH);
            }

            WeChatPayReceiver newReceiver = (WeChatPayReceiver) weChatPayDao.getReceiverByAccount(receiver.getAccount()).getData();
            if(newReceiver==null){
                weChatPayDivReceiverRetVo.setResult("PENDING");
                weChatPayDivReceiverRetVo.setFailReason("NO_RELATION");
                flagState = false;
            }
            else{
                weChatPayDivReceiverRetVo.setResult("SUCCESS");
                weChatPayDivReceiverRetVo.setFailReason("NULL");
            }
            //weChatPayDivReceiverRetVo.setFinishTime(LocalDateTime.now().toString());
            weChatPayDivReceiverRetVo.setFinishTime("2015-05-20T13:29:35+08:00");
            weChatPayDivPayTrans.getRetReceivers().add(weChatPayDivReceiverRetVo);
        }
        WeChatPayReturnObject returnObject = null;
        if(flagState){
            returnObject = divPaySuccess(weChatPayDivPayTrans);
        }
        else{
            returnObject = divPayFail(weChatPayDivPayTrans);
        }
        return returnObject;
    }

    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public WeChatPayReturnObject getDivPayTransByOutOrderNo(String outOrderNo){
        WeChatPayReturnObject returnObject = weChatPayDao.getDivPayTransByOutOrderNo(outOrderNo);
        return returnObject;
    }

    public WeChatPayReturnObject createReceiver(WeChatPayReceiver weChatPayReceiver){
        WeChatPayReceiver receiver = (WeChatPayReceiver) weChatPayDao.getReceiverByAccount(weChatPayReceiver.getAccount()).getData();
        if(receiver!=null){
            return new WeChatPayReturnObject(WeChatPayReturnNo.OUT_DIVREFUND_NO_USED);
        }
        WeChatPayReturnObject returnObject = null;
        returnObject = weChatPayDao.createReceiver(cloneObj(weChatPayReceiver, WeChatPayReceiverPo.class));
        return returnObject;
    }

    public WeChatPayReturnObject deleteReceiver(String account){
        return weChatPayDao.deleteReceiver(account);
    }

    private WeChatPayReturnObject divRefundSuccess(WeChatPayDivRefundTrans weChatPayDivRefundTrans){
        weChatPayDivRefundTrans.setResult(DIVREFUND_STATUS_SUCCESS);
        weChatPayDivRefundTrans.setReturnId("3008450740201411110007820472");
        weChatPayDivRefundTrans.setCreateTime("2015-05-20T13:29:35+08:00");
        weChatPayDivRefundTrans.setFinishTime("2015-05-20T13:29:35+08:00");
        weChatPayDao.createDivRefundTrans(weChatPayDivRefundTrans);
        return new WeChatPayReturnObject(weChatPayDivRefundTrans);
    }
    private WeChatPayReturnObject divPaySuccess(WeChatPayDivPayTrans weChatPayDivPayTrans){
        weChatPayDivPayTrans.setState(DIVPAY_STATE_FINISHED);
        weChatPayDivPayTrans.setPayerTotal(weChatPayDivPayTrans.getPayerTotal());
        weChatPayDivPayTrans.setSuccessTime(LocalDateTime.now());
        weChatPayDivPayTrans.setOrderId("3008450740201411110007820472");
        weChatPayDao.createDivPayTrans(weChatPayDivPayTrans);
        return new WeChatPayReturnObject(weChatPayDivPayTrans);
    }

    private WeChatPayReturnObject divPayFail(WeChatPayDivPayTrans weChatPayDivPayTrans){
        weChatPayDivPayTrans.setState(DIVPAY_STATE_PROCESSING);
        weChatPayDivPayTrans.setOrderId("3008450740201411110007820472");
        weChatPayDao.createDivPayTrans(weChatPayDivPayTrans);
        return new WeChatPayReturnObject(weChatPayDivPayTrans);
    }


    private WeChatPayReturnObject paySuccess(WeChatPayTransaction weChatPayTransaction){
        weChatPayTransaction.setTradeState(TRADE_STATE_SUCCESS);
        weChatPayTransaction.setPayerTotal(weChatPayTransaction.getTotal()-(int)(Math.random()*2));
        weChatPayTransaction.setSuccessTime(LocalDateTime.now());
        weChatPayTransaction.setTransactionId(Common.genSeqNum(32));
        return weChatPayDao.createTransaction( cloneObj(weChatPayTransaction,WeChatPayTransactionPo.class) );
    }

    private WeChatPayReturnObject payFail(WeChatPayTransaction weChatPayTransaction){
        weChatPayTransaction.setTradeState(TRADE_STATE_FAIL);
        weChatPayTransaction.setTransactionId(Common.genSeqNum(32));
        return weChatPayDao.createTransaction( cloneObj(weChatPayTransaction,WeChatPayTransactionPo.class) );
    }

    private WeChatPayReturnObject refundSuccess(WeChatPayRefund weChatPayRefund){
        WeChatPayTransactionPo weChatPayTransactionPo = new WeChatPayTransactionPo();
        weChatPayTransactionPo.setOutTradeNo(weChatPayRefund.getOutTradeNo());
        weChatPayTransactionPo.setTradeState(TRADE_STATE_REFUND);
        weChatPayDao.updateTransactionByOutTradeNo(weChatPayTransactionPo);

        weChatPayRefund.setStatus(REFUND_STATUS_SUCCESS);
        weChatPayRefund.setSuccessTime(LocalDateTime.now());
        return weChatPayDao.createRefund( cloneObj(weChatPayRefund,WeChatPayRefundPo.class) );
    }

    private WeChatPayReturnObject refundFail(WeChatPayRefund weChatPayRefund){
        weChatPayRefund.setStatus(REFUND_STATUS_FAIL);
        return weChatPayDao.createRefund( cloneObj(weChatPayRefund,WeChatPayRefundPo.class) );
    }

}
