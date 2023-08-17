//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.payment.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.payment.dao.ChannelDao;
import cn.edu.xmu.oomall.payment.dao.PayTransDao;
import cn.edu.xmu.oomall.payment.dao.ShopChannelDao;
import cn.edu.xmu.oomall.payment.dao.TransactionDao;
import lombok.*;

import java.time.LocalDateTime;

/**
 *  台账
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Ledger extends OOMallObject {
    /**
     * 未处理
     */
    public static final Byte UNSETTLE = 0;
    /**
     * 已处理
     */
    public static final Byte SETTLE = 1;
    /**
     * 所有类型
     */
    public static final Byte ALL_TYPE = 0;
    /**
     * 支付渠道长款
     */
    public static final Byte CHANNEL_TYPE = 5;
    /**
     * 支付交易
     */
    public static final Byte PAY_TYPE = 1;
    /**
     * 退款交易
     */
    public static final Byte REFUND_TYPE = 2;
    /**
     * 支付分账
     */
    public static final Byte DIVPAY_TYPE = 3;
    /**
     * 分账退回
     */
    public static final Byte DIVREFUND_TYPE = 4;

    /**
     * 内部交易号
     */
    @Getter
    @Setter
    private String outNo;

    /**
     * 渠道交易号
     */
    @Getter
    @Setter
    private String transNo;

    /**
     * 金额
     */
    @Getter
    @Setter
    private Long amount;

    /**
     * 交易时间
     */
    @Getter
    @Setter
    private LocalDateTime successTime;

    /**
     * 状态
     */
    @Getter
    @Setter
    private Byte status;
    /**
     * 类型
     */
    @Getter
    @Setter
    private Byte type;
    /**
     * 调账者id
     */
    @Getter
    @Setter
    private Long adjust_id;

    /**
     * 调账者
     */
    @Getter
    @Setter
    private String adjust_name;

    /**
     * 调账时间
     */
    @Getter
    @Setter
    private LocalDateTime adjustTime;
    /**
     * 对账时间
     */
    @Getter
    @Setter
    private LocalDateTime checkTime;


    /**
     * 关联的交易
     * <p>
     * 渠道多出的账目此属性为null
     */
    @Setter
    private Transaction trans;

    @Getter
    @Setter
    private Long transId;


    @Setter
    private TransactionDao transactionDao;

    public Transaction getTrans(){
        if (null == this.trans && null != this.transactionDao) {
            this.trans = this.transactionDao.findById(this.transId, this.type);
        }
        return this.trans;
    }

    /**
     * 台账所属商铺渠道
     */
    @Setter
    private ShopChannel shopChannel;

    @Getter
    @Setter
    private Long shopChannelId;

    @Setter
    @ToString.Exclude
    private ShopChannelDao shopChannelDao;

    public ShopChannel getShopChannel(){
        if (null == this.shopChannel && null != this.shopChannelDao) {
            this.shopChannel = this.shopChannelDao.findById(this.shopChannelId);
        }
        return this.shopChannel;
    }

    /**
     * 台账所属渠道, 如果shopChanel为null，此属性应该有值
     */
    private Channel channel;

    @Getter
    @Setter
    private Long channelId;

    @Setter
    private ChannelDao channelDao;

    public Channel getChannel(){
        if (null == this.channel && null != this.channelDao) {
            this.channel = this.channelDao.findById(this.channelId);
        }
        return this.channel;
    }

}
