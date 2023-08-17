//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.oomall.payment.dao.ChannelDao;
import cn.edu.xmu.oomall.payment.dao.DivPayTransDao;
import cn.edu.xmu.oomall.payment.dao.RefundTransDao;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;

/**
 * 支付交易
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PayTrans extends Transaction{

    private static final Logger logger = LoggerFactory.getLogger(PayTrans.class);
    /**
     * 未支付
     */
    public static final Byte NEW = 0;
    /**
     * 已支付
     */
    public static final Byte SUCCESS = 1;
    /**
     * 已对账
     */
    public static final Byte CHECKED = 2;
    /**
     * 错账
     */
    public static final Byte WRONG = 3;
    /**
     * 支付失败
     */
    public static final Byte FAIL = 4;
    /**
     * 取消
     */
    public static final Byte CANCEL = 5;
    /**
     * 分账
     */
    public static final Byte DIV = 7;

    /**
     * 状态和名称的对应
     */
    public static final Map<Byte, String> STATUSNAMES = new HashMap(){
        {
            put(NEW, "待支付");
            put(SUCCESS, "已支付");
            put(CHECKED, "已对账");
            put(WRONG, "错账");
            put(FAIL, "支付失败");
            put(CANCEL, "取消");
            put(DIV, "分账");
        }
    };

    /**
     * 允许的状态迁移
     */
    private static final Map<Byte, Set<Byte>> toStatus = new HashMap<>(){
        {
            put(NEW, new HashSet<>(){
                {
                    add(FAIL);
                    add(CANCEL);
                    add(SUCCESS);
                }
            });
            put(SUCCESS, new HashSet<>(){
                {
                    add(CHECKED);
                    add(WRONG);
                }
            });
            put(CHECKED, new HashSet<>(){
                {
                    add(DIV);
                }
            });
            put(WRONG, new HashSet<>(){
                {
                    add(CHECKED);
                }
            });
        }
    };

    /**
     * 是否允许状态迁移
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:25
     * @param status
     * @return
     */
    public boolean allowStatus(Byte status){
        boolean ret = false;

        if (null != status && null != this.status){
            Set<Byte> allowStatusSet = toStatus.get(this.status);
            if (null != allowStatusSet) {
                ret = allowStatusSet.contains(status);
            }
        }
        return ret;
    }

    /**
     * 获得当前状态名称
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:43
     * @return
     */
    public String getStatusName(){
        return STATUSNAMES.get(this.status);
    }

    /**
     * 退款中
     */
    public static final Byte REFUNDING = 1;
    /**
     * 支付用户id
     */
    @Setter
    @Getter
    private String spOpenid;

    /**
     * 交易结束时间
     */
    @Setter
    @Getter
    private LocalDateTime timeExpire;

    /**
     * 交易开始时间
     */
    @Setter
    @Getter
    private LocalDateTime timeBegin;

    /**
     * 预支付id
     */
    @Setter
    @Getter
    private String prepayId;

    /**
     * 待分账金额
     */
    @Setter
    @Getter
    private Long divAmount;

    /**
     * 0 正常 1退款中
     */
    @Setter
    @Getter
    private Byte inRefund;

    @Setter
    private RefundTransDao refundTransDao;
    /**
     * 关联的退款交易
     */
    @ToString.Exclude
    @Setter
    private List<RefundTrans> refundTransList;

    public List<RefundTrans> getRefundTransList() throws BusinessException {
        if (null == this.refundTransList && null != this.refundTransDao){
            this.refundTransList = this.refundTransDao.retrieveByPayTransId(this.id, 1, MAX_RETURN).getList();
        }
        return this.refundTransList;
    }

    @Setter
    private DivPayTransDao divPayTransDao;
    /**
     * 关联的分账交易
     */
    @ToString.Exclude
    private DivPayTrans divTrans;

    public DivPayTrans getDivTrans() {
        if (null == this.divTrans && null != this.divPayTransDao) {
            this.divTrans = this.divPayTransDao.retrieveByPayTransId(this.id);
        }
        return divTrans;
    }

    /**
     * 已经退回和正在处理中的退款总额
     * @author Ming Qiu
     * <p>
     * date: 2022-11-15 15:25
     * @return
     */
    public Long getRefundAmount(){
        return this.getRefundTransList().stream()
            .filter(trans -> RefundTrans.CANCEL != trans.getStatus() || RefundTrans.FAIL == trans.getStatus())
            .map(RefundTrans::getAmount)
            .reduce((x,y)->x + y).get();
    }
    /**
     * 创建支付交易
     * @author Ming Qiu
     * <p>
     * date: 2022-11-14 6:43
     * @param shopChannel
     * @param spOpenid
     * @param timeExpire 过期时间，单位秒
     * @param amount
     */
    public PayTrans(LocalDateTime timeBegin, LocalDateTime timeExpire,  String spOpenid,  Long amount, long divAmount, ShopChannel shopChannel) {
        this.spOpenid = spOpenid;
        this.timeExpire = timeExpire;
        this.spOpenid = spOpenid;
        this.shopChannelId = shopChannel.getId();
        this.amount = amount;
        this.timeBegin = timeBegin;
        this.timeExpire = timeExpire;
        this.divAmount = divAmount;
    }
}
