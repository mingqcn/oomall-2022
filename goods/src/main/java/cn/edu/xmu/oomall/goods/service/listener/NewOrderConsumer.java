//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.service.listener;

import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.goods.dao.bo.Order;
import cn.edu.xmu.oomall.goods.service.OnsaleService;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * 消费订单模块发出的新订单消息
 */
@Service
@RocketMQMessageListener(topic = "New-Order", consumerGroup = "goods-new-order", consumeThreadMax = 1)
public class NewOrderConsumer implements RocketMQListener<Message> {

    private static final Logger logger = LoggerFactory.getLogger(NewOrderConsumer.class);

    private OnsaleService onsaleService;

    @Autowired
    public NewOrderConsumer(OnsaleService onsaleService) {
        this.onsaleService = onsaleService;
    }

    @Override
    public void onMessage(Message message) {

        try {
            String content = new String(message.getBody(), "UTF-8");
            Order order = JacksonUtil.toObj(content, Order.class);
            if (null == order || null == order.getOrderItems()){
                logger.error("OrderConsumer: wrong format.... content = {}",content);
            }else{
                order.getOrderItems().stream().forEach(item -> {
                    this.onsaleService.incrQuantity(item.getId(), -1 * item.getQuantity());
                });
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("OrderConsumer: wrong encoding.... msg = {}",message);
        }

    }
}
