//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.dao.bo.Ledger;
import cn.edu.xmu.oomall.payment.dao.bo.PayTrans;
import cn.edu.xmu.oomall.payment.dao.bo.RefundTrans;
import cn.edu.xmu.oomall.payment.dao.bo.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public class TransactionDao {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDao.class);

    private PayTransDao payTransDao;
    private RefundTransDao refundTransDao;
    private DivPayTransDao divPayTransDao;
    private DivRefundTransDao divRefundTransDao;

    @Autowired
    public TransactionDao(PayTransDao payTransDao, RefundTransDao refundTransDao, DivPayTransDao divPayTransDao, DivRefundTransDao divRefundTransDao) {
        this.payTransDao = payTransDao;
        this.refundTransDao = refundTransDao;
        this.divPayTransDao = divPayTransDao;
        this.divRefundTransDao = divRefundTransDao;
    }

    public Transaction findById(Long id, Byte type){
        if (Ledger.ALL_TYPE == type || Ledger.PAY_TYPE == type){
            return this.payTransDao.findById(id);
        }

        if (Ledger.ALL_TYPE == type || Ledger.REFUND_TYPE == type){
            return this.refundTransDao.findById(id);
        }

        if (Ledger.ALL_TYPE == type || Ledger.DIVPAY_TYPE == type){
            return this.divPayTransDao.findById(id);
        }

        if (Ledger.ALL_TYPE == type || Ledger.DIVREFUND_TYPE == type){
            return this.divRefundTransDao.findById(id);
        }
        return null;
    }

    public void saveById(Long id, Byte type, UserDto user, LocalDateTime adjustTime) throws RuntimeException {

        if (Ledger.ALL_TYPE == type || Ledger.PAY_TYPE == type) {
            this.payTransDao.saveById(id, user, adjustTime);
        }
        if (Ledger.ALL_TYPE == type || Ledger.REFUND_TYPE == type) {
            this.refundTransDao.saveById(id, user, adjustTime);
        }
        if (Ledger.ALL_TYPE == type || Ledger.DIVPAY_TYPE == type) {
            this.divPayTransDao.saveById(id, user, adjustTime);
        }
        if (Ledger.ALL_TYPE == type || Ledger.DIVREFUND_TYPE == type) {
            this.divRefundTransDao.saveById(id, user, adjustTime);
        }
    }

    public void saveById(Long transId, String className, Byte status,UserDto userDto) throws RuntimeException {
        if (className.equals(PayTrans.class.getName()))
            this.payTransDao.saveById(transId, status,userDto);
        else if (className.equals(RefundTrans.class.getName()))
            this.refundTransDao.saveById(transId, status,userDto);
    }
}
