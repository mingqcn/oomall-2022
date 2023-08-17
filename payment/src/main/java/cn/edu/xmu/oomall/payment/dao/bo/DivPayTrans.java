//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao.bo;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.oomall.payment.dao.DivRefundTransDao;
import cn.edu.xmu.oomall.payment.dao.PayTransDao;
import lombok.*;

import java.util.List;

/**
 * 支付分账交易
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DivPayTrans extends Transaction{

    @Getter
    @Setter
    private Long payTransId;

    /**
     * 关联的支付交易
     */
    @ToString.Exclude
    private PayTrans trans;

    @Setter
    private PayTransDao payTransDao;

    public PayTrans getTrans() throws BusinessException {
        if (null == trans && null != this.payTransDao){
            this.trans = payTransDao.findById(this.payTransId);
        }
        return this.trans;
    }

    @Setter
    @ToString.Exclude
    private List<DivRefundTrans> divRefundTransList;

    @Setter
    private DivRefundTransDao divRefundTransDao;

    public List<DivRefundTrans> getDivRefundTransList(){
        if (null == this.divRefundTransList && null != this.divRefundTransDao){
            this.divRefundTransList = this.divRefundTransDao.retrieveByDivPayTransId(this.id);
        }
        return this.divRefundTransList;
    }

    /**
     * 已经退回和正在处理中的分账退回总额
     * @author Ming Qiu
     * <p>
     * date: 2022-11-15 15:24
     * @return
     */
    public Long getRefundAmount(){
        return this.getDivRefundTransList().stream()
                .filter(trans -> DivRefundTrans.CANCEL != trans.getStatus() || DivRefundTrans.FAIL == trans.getStatus())
                .map(DivRefundTrans::getAmount)
                .reduce((x,y)->x + y).get();
    }
}
