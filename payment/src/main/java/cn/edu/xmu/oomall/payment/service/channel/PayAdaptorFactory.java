//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service.channel;

import cn.edu.xmu.oomall.payment.dao.bo.Channel;
import cn.edu.xmu.oomall.payment.dao.bo.ShopChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class PayAdaptorFactory {

    private static final Logger logger = LoggerFactory.getLogger(PayAdaptorFactory.class);

    private ApplicationContext context;

    @Autowired
    public PayAdaptorFactory(ApplicationContext context) {
        this.context = context;
    }

    /**
     * 返回商铺的支付渠道服务
     * 简单工厂模式
     *
     * @param shop 商铺支付渠道
     * @return
     * @author Ming Qiu
     * <p>
     * date: 2022-11-06 18:05
     */
    public PayAdaptor createPayAdaptor(ShopChannel shop) {
        Channel channel = shop.getChannel();
        logger.debug("createPayAdaptor: channel = {}",channel);
        return (PayAdaptor) context.getBean(channel.getBeanName());
    }

}
