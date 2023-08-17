package cn.edu.xmu.oomall.alipay.service.mq;

import cn.edu.xmu.oomall.alipay.microservice.PaymentFeightService;
import cn.edu.xmu.oomall.alipay.model.bo.NotifyBody;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@RocketMQMessageListener(topic = "alipay-notify-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumerGroup = "alipay-notify-group")
public class NotifyService implements RocketMQListener<NotifyBody> {
    @Resource
    PaymentFeightService paymentFeightService;

    @Override
    public void onMessage(NotifyBody notifyBody) {
        paymentFeightService.notify(notifyBody);
    }
}
