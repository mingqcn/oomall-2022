//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.bo.PayTrans;
import cn.edu.xmu.oomall.payment.dao.bo.RefundTrans;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class PayTransDaoTest {

    @Autowired
    private PayTransDao payTransDao;

    @Test
    public void saveById1(){

        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);
        PayTrans obj = new PayTrans();
        obj.setId(Long.valueOf(1));
        obj.setOutNo("aaaa");

        assertThrows(BusinessException.class, () ->payTransDao.saveById(obj, user));
    }

    @Test
    public void findById1(){
        PayTrans obj = payTransDao.findById(551L);
        assertEquals(551, obj.getId());
        assertEquals("12222", obj.getTransNo());
        assertEquals(100, obj.getAmount());

        List<RefundTrans> refundTransList = obj.getRefundTransList();
        assertNotNull(refundTransList);
        assertEquals(1, refundTransList.size());
        assertEquals(100, refundTransList.get(0).getAmount());
        assertEquals(100, obj.getRefundAmount());
    }
}
