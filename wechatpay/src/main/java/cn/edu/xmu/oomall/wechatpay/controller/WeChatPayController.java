package cn.edu.xmu.oomall.wechatpay.controller;

import cn.edu.xmu.oomall.wechatpay.model.bo.*;
import cn.edu.xmu.oomall.wechatpay.model.vo.*;
import cn.edu.xmu.oomall.wechatpay.service.WeChatPayService;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayCommon;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayReturnNo;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author linzhicheng
 * @date 2022/11/30
 */
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class WeChatPayController {

    private WeChatPayService weChatPayService;
    @Autowired
    public WeChatPayController(WeChatPayService weChatPayService){
        this.weChatPayService = weChatPayService;
    }

    @PostMapping("/internal/wechat/pay/transactions/jsapi")
    public Object createTransaction(@Validated @RequestBody WeChatPayTransactionVo weChatPayTransactionVo, BindingResult bindingResult){
        WeChatPayReturnObject returnObject = weChatPayService.createTransaction(new WeChatPayTransaction(weChatPayTransactionVo));
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @GetMapping("/internal/wechat/pay/transactions/out-trade-no/{out_trade_no}")
    public Object getTransaction(@PathVariable("out_trade_no") String outTradeNo){
        WeChatPayReturnObject returnObject = weChatPayService.getTransaction(outTradeNo);
        returnObject = WeChatPayCommon.getRetObject(returnObject);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @PostMapping("/internal/wechat/pay/transactions/out-trade-no/{out_trade_no}/close")
    public Object closeTransaction(@PathVariable("out_trade_no") String outTradeNo){

        WeChatPayReturnObject returnObject = weChatPayService.closeTransaction(outTradeNo);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @PostMapping("/internal/wechat/refund/domestic/refunds")
    public Object createRefund(@Validated @RequestBody WeChatPayRefundVo weChatPayRefundVo, BindingResult bindingResult){
        WeChatPayReturnObject returnObject = weChatPayService.createRefund(new WeChatPayRefund(weChatPayRefundVo));
        returnObject = WeChatPayCommon.getRetObject(returnObject);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @GetMapping("/internal/wechat/refund/domestic/refunds/{out_refund_no}")
    public Object getRefund(@PathVariable("out_refund_no") String outRefundNo){

        WeChatPayReturnObject returnObject = weChatPayService.getRefund(outRefundNo);
        returnObject = WeChatPayCommon.getRetObject(returnObject);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @GetMapping("/internal/wechat/bill/fundflowbill")
    public Object getFundFlowBill(@RequestParam("bill_date") String billDate){

        WeChatPayReturnObject returnObject = new WeChatPayReturnObject(new WeChatPayFundFlowBillRetVo());
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @PostMapping("/internal/wechat/profitsharing/orders")
    public Object createDivPayTrans(@Validated @RequestBody WeChatPayDivPayTransVo weChatPayDivPayTransVo,
                                    BindingResult bindingResult){
        WeChatPayReturnObject returnObject = weChatPayService.createDivPayTrans(new WeChatPayDivPayTrans(weChatPayDivPayTransVo));
        returnObject = WeChatPayCommon.getRetObject(returnObject);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @GetMapping("/internal/wechat/profitsharing/orders/{out_order_no}")
    public Object getDivPayTrans(@PathVariable("out_order_no") String outOrderNo,@RequestParam String transaction_id){
        WeChatPayReturnObject returnObject = weChatPayService.getDivPayTransByOutOrderNo(outOrderNo);
        returnObject = WeChatPayCommon.getRetObject(returnObject);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @PostMapping("/internal/wechat/profitsharing/return-orders")
    public Object createDivRefundTrans(@Validated @RequestBody WeChatPayDivRefundTransVo weChatPayDivRefundTransVo,
                                       BindingResult bindingResult){
        WeChatPayReturnObject returnObject = weChatPayService.createDivRefundTrans(new WeChatPayDivRefundTrans(weChatPayDivRefundTransVo));
        returnObject = WeChatPayCommon.getRetObject(returnObject);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @GetMapping("/internal/wechat/profitsharing/return-orders/{out_return_no}")
    public Object getDivRefundTrans(@PathVariable("out_return_no") String outReturnNo, @RequestParam String out_order_no){
        WeChatPayReturnObject returnObject = weChatPayService.getDivRefundTransByOutReturnNo(outReturnNo);
        returnObject = WeChatPayCommon.getRetObject(returnObject);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @PostMapping("/internal/wechat/profitsharing/receivers/add")
    public Object createReceiver(@Validated @RequestBody WeChatPayReceiverVo weChatPayReceiverVo,
                                 BindingResult bindingResult){
        WeChatPayReturnObject returnObject = weChatPayService.createReceiver(new WeChatPayReceiver(weChatPayReceiverVo));
        returnObject = WeChatPayCommon.getRetObject(returnObject);
        return WeChatPayCommon.decorateReturnObject(returnObject);
    }

    @DeleteMapping("/internal/wechat/profitsharing/receivers/delete")
    public Object deleteReceiver(@RequestParam String appid,
                                 @RequestParam String type,
                                 @RequestParam String account){
        return WeChatPayCommon.decorateReturnObject(weChatPayService.deleteReceiver(account));
    }

}
