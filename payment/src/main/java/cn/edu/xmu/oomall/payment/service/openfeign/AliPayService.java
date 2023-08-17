//School of Informatics Xiamen University, GPL-3.0 license

/**
 * 向支付宝发送请求的接口
 * */

package cn.edu.xmu.oomall.payment.service.openfeign;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.oomall.payment.service.openfeign.AliParam.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "alipay-service")
public interface AliPayService {

    /**
     * 发起支付交易
     * */
    @PostMapping("internal/alipay/gateway.do")
    InternalReturnObject<AliPostTransRetObj> postTransaction(PublicRequestParam param);

    /**
     * 查询订单(未收到微信返回信息)
     * */
    @GetMapping("internal/alipay/gateway.do")
    InternalReturnObject<AliGetTransRetObj> getOrderByOutNo(PublicRequestParam param);

    /**
     * 查询订单
     * */
    @GetMapping("internal/alipay/gateway.do")
    InternalReturnObject<AliGetTransRetObj> getOrderByTransId(PublicRequestParam param);

    /**
     * @description: 微信退款
     * @author: Wenbo Li
     * */
    @PostMapping("internal/alipay/gateway.do")
    InternalReturnObject<AliPostRefundRetObj> postRefundTransaction(PublicRequestParam params);

    /**
     * 查询退款信息
     * @author: Wenbo Li
     * */
    @GetMapping("internal/alipay/gateway.do")
    InternalReturnObject<AliGetRefundRetObj> getRefund(PublicRequestParam param);

    @PostMapping("internal/alipay/gateway.do")
    InternalReturnObject<AliCancelOrderRetObj> cancelOrder(PublicRequestParam param);
    @PostMapping("internal/alipay/gateway.do")
    InternalReturnObject<AliPostDivPayRetObj> postDivPay(PublicRequestParam param);

    @GetMapping("internal/alipay/gateway.do")
    InternalReturnObject<AliGetDivPayRetObj> getDivPay(PublicRequestParam param);
}

