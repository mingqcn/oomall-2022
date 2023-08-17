//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.oomall.payment.dao.DivPayTransDao;
import cn.edu.xmu.oomall.payment.dao.RefundTransDao;
import lombok.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 支付分账交易
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DivRefundTrans extends Transaction{

    /**
     * 待退款
     */
    public static final Byte NEW = 0;
    /**
     * 已退款
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
     * 退款失败
     */
    public static final Byte FAIL = 4;
    /**
     * 退款取消
     */
    public static final Byte CANCEL = 5;

    public static final Map<Byte, String> STATUSNAMES = new HashMap(){
        {
            put(NEW, "待退回");
            put(SUCCESS, "已退回");
            put(CHECKED, "已对账");
            put(WRONG, "错账");
            put(FAIL, "退回失败");
            put(CANCEL, "取消");
        }
    };

    private static final Map<Byte, Set<Byte>> toStatus = new HashMap<>(){
        {
            put(NEW, new HashSet<>(){
                {
                    add(SUCCESS);
                    add(CANCEL);
                    add(FAIL);
                }
            });
            put(SUCCESS, new HashSet<>(){
                {
                    add(WRONG);
                    add(CHECKED);
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

    @Getter
    @Setter
    private Long refundTransId;

    @ToString.Exclude
    private RefundTrans refundTrans;

    @Setter
    private RefundTransDao refundTransDao;

    public RefundTrans getRefundTrans() throws BusinessException{
        if (null == this.refundTrans && null != this.refundTransDao){
            this.refundTrans = this.refundTransDao.findById(this.refundTransId);
        }
        return this.refundTrans;
    }

    @Getter
    @Setter
    private Long divPayTransId;

    @ToString.Exclude
    private DivPayTrans divPayTrans;

    @Setter
    private DivPayTransDao divPayTransDao;

    public DivPayTrans getDivPayTrans() throws BusinessException{
        if (null == this.divPayTrans && null != this.divPayTransDao){
            this.divPayTrans = this.divPayTransDao.findById(this.divPayTransId);
        }
        return this.divPayTrans;
    }

    public DivRefundTrans(RefundTrans refundTrans, DivPayTrans divPayTrans, ShopChannel shopChannel) throws BusinessException {
        super();
        this.refundTransId = refundTrans.getId();
        this.amount = refundTrans.getDivAmount();

        this.shopChannelId = shopChannel.getId();
        this.status  = DivRefundTrans.NEW;
        this.divPayTransId = divPayTrans.getId();
    }
}
