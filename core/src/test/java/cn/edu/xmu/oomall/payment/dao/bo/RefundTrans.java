//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.payment.dao.bo;

import lombok.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    public static final Byte NOTREFUND = 0;
    /**
     * 已退款
     */
    public static final Byte REFUNDED = 1;
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
    public static final Byte REFOUDFAIL = 4;
    /**
     * 退款取消
     */
    public static final Byte CANCEL = 5;

    public static final Map<Byte, String> STATUSNAMES = new HashMap(){
        {
            put(NOTREFUND, "待退款");
            put(REFUNDED, "已退款");
            put(CHECKED, "已对账");
            put(WRONG, "错账");
            put(REFOUDFAIL, "退款失败");
            put(CANCEL, "待退款");
        }
    };

    private static final Map<Byte, Set<Byte>> fromStatus = new HashMap<>(){
        {
            put(NOTREFUND, null);
            put(REFUNDED, new HashSet<>(){
                {
                    add(NOTREFUND);
                }
            });
            put(CHECKED, new HashSet<>(){
                {
                    add(REFUNDED);
                    add(WRONG);
                }
            });
            put(WRONG, new HashSet<>(){
                {
                    add(REFUNDED);
                }
            });
            put(REFOUDFAIL, new HashSet<>(){
                {
                    add(NOTREFUND);
                }
            });
            put(CANCEL, new HashSet<>(){
                {
                    add(NOTREFUND);
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
        return fromStatus.get(this.status).contains(status);
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
     * 状态
     */
    @Getter
    @Setter
    private Byte status;

    /**
     * 用户退回账号
     */
    @Getter
    @Setter
    private String userReceivedAccount;

    /**
     * 关联的支付交易
     */
    private PayTrans payTrans;

    @Getter
    @Setter
    private Long payTransId;

}
