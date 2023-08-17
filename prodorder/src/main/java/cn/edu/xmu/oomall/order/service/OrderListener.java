//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.order.dao.bo.OrderItem;
import cn.edu.xmu.oomall.order.service.dto.ConsigneeDto;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RocketMQTransactionListener
public class OrderListener implements RocketMQLocalTransactionListener {


    private  OrderService orderService;

    @Autowired
    public OrderListener(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 事务消息发送成功回调
     * @author Ming Qiu
     * <p>
     * date: 2022-11-30 21:00
     * @param msg
     * @param arg
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        ConsigneeDto consigneeDto = (ConsigneeDto) msg.getHeaders().get("consigee");
        String message = (String) msg.getHeaders().get("message");
        UserDto user = (UserDto) msg.getHeaders().get("user");
        String body = new String((byte[]) msg.getPayload(), StandardCharsets.UTF_8);
        Map<Long, List<OrderItem>> packs = JacksonUtil.toObj(body, HashMap.class);

        try{
            this.orderService.saveOrder(packs, consigneeDto, message, user);
        }catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        return null;
    }
}
