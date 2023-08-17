package cn.edu.xmu.oomall.payment.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.PaymentApplication;
import cn.edu.xmu.oomall.payment.service.dto.DivRefundTransDto;
import cn.edu.xmu.oomall.payment.service.dto.RefundStatesDto;
import cn.edu.xmu.oomall.payment.service.dto.RefundTransDto;
import com.github.pagehelper.util.PageObjectUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static cn.edu.xmu.javaee.core.model.Constants.DATE_TIME_FORMATTER;
import static cn.edu.xmu.javaee.core.model.Constants.END_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = PaymentApplication.class)
@Transactional
public class RefundServiceTest {
    @Autowired
    private RefundService refundService;

    @MockBean
    private RedisUtil redisUtil;

    @Test
    public void retrieveDivRefunds1() {
        LocalDateTime beginTime = LocalDateTime.parse("2021-11-01T12:12:12", DATE_TIME_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse("2025-11-01T12:12:12", DATE_TIME_FORMATTER);
        PageDto<DivRefundTransDto> dto = refundService.retrieveDivRefunds(1L, beginTime, endTime, 501L, 1, 10);
        assertEquals(10, dto.getList().size());
    }

/*    @Test
    void createRefund() {
        UserDto user = new UserDto();
        user.setId(Long.valueOf(2));
        user.setName("test1");
        user.setUserLevel(1);

        RefundTransDto obj = refundService.createRefund(1L, 551L, 100L, user);
        assertEquals(100L, obj.getAmount());
    }*/

    @Test
    public void findRefundById() {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);

        cn.edu.xmu.oomall.payment.service.dto.RefundTransDto refund = refundService.findRefundById(1L, 501L);
        assertEquals(501L, refund.getId());
    }

}
