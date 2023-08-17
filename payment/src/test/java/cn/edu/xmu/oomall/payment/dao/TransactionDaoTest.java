package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.dao.bo.Ledger;
import cn.edu.xmu.oomall.payment.mapper.generator.RefundTransPoMapper;
import cn.edu.xmu.oomall.payment.mapper.generator.po.RefundTransPo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class TransactionDaoTest {
    @Autowired
    private TransactionDao transactionDao;

    @MockBean
    RefundTransPoMapper poMapper;

    //adjustObjById(Long id, Byte type,Long userId,String userName,LocalDateTime adjustTime)
    @Test
    public void adjustObjById1(){
        Long id =1L;
        Byte type =1;
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("11");
        LocalDateTime adjustTime = LocalDateTime.now();
        assertThrows(RuntimeException.class, () ->transactionDao.saveById(id,type,userDto,adjustTime));
    }

    @Test
    public void adjustObjById2(){
        Long id =501L;
        Byte type =2;
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("11");
        LocalDateTime adjustTime = LocalDateTime.now();
        assertThrows(BusinessException.class, () ->transactionDao.saveById(id,type,userDto,adjustTime));
    }

}
