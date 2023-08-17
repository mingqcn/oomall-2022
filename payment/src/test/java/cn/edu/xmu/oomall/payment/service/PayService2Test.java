//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.*;
import cn.edu.xmu.oomall.payment.service.dto.FullLedgerDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertThrows;
/**
 * @author Wanru Zhuang
 * @date 2022/11/27
 */
@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class PayService2Test {
    @MockBean
    private RefundTransDao refundTransDao;
    @MockBean
    private PayTransDao payTransDao;
    @MockBean
    private DivPayTransDao divPayTransDao;
    @MockBean
    private DivRefundTransDao divRefundTransDao;
    @Autowired
    private PaymentService paymentService;

    @Test
    public void retrieveLedgerTrans1(){
        Byte transType = (byte)1;
        Long transId = 1L;
        FullLedgerDto vo = new FullLedgerDto();
        assertThrows(BusinessException.class,()->paymentService.retrieveLedgerTrans(transType,transId,vo));
    }

    @Test
    public void retrieveLedgerTrans2(){
        Byte transType = (byte)2;
        Long transId = 1L;
        FullLedgerDto vo = new FullLedgerDto();
        assertThrows(BusinessException.class,()->paymentService.retrieveLedgerTrans(transType,transId,vo));
    }
    @Test
    public void retrieveLedgerTrans3(){
        Byte transType = (byte)3;
        Long transId = 1L;
        FullLedgerDto vo = new FullLedgerDto();
        assertThrows(BusinessException.class,()->paymentService.retrieveLedgerTrans(transType,transId,vo));
    }
    @Test
    public void retrieveLedgerTrans4(){
        Byte transType = (byte)4;
        Long transId = 1L;
        FullLedgerDto vo = new FullLedgerDto();
        assertThrows(BusinessException.class,()->paymentService.retrieveLedgerTrans(transType,transId,vo));
    }

    @Test
    public void retrieveLedger1(){
        Long shopId =2L;
        Long id = 2L;
        assertThrows(BusinessException.class,()->paymentService.retrieveLedger(shopId,id));
    }
    @Test
    public void doAdjust1(){
        Long shopId =3L;
        Long id = 2L;
        Long userId = 111L;
        String userName = "asss";
        UserDto user = new UserDto();
        user.setId(userId);
        user.setName(userName);
        assertThrows(BusinessException.class,()->paymentService.adjust(shopId,id,user));
    }

    @Test
    public void query1(){
        Byte transType = 4;
        Long transId = 501L;
        Mockito.when(divRefundTransDao.findById(transId)).thenReturn(null);
        assertThrows(BusinessException.class,()->paymentService.retrieveLedgerTrans(transType,transId,null));
    }

    @Test
    public void query2(){
        Byte transType = 1;
        Long transId = 501L;
        Mockito.when(payTransDao.findById(Mockito.any())).thenReturn(null);
        assertThrows(BusinessException.class,()->paymentService.retrieveLedgerTrans(transType,transId,null));
    }

    @Test
    public void query3(){
        Byte transType = 2;
        Long transId = 501L;
        Mockito.when(refundTransDao.findById(Mockito.any())).thenReturn(null);
        assertThrows(BusinessException.class,()->paymentService.retrieveLedgerTrans(transType,transId,null));
    }

    @Test
    public void query4(){
        Byte transType = 3;
        Long transId = 501L;
        Mockito.when(divPayTransDao.findById(Mockito.any())).thenReturn(null);
        assertThrows(BusinessException.class,()->paymentService.retrieveLedgerTrans(transType,transId,null));
    }

}
