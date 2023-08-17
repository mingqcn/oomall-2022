//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.payment.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.payment.dao.ChannelDao;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * 商铺支付渠道
 */
@ToString(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopChannel extends OOMallObject implements Serializable {
    private static  final Logger logger = LoggerFactory.getLogger(ShopChannel.class);

    /**
     * 有效
     */
    public static Byte VALID = 0;
    /**
     * 无效
     */
    public static Byte INVALID = 1;

    /**
     * 商铺id
     */
    @Getter
    @Setter
    private Long shopId;

    /**
     * 子商户号
     */
    @Getter
    @Setter
    private String subMchid;

    /**
     * 状态
     */
    @Getter
    @Setter
    private Byte status;

    /**
     * 支付渠道
     */
    private Channel channel;

    @Setter
    @Getter
    private Long channelId;

    @Setter
    private ChannelDao channelDao;

    //新建是无效态
    public ShopChannel(Long shopId, Long id, String subMchid){
        this.shopId =shopId;
        this.channelId = id;
        this.subMchid =subMchid;
        this.status = ShopChannel.INVALID;
    }

    public Channel getChannel() throws BusinessException{
        if (null == this.channel && null != this.channelDao){
            logger.debug("getChannel: this.channelId = {}", this.channelId);
            this.channel = this.channelDao.findById(this.channelId);
        }
        return this.channel;
    }
}
