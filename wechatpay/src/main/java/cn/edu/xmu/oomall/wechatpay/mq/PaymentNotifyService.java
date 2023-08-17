package cn.edu.xmu.oomall.wechatpay.mq;

import cn.edu.xmu.oomall.wechatpay.microservice.WeChatPayNotifyService;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayPaymentNotifyRetVo;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@RocketMQMessageListener(topic = "wechatpaypayment-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumerGroup = "wechatpaypayment-group")
public class PaymentNotifyService implements RocketMQListener<WeChatPayPaymentNotifyRetVo> {
    @Resource
    WeChatPayNotifyService weChatPayNotifyService;

    @Override
    public void onMessage(WeChatPayPaymentNotifyRetVo weChatPayPaymentNotifyRetVo) {
        weChatPayNotifyService.paymentNotify(weChatPayPaymentNotifyRetVo);
    }
}