package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.bo.RefundTrans;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class RefundTransDaoTest {
    @Autowired
    private RefundTransDao refundTransDao;

    @Test
    public void retrieveObjByPayTransId1() {
        RefundTrans refund = refundTransDao.retrieveByPayTransId(551L, 1, 10).getList().get(0);
        assertThat(refund.getPayTransId()).isEqualTo(551L);
    }
}
