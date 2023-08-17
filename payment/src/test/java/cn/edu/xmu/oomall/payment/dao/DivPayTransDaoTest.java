package cn.edu.xmu.oomall.payment.dao;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.mapper.generator.DivPayTransPoMapper;
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
public class DivPayTransDaoTest {
    @MockBean
    private DivPayTransPoMapper divPayTransPoMapper;
    @Autowired
    private DivPayTransDao divPayTransDao;
    @Test
    public void test(){
        Mockito.when(divPayTransPoMapper.selectByPrimaryKey(Mockito.any())).thenReturn(null);
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("11");
        assertThrows(BusinessException.class,()->divPayTransDao.saveById(1L,userDto, LocalDateTime.MIN));
    }
}
