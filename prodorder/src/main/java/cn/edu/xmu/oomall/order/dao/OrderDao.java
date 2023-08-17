//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.order.dao.bo.Order;
import cn.edu.xmu.oomall.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.order.mapper.OrderPoMapper;
import cn.edu.xmu.oomall.order.mapper.po.OrderItemPo;
import cn.edu.xmu.oomall.order.mapper.po.OrderPo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao {

    private OrderPoMapper orderPoMapper;

    private OrderItemPoMapper orderItemPoMapper;


    @Autowired
    public OrderDao(OrderPoMapper orderPoMapper, OrderItemPoMapper orderItemPoMapper) {
        this.orderPoMapper = orderPoMapper;
        this.orderItemPoMapper = orderItemPoMapper;
    }


    public void createOrder(Order order){
        OrderPo orderPo = OrderPo.builder().creatorId(order.getCreatorId()).creatorName(order.getCreatorName()).orderSn(order.getOrderSn()).build();
        orderPoMapper.save(orderPo);

        order.getOrderItems().stream().forEach(orderItem -> {
            //TODO: 先要减去货品数量

            OrderItemPo orderItemPo = OrderItemPo.builder().creatorId(orderItem.getCreatorId()).onsaleId(orderItem.getOnsaleId()).quantity(orderItem.getQuantity()).build();
            orderItemPoMapper.save(orderItemPo);
        });
    }
}
