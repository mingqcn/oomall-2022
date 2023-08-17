package cn.edu.xmu.oomall.wechatpay.dao;


import cn.edu.xmu.oomall.wechatpay.mapper.*;
import cn.edu.xmu.oomall.wechatpay.model.bo.*;
import cn.edu.xmu.oomall.wechatpay.model.po.*;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayDivReceiverRetVo;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayReturnNo;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneObj;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;

/**
 * @author linzhicheng
 * @date 2022/11/30
 */
@Repository
public class WeChatPayDao {

    private WeChatPayTransactionPoMapper weChatPayTransactionPoMapper;

    private WeChatPayRefundPoMapper weChatPayRefundPoMapper;

    private WeChatPayDivPayTransPoMapper weChatPayDivPayTransPoMapper;

    private WeChatPayDivRefundTransPoMapper weChatPayDivRefundTransPoMapper;

    private WeChatPayReceiverPoMapper weChatPayReceiverPoMapper;
    @Autowired
    public WeChatPayDao(WeChatPayTransactionPoMapper weChatPayTransactionPoMapper, WeChatPayRefundPoMapper weChatPayRefundPoMapper, WeChatPayDivPayTransPoMapper weChatPayDivPayTransPoMapper,
                        WeChatPayDivRefundTransPoMapper weChatPayDivRefundTransPoMapper, WeChatPayReceiverPoMapper weChatPayReceiverPoMapper){
        this.weChatPayTransactionPoMapper = weChatPayTransactionPoMapper;
        this.weChatPayRefundPoMapper = weChatPayRefundPoMapper;
        this.weChatPayDivPayTransPoMapper = weChatPayDivPayTransPoMapper;
        this.weChatPayDivRefundTransPoMapper = weChatPayDivRefundTransPoMapper;
        this.weChatPayReceiverPoMapper = weChatPayReceiverPoMapper;
    }

