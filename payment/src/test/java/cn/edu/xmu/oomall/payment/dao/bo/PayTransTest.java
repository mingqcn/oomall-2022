//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao.bo;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PayTransTest {

    @Test
    public void allowStatus(){
        PayTrans payTrans = new PayTrans();
        payTrans.setStatus(PayTrans.NEW);
        assertTrue(payTrans.allowStatus(PayTrans.CANCEL));
        assertTrue(payTrans.allowStatus(PayTrans.FAIL));
        assertTrue(payTrans.allowStatus(PayTrans.SUCCESS));
        assertFalse(payTrans.allowStatus(PayTrans.WRONG));
        payTrans.setStatus(PayTrans.CANCEL);
        assertFalse(payTrans.allowStatus(PayTrans.WRONG));
        assertFalse(payTrans.allowStatus(PayTrans.SUCCESS));
        assertFalse(payTrans.allowStatus(PayTrans.FAIL));
    }
}
