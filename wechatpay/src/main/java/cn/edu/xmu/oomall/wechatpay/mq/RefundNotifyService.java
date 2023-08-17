package cn.edu.xmu.oomall.wechatpay.mq;

import cn.edu.xmu.oomall.wechatpay.microservice.WeChatPayNotifyService;
import cn.edu.xmu.oomall.wechatpay.model.vo.WeChatPayRefundNotifyRetVo;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@RocketMQMessageListener(topic = "wechatpayrefund-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumerGroup = "wechatpayrefund-group")
public class RefundNotifyService implements RocketMQListener<WeChatPayRefundNotifyRetVo> {
    @Resource
    WeChatPayNotifyService weChatPayNotifyService;

    @Override
    public void onMessage(WeChatPayRefundNotifyRetVo weChatPayRefundNotifyRetVo) {
        weChatPayNotifyService.refundNotify(weChatPayRefundNotifyRetVo);
    }
}