    public WeChatPayReturnObject getDivRefundTransByOutReturnNo(String outReturnNo){
        try{
            WeChatPayDivRefundTransPoExample example = new WeChatPayDivRefundTransPoExample();
            WeChatPayDivRefundTransPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutReturnNoEqualTo(outReturnNo);
            List<WeChatPayDivRefundTransPo> list = weChatPayDivRefundTransPoMapper.selectByExample(example);
            if(list.size()==0) {
                return new WeChatPayReturnObject(WeChatPayReturnNo.RESOURCE_NOT_EXISTS);
            }
            WeChatPayDivRefundTrans weChatPayDivRefundTrans = cloneObj(list.get(0), WeChatPayDivRefundTrans.class);
            weChatPayDivRefundTrans.setOrderId("3008450740201411110007820472");
            weChatPayDivRefundTrans.setReturnId("3008450740201411110007820472");
            weChatPayDivRefundTrans.setReturnMchid("86693852");
            weChatPayDivRefundTrans.setDescription("用户退款");
            weChatPayDivRefundTrans.setResult("SUCCESS");
            weChatPayDivRefundTrans.setCreateTime("2015-05-20T13:29:35+08:00");
            weChatPayDivRefundTrans.setFinishTime("2015-05-20T13:29:35+08:00");
            return new WeChatPayReturnObject(weChatPayDivRefundTrans);
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public void createDivRefundTrans(WeChatPayDivRefundTrans weChatPayDivRefundTrans){
        WeChatPayDivRefundTransPo weChatPayDivRefundTransPo = cloneObj(weChatPayDivRefundTrans, WeChatPayDivRefundTransPo.class);
        weChatPayDivRefundTransPoMapper.insertSelective(weChatPayDivRefundTransPo);
    }

    public void createDivPayTrans(WeChatPayDivPayTrans weChatPayDivPayTrans){
        WeChatPayDivPayTransPo weChatPayDivPayTransPo = cloneObj(weChatPayDivPayTrans, WeChatPayDivPayTransPo.class);
        weChatPayDivPayTransPoMapper.insertSelective(weChatPayDivPayTransPo);
    }

    public WeChatPayReturnObject getDivPayTransByOutOrderNo(String OutOrderNo){
        try{
            WeChatPayDivPayTransPoExample example = new WeChatPayDivPayTransPoExample();
            WeChatPayDivPayTransPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutOrderNoEqualTo(OutOrderNo);
            List<WeChatPayDivPayTransPo> list = weChatPayDivPayTransPoMapper.selectByExample(example);
            if(list.size()==0) {
                return new WeChatPayReturnObject(WeChatPayReturnNo.RESOURCE_NOT_EXISTS);
            }
            WeChatPayDivPayTrans weChatPayDivPayTrans = cloneObj(list.get(0), WeChatPayDivPayTrans.class);
            weChatPayDivPayTrans.setTransactionId("4208450740201411110007820472");
            weChatPayDivPayTrans.setOrderId("3008450740201411110007820472");
            WeChatPayDivReceiverRetVo weChatPayDivReceiverRetVo = new WeChatPayDivReceiverRetVo("MERCHANT_ID","1900000109",100,"分给商户1900000110","SUCCESS","NULL","2015-05-20T13:29:35+08:00","2015-05-20T13:29:35+08:00","36011111111111111111111");
            weChatPayDivPayTrans.getRetReceivers().add(weChatPayDivReceiverRetVo);
            weChatPayDivPayTrans.setState("FINISHED");
            return new WeChatPayReturnObject(weChatPayDivPayTrans);
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject createReceiver(WeChatPayReceiverPo weChatPayReceiverPo){
        try{
            weChatPayReceiverPoMapper.insertSelective(weChatPayReceiverPo);
            WeChatPayReceiverPo newWeChatPayReceiverPo =
                    weChatPayReceiverPoMapper.selectByPrimaryKey(weChatPayReceiverPo.getId());
            return new WeChatPayReturnObject(cloneObj(newWeChatPayReceiverPo, WeChatPayReceiver.class));
        }catch (Exception e) {
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject deleteReceiver(String account){
        try{
            WeChatPayReceiver receiver = (WeChatPayReceiver) getReceiverByAccount(account).getData();
            if(receiver==null){
                return new WeChatPayReturnObject(WeChatPayReturnNo.INVALID_REQUEST);
            }
            Map<String,String> ret = new HashMap();
            ret.put("type",receiver.getType());
            ret.put("account",receiver.getAccount());
            weChatPayReceiverPoMapper.deleteByPrimaryKey(receiver.getId());
            return new WeChatPayReturnObject(WeChatPayReturnNo.OK, ret);
        }catch (Exception e) {
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject getReceiverByAccount(String account){
        try {
            WeChatPayReceiverPoExample example = new WeChatPayReceiverPoExample();
            WeChatPayReceiverPoExample.Criteria criteria = example.createCriteria();
            criteria.andAccountEqualTo(account);
            List<WeChatPayReceiverPo> list = weChatPayReceiverPoMapper.selectByExample(example);
            if(list.size()==0) {
                return new WeChatPayReturnObject(WeChatPayReturnNo.RESOURCE_NOT_EXISTS);
            }
            return new WeChatPayReturnObject(cloneObj(list.get(0), WeChatPayReceiver.class));

        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject createTransaction(WeChatPayTransactionPo weChatPayTransactionPo){
        try{
            weChatPayTransactionPoMapper.insertSelective(weChatPayTransactionPo);
            WeChatPayTransactionPo newWeChatPayTransactionPo = weChatPayTransactionPoMapper.selectByPrimaryKey(weChatPayTransactionPo.getId());
            return new WeChatPayReturnObject(cloneObj(newWeChatPayTransactionPo, WeChatPayTransaction.class));
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject getTransactionByOutTradeNo(String outTradeNo){
        try{
            WeChatPayTransactionPoExample example = new WeChatPayTransactionPoExample();
            WeChatPayTransactionPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutTradeNoEqualTo(outTradeNo);
            List<WeChatPayTransactionPo> list = weChatPayTransactionPoMapper.selectByExample(example);
            if(list.size()==0) {
                return new WeChatPayReturnObject(WeChatPayReturnNo.RESOURCE_NOT_EXISTS);
            }
            return new WeChatPayReturnObject(cloneObj(list.get(0), WeChatPayTransaction.class));
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject updateTransactionByOutTradeNo(WeChatPayTransactionPo weChatPayTransactionPo){
        try{
            WeChatPayTransactionPoExample example = new WeChatPayTransactionPoExample();
            WeChatPayTransactionPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutTradeNoEqualTo(weChatPayTransactionPo.getOutTradeNo());
            int ret = weChatPayTransactionPoMapper.updateByExampleSelective(weChatPayTransactionPo,example);
            if (ret == 0) {
                return new WeChatPayReturnObject(WeChatPayReturnNo.RESOURCE_NOT_EXISTS);
            } else {
                return new WeChatPayReturnObject(WeChatPayReturnNo.OK);
            }
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject createRefund(WeChatPayRefundPo weChatPayRefundPo){
        try{
            weChatPayRefundPoMapper.insertSelective(weChatPayRefundPo);
            WeChatPayRefundPo newWeChatPayRefundPo = weChatPayRefundPoMapper.selectByPrimaryKey(weChatPayRefundPo.getId());
            return new WeChatPayReturnObject(cloneObj(newWeChatPayRefundPo, WeChatPayRefund.class));
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject getRefundByOutTradeNo(String outTradeNo){
        try{
            WeChatPayRefundPoExample example = new WeChatPayRefundPoExample();
            WeChatPayRefundPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutTradeNoEqualTo(outTradeNo);
            List<WeChatPayRefundPo> list = weChatPayRefundPoMapper.selectByExample(example);
            return new WeChatPayReturnObject(list);
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject getRefundByOutRefundNo(String outRefundNo){
        try{
            WeChatPayRefundPoExample example = new WeChatPayRefundPoExample();
            WeChatPayRefundPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutRefundNoEqualTo(outRefundNo);
            List<WeChatPayRefundPo> list = weChatPayRefundPoMapper.selectByExample(example);
            if(list.size()==0) {
                return new WeChatPayReturnObject(WeChatPayReturnNo.RESOURCE_NOT_EXISTS);
            }
            return new WeChatPayReturnObject(cloneObj(list.get(0), WeChatPayRefund.class));
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

}
