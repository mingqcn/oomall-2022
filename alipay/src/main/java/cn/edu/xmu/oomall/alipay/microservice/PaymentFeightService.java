package cn.edu.xmu.oomall.alipay.microservice;

import cn.edu.xmu.oomall.alipay.model.bo.NotifyBody;
import cn.edu.xmu.oomall.alipay.util.NotifyReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "payment-service")
public interface PaymentFeightService {
    /**
     * 回调,退款只有成功的回调，支付有不成功和成功的回调
     * 退款和支付的区别在于
     * 退款的out_biz_no字段不为空
     * 而支付的回调out_biz_no字段为空
     * @return
     */
    @PostMapping(value = "/alipay/notify")
    NotifyReturnObject notify(@RequestBody NotifyBody notifyBody);
}
