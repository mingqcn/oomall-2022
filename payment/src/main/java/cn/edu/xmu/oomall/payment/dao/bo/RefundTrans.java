//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.payment.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.oomall.payment.dao.DivRefundTransDao;
import cn.edu.xmu.oomall.payment.dao.PayTransDao;
import lombok.*;

import java.util.*;

/**
 * 退款交易
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RefundTrans extends Transaction{

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
            put(NEW, "待退款");
            put(SUCCESS, "已退款");
            put(CHECKED, "已对账");
            put(WRONG, "错账");
            put(FAIL, "退款失败");
            put(CANCEL, "退款取消");
        }
    };

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
     * 用户退回账号
     */
    @Getter
    @Setter
    private String userReceivedAccount;

    /**
     * 待退回的分账金额
     */
    @Getter
    @Setter
    private Long divAmount;

    /**
     * 关联的支付交易
     */
    @ToString.Exclude
    private PayTrans payTrans;

    @Getter
    @Setter
    private Long payTransId;

    @Setter
    private PayTransDao payTransDao;


    public PayTrans getPayTrans(){
        if (null == this.payTrans && null != this.payTransDao){
            this.payTrans = this.payTransDao.findById(this.payTransId);
        }
        return this.payTrans;
    }

    /**
     * 关联的分账退回
     */
    @ToString.Exclude
    private DivRefundTrans divTrans;

    @Setter
    private DivRefundTransDao divRefundTransDao;

    public DivRefundTrans getDivTrans(){
        if (null == this.divTrans && null != this.divRefundTransDao) {
            this.divTrans = this.divRefundTransDao.findByRefundTransId(this.id);
        }
        return this.divTrans;
    }

    public RefundTrans(ShopChannel shopChannel, PayTrans payTrans, Long amount, Long divAmount) throws BusinessException{
        super();
        Set<Byte> admitStatue = new HashSet<>(){
            {
                add(PayTrans.SUCCESS);
                add(PayTrans.CHECKED);
                add(PayTrans.DIV);
            }
        };
        if (!admitStatue.contains(payTrans.getStatus()) && PayTrans.REFUNDING == payTrans.getInRefund()){
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(),"支付对象", payTrans.getId(), payTrans.getStatusName()));
        }

        // 判断退款金额总和小于支付金额
        if (amount + payTrans.getRefundAmount() > payTrans.getAmount()){
            throw new BusinessException(ReturnNo.PAY_REFUND_MORE, String.format(ReturnNo.PAY_REFUND_MORE.getMessage(), payTrans.getId()));
        }

        // 判断分账退款金额总和小于支付分账金额
        if (divAmount + payTrans.getDivTrans().getRefundAmount() > payTrans.getDivAmount()){
            throw new BusinessException(ReturnNo.PAY_DIVREFUND_MORE, String.format(ReturnNo.PAY_DIVREFUND_MORE.getMessage(), payTrans.getId()));
        }

        this.amount = amount;
        this.shopChannelId = shopChannel.getId();
        this.payTransId = payTrans.getId();
        this.status = RefundTrans.NEW;
        this.divAmount = divAmount;

        payTrans.setRefundTransList(null);
    }
}
