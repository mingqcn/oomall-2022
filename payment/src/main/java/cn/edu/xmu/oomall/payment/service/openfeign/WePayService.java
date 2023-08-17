//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.payment.service.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.payment.service.openfeign.WeParam.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "wepay-service")
public interface WePayService{

   /**
    * 发起支付交易
    * @author: Wenbo Li
    * */
   @PostMapping("/internal/wechat/pay/transactions/jsapi")
   InternalReturnObject<WePostTransRetObj> postTransaction(WePostTransParam param);

    /**
     * 查询订单(未收到微信返回信息)
     * @author: Wenbo Li
     * */
    @GetMapping("/internal/wechat/pay/transactions/out-trade-no/{out_trade_no}")
    InternalReturnObject<WeGetTransRetObj> getOrderByOutNo(WeGetTransParam param);

    /**
     * 查询订单
     * @author: Wenbo Li
     * */
    @GetMapping("/internal/wechat/pay/transactions/out-trade-no/{out_trade_no}")
    InternalReturnObject<WeGetTransRetObj> getOrderByTransId(WeGetTransParam param);


    /**
     * 微信退款
     * @author: Wenbo Li
     * */
    @PostMapping("/internal/wechat/refund/domestic/refunds")
    InternalReturnObject<WePostRefundRetObj> postRefundTransaction(WePostRefundParam params);

    /**
     * 查询退款信息
     * @author: Wenbo Li
     * */
    @GetMapping("/internal/wechat/refund/domestic/refunds/{out_refund_no}")
    InternalReturnObject<WeGetRefundRetObj> getRefund(WeGetRefundParam param);

    @GetMapping("/internal/wechat/pay/transactions/out-trade-no/{out_trade_no}")
    InternalReturnObject<WeCancelOrderRetObj> cancelOrder(WeCancelOrderParam param);

    @PostMapping("/internal/wechat/profitsharing/orders")
    InternalReturnObject<WePostDivPayRetObj> postDivPay(WePostDivPayParam param);

    @GetMapping("/internal/wechat/profitsharing/orders/{out_order_no}")
    InternalReturnObject<WeGetDivPayRetObj> getDivPay(WeGetDivPayParam param);

    @PostMapping("/internal/wechat/profitsharing/return-orders")
    InternalReturnObject<WePostDivRefundRetObj> postDivRefund(WePostDivRefundParam param);

    @GetMapping("/internal/wechat/profitsharing/return-orders/{out_return_no}")
    InternalReturnObject<WeGetDivRefundRetObj> getDivRefund(WeGetDivRefundParam param);
}

