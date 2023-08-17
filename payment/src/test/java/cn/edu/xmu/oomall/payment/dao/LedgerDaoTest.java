package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.bo.Ledger;
import cn.edu.xmu.oomall.payment.mapper.generator.LedgerPoMapper;
import cn.edu.xmu.oomall.payment.mapper.generator.po.LedgerPo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class LedgerDaoTest {
    @MockBean
    private LedgerPoMapper ledgerPoMapper;

    @Autowired
    private LedgerDao ledgerDao;

    @MockBean
    private TransactionDao transactionDao;
    //adjustObjById(Ledger obj, Long userId, String userName)
    @Test
    public void adjustById1(){
        Ledger p = new Ledger();
        p.setId(1L);
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("11");
        Mockito.when(ledgerPoMapper.updateByPrimaryKeySelective(Mockito.any())).thenReturn(0);
        assertThrows(BusinessException.class,()->ledgerDao.save(p,userDto));
    }

    @Test
    public void adjustById2(){
        LedgerPo p = new LedgerPo();
        p.setId(501L);
        Ledger p1 = new Ledger();
        p1.setId(501L);
        Byte b = 1;
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("11");
        Mockito.when(ledgerPoMapper.updateByPrimaryKeySelective(Mockito.any())).thenReturn(1);
        LocalDateTime t = LocalDateTime.now();
    }

}
