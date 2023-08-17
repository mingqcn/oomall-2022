//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.controller.vo.IntervalVo;
import cn.edu.xmu.oomall.payment.controller.vo.OrderPayVo;
import cn.edu.xmu.oomall.payment.controller.vo.RefundVo;
import cn.edu.xmu.oomall.payment.controller.vo.TimePeriodVo;
import cn.edu.xmu.oomall.payment.service.RefundService;
import cn.edu.xmu.oomall.payment.service.dto.FullPayTransDto;
import cn.edu.xmu.oomall.payment.service.dto.PayTransDto;
import cn.edu.xmu.oomall.payment.service.PaymentService;
import cn.edu.xmu.oomall.payment.service.dto.RefundTransDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static cn.edu.xmu.javaee.core.model.ReturnNo.LATE_BEGINTIME;

/**
 * 内部的接口
 */
@RestController
@RequestMapping(value = "/internal", produces = "application/json;charset=UTF-8")
public class InternalPaymentController {

    private final Logger logger = LoggerFactory.getLogger(InternalPaymentController.class);

    private PaymentService paymentService;
    private RefundService refundService;

    @Autowired
    public InternalPaymentController(PaymentService paymentService, RefundService refundService) {
        this.paymentService = paymentService;
        this.refundService = refundService;
    }

    @PostMapping("/payments")
    @Audit(departName = "shops")
    public ReturnObject createPayment(@Validated @RequestBody OrderPayVo orderPayVo, @LoginUser UserDto user){
        logger.debug("createPayment: orderPayVo = {}", orderPayVo);
        if (orderPayVo.getTimeEnd().isBefore(orderPayVo.getTimeBegin())){
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, "结束时间不能早于开始时间");
        }
        if ( orderPayVo.getDivAmount() > orderPayVo.getAmount()){
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, "分账金额不能大于支付金额");
        }

        PayTransDto payTransDto =  paymentService.createPayment(orderPayVo.getTimeBegin(),
                orderPayVo.getTimeEnd(), orderPayVo.getSpOpenid(),
                orderPayVo.getShopChannelId(), orderPayVo.getAmount(), orderPayVo.getDivAmount(), user);
        return new ReturnObject(ReturnNo.CREATED, payTransDto);
    }

    @PostMapping("/shops/{shopId}/payments/{id}/refunds")
    @Audit(departName = "shops")
    public ReturnObject createRefund(@PathVariable Long shopId, @PathVariable Long id, @Validated @RequestBody(required = true) RefundVo vo, @LoginUser UserDto user){
        logger.debug("createRefund: shopId = {}, id = {}, vo = {}",shopId, id, vo);
        if (vo.getDivAmount() > vo.getAmount()){
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, "分账退回金额不能大于退款金额");
        }

        RefundTransDto refundTransDto = refundService.createRefund(shopId, id, vo.getAmount(), vo.getDivAmount(), user);
        return new ReturnObject(ReturnNo.CREATED, refundTransDto);
    }

    @GetMapping("/shops/{shopId}/refunds/{id}")
    @Audit(departName = "shops")
    public ReturnObject findRefundById(@PathVariable Long shopId, @PathVariable Long id) {
        RefundTransDto refundDto = refundService.findRefundById(shopId, id);
        return new ReturnObject(refundDto);
    }

    @DeleteMapping("/shops/{shopId}/refunds/{id}")
    @Audit(departName = "shops")
    public ReturnObject cancelRefundById(@PathVariable Long shopId, @PathVariable Long id, @LoginUser UserDto user) {
        refundService.cancelRefundById(shopId, id, user);
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 对账
     * author:zwr
     */
    @PutMapping(value = "/shops/{shopId}/ledgers/check", produces = "application/json;charset=UTF-8")
    @Audit(departName = "shops")
    public ReturnObject Check(@PathVariable Long shopId,
                              @Validated @RequestBody IntervalVo intervalVo,
                              @LoginUser UserDto user) {
        LocalDateTime beginTime = intervalVo.getBeginTime();
        LocalDateTime endTime = intervalVo.getEndTime();
        if (beginTime.isAfter(endTime)) {
            return new ReturnObject(LATE_BEGINTIME, "开始时间不能晚于结束时间");
        }
        ReturnObject ret = paymentService.Check(shopId, beginTime, endTime, user);
        return ret;
    }

    @PutMapping("/payments/div")
    @Audit(departName = "shops")
    public ReturnObject divPayment(@Validated @RequestBody TimePeriodVo timePeriodVo, @LoginUser UserDto user) {
        LocalDateTime timeBegin = timePeriodVo.getBeginTime();
        LocalDateTime timeEnd = timePeriodVo.getEndTime();
        paymentService.divPayment(timeBegin, timeEnd, user);
        return new ReturnObject(ReturnNo.OK);
    }

    @GetMapping("/shops/{shopId}/payments/{id}")
    @Audit(departName = "shops")
    public ReturnObject getPayment(@PathVariable Long shopId, @PathVariable Long id) {
        FullPayTransDto fullPaymentDto = paymentService.findPayment(shopId, id);
        return new ReturnObject(ReturnNo.OK, fullPaymentDto);
    }

    @DeleteMapping("/shops/{shopId}/payments/{id}")
    @Audit(departName = "shops")
    public ReturnObject cancelPayment(@PathVariable Long shopId, @PathVariable Long id, @LoginUser UserDto user) {
        paymentService.cancelPayment(shopId, id, user);
        return new ReturnObject(ReturnNo.OK);
    }
}
