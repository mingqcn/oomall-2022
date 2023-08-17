//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.payment.dao.ShopChannelDao;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 交易
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Transaction extends OOMallObject{

    /**
     * 内部交易号
     */
    @Setter
    @Getter
    protected String outNo;

    /**
     * 渠道交易号
     */
    @Setter
    @Getter
    protected String transNo;

    /**
     * 金额
     */
    @Setter
    @Getter
    protected Long amount;

    /**
     * 交易时间
     */
    @Setter
    @Getter
    protected LocalDateTime successTime;

    /**
     * 状态
     */
    @Setter
    @Getter
    protected Byte status;

    /**
     * 调账者id
     */
    @Setter
    @Getter
    protected Long adjustId;

    /**
     * 调账者
     */
    @Setter
    @Getter
    protected String adjustName;

    /**
     * 调账时间
     */
    @Setter
    @Getter
    protected LocalDateTime adjustTime;

    @Setter
    @Getter
    protected Long shopChannelId;

    /**
     * 交易所属的商铺渠道
     */
    protected ShopChannel shopChannel;

    @Setter
    protected ShopChannelDao shopChannelDao;


    public ShopChannel getShopChannel() throws BusinessException {
        if (null == this.shopChannel && null != this.shopChannelDao) {
            this.shopChannel = this.shopChannelDao.findById(this.shopChannelId);
        }
        return this.shopChannel;
    }
}
