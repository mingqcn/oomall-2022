package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.mapper.generator.DivRefundTransPoMapper;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.dao.bo.DivRefundTrans;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import static cn.edu.xmu.javaee.core.model.Constants.END_TIME;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class DivRefundTransDaoTest {

    @Autowired
    private DivRefundTransDao divRefundTransDao;
    @MockBean
    private DivRefundTransPoMapper divRefundTransPoMapper;

    @MockBean
    private RedisUtil redisUtil;

/*    @Test
    public void retrieveByRefundTransId() {
        DivRefundTrans divRefundTrans = divRefundTransDao.findByRefundTransId(501L);
        assertThat(divRefundTrans.getRefundTransId()).isEqualTo(501L);
    }*/

    @Test
    public void test() {
        Mockito.when(divRefundTransPoMapper.selectByPrimaryKey(Mockito.any())).thenReturn(null);
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("11");
        assertThrows(BusinessException.class, () -> divRefundTransDao.saveById(1L, userDto, LocalDateTime.MIN));
    }
}
