package cn.edu.xmu.oomall.payment.controller;

import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.StatusDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.controller.vo.AlipayNotifyVo;
import cn.edu.xmu.oomall.payment.controller.vo.WepayNotifyVo;
import cn.edu.xmu.oomall.payment.dao.bo.PayTrans;
import cn.edu.xmu.oomall.payment.dao.bo.RefundTrans;
import cn.edu.xmu.oomall.payment.service.ChannelService;
import cn.edu.xmu.oomall.payment.service.PaymentService;
import cn.edu.xmu.oomall.payment.service.RefundService;
import cn.edu.xmu.oomall.payment.service.dto.PayNotifyDto;
import cn.edu.xmu.oomall.payment.service.dto.SimpleChannelDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@RestController
public class CustomerController {
    private final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private ChannelService channelService;

    private PaymentService paymentService;


    @Autowired
    public CustomerController(ChannelService channelService, PaymentService paymentService) {
        this.channelService = channelService;
        this.paymentService = paymentService;
    }

    @GetMapping("/refund/states")
    public ReturnObject retrieveRefundStates() {
        return new ReturnObject(RefundTrans.STATUSNAMES.keySet().stream().map(key -> new StatusDto(key, RefundTrans.STATUSNAMES.get(key))).collect(Collectors.toList()));
    }

    /**
     *获得有效的支付渠道
     */
    @GetMapping("/channels")
    public ReturnObject retrieveChannel(@RequestParam(required = true) Long shopId,
                                        @RequestParam(required = false,defaultValue = "1") Integer page,
                                        @RequestParam(required = false,defaultValue = "10") Integer pageSize) {
        PageDto<SimpleChannelDto> pageDto = channelService.retrieveValidChannel(shopId, page, pageSize);
        return new ReturnObject(pageDto);
    }

    @GetMapping("/payments/states")
    public ReturnObject retrievePaymentStates() {
        return paymentService.retrievePaymentStates();
    }

    @PostMapping("/notify/payments/alipay")
    public ReturnObject alipayNotify(@Validated @RequestBody AlipayNotifyVo alipayNotifyVo,
                                     @LoginUser UserDto user) {
        PayNotifyDto payNotifyDto = new PayNotifyDto();
        String status = alipayNotifyVo.getTradeStatus();
        if(status.equals("TRADE_SUCCESS")) {
            payNotifyDto.setStatus(PayTrans.SUCCESS);
        } else if(status.equals("TRADE_CLOSED")) {
            payNotifyDto.setStatus(PayTrans.CANCEL);
        } else {
            payNotifyDto.setStatus(PayTrans.NEW);
        }

        LocalDateTime gmtPayment = LocalDateTime.parse(alipayNotifyVo.getGmtPayment(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        payNotifyDto.setOutTradeNo(alipayNotifyVo.getOutTradeNo());
        payNotifyDto.setTransNo(alipayNotifyVo.getTradeNo());
        payNotifyDto.setSuccessTime(gmtPayment);
        payNotifyDto.setAmount(alipayNotifyVo.getReceiptAmount());

        return paymentService.updatePaymentByNotify(payNotifyDto, user);
    }


    @PostMapping("/notify/payments/wepay")
    public ReturnObject wepayNotify(@Validated @RequestBody WepayNotifyVo wepayNotifyVo,
                                    @LoginUser UserDto user) {
        PayNotifyDto dto = new PayNotifyDto();
        String status = wepayNotifyVo.getResource().getTradeState();
        if(status.equals("SUCCESS")) {
            dto.setStatus(PayTrans.SUCCESS);
        } else if(status.equals("CLOSED")) {
            dto.setStatus(PayTrans.CANCEL);
        } else if(status.equals("PAYERROR")) {
            dto.setStatus(PayTrans.FAIL);
        } else {
            dto.setStatus(PayTrans.NEW);
        }

        System.out.println(wepayNotifyVo.getResource().getSuccessTime());
        LocalDateTime successTime = LocalDateTime.parse(wepayNotifyVo.getResource().getSuccessTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[XXX][X]"));

        dto.setOutTradeNo(wepayNotifyVo.getResource().getOutTradeNo());
        dto.setTransNo(wepayNotifyVo.getResource().getTransactionId());
        dto.setSuccessTime(successTime);
        dto.setAmount(wepayNotifyVo.getResource().getAmount().getTotal());

        return paymentService.updatePaymentByNotify(dto, user);
    }
}
