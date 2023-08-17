//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.payment.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.controller.vo.ShopChannelVo;
import cn.edu.xmu.oomall.payment.dao.bo.Channel;
import cn.edu.xmu.oomall.payment.dao.bo.ShopChannel;
import cn.edu.xmu.oomall.payment.service.ChannelService;
import cn.edu.xmu.oomall.payment.service.dto.*;
import cn.edu.xmu.oomall.payment.service.RefundService;
import cn.edu.xmu.oomall.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;

/**
 * 管理人员的接口
 */
@RestController
@RequestMapping(value = "/shops/{shopId}", produces = "application/json;charset=UTF-8")
public class AdminPaymentController {

    private final Logger logger = LoggerFactory.getLogger(AdminPaymentController.class);

    private PaymentService paymentService;
    private RefundService refundService;

    private ChannelService channelService;

    @Autowired
    public AdminPaymentController(PaymentService paymentService, RefundService refundService, ChannelService channelService) {
        this.paymentService = paymentService;
        this.refundService = refundService;
        this.channelService = channelService;
    }

    @GetMapping("/ledgers")
    @Audit(departName = "shops")
    public ReturnObject retrieveLedge(@PathVariable Long shopId,
                                      @RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beginTime,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                      @RequestParam(required = false, defaultValue = "0") Integer type,
                                      @RequestParam(required = false) Long channelId,
                                      @RequestParam(required = false,defaultValue = "1") Integer page,
                                      @RequestParam(required = false,defaultValue = "10") Integer pageSize){
            PageDto<LedgerDto> pageDto = paymentService.retrieveLedge(shopId, beginTime, endTime, type.byteValue(), channelId, page, pageSize);
        return new ReturnObject(pageDto);
    }

    /**
     *获得商铺所有的支付渠道
     */
    @GetMapping("/shopchannels")
    public ReturnObject retrieveShopChannel(@PathVariable Long shopId,
                                            @RequestParam(required = false, defaultValue = "1") Integer page,
                                            @RequestParam(required = false,defaultValue = "10") Integer pageSize) {
        PageDto<ShopChannelDto> pageDto = paymentService.retrieveShopChannel(shopId, page, pageSize);
        return new ReturnObject(pageDto);
    }

    /**
     *签约支付渠道
     */
    @Audit(departName = "shops")
    @PostMapping("/channels/{id}/shopchannels")
    public ReturnObject createShopChannels(@PathVariable("shopId") Long shopId,
                                           @PathVariable("id") Long id,
                                           @Validated @RequestBody ShopChannelVo shopChannelVo,
                                           @LoginUser UserDto user){
        String subMchid = shopChannelVo.getSubMchid();
        SimpleShopChannelDto dto = paymentService.createShopChannel(shopId, id, subMchid, user);

        return new ReturnObject(ReturnNo.CREATED, dto);
    }

    /**
     * 获得商铺支付渠道
     */
    @GetMapping("/shopchannels/{id}")
    public ReturnObject retrieveShopChannels(@PathVariable("shopId") Long shopId,
                                             @PathVariable("id") Long id){
        FullShopChannelDto dto = paymentService.findShopChannel(shopId, id);
        return new ReturnObject(dto);
    }

    /**
     *解约店铺的账户
     */
    @DeleteMapping("/shopchannels/{id}")
    public ReturnObject delShopChannel(@PathVariable("shopId") Long shopId,
                                       @PathVariable("id") Long id){
        ReturnObject ret = paymentService.delShopChannel(shopId, id);
        return ret;
    }

    /**
     *修改支付渠道
     */
    @Audit(departName = "shops")
    @PutMapping("/shopchannels/{id}")
    public ReturnObject updateShopChannel(@PathVariable("shopId") Long shopId,
                                          @PathVariable("id") Long id,
                                          @Validated @RequestBody ShopChannelVo shopChannelVo,
                                          @LoginUser UserDto user) {
        ReturnObject ret = paymentService.updateShopChannelSubMchId(shopId, id, shopChannelVo.getSubMchid(), user);
        return ret;
    }

    /**
     *修改支付渠道为有效
     */
    @Audit(departName = "shops")
    @PutMapping("/shopchannels/{id}/valid")
    public ReturnObject updateShopChannelValid(@PathVariable("shopId") Long shopId,
                                               @PathVariable("id") Long id,
                                               @LoginUser UserDto user) {
        ReturnObject returnObject = paymentService.updateShopChannelStatus(shopId, id, ShopChannel.VALID, user);
        return returnObject;
    }

    /**
     *修改支付渠道为无效
     */
    @Audit(departName = "shops")
    @PutMapping("/shopchannels/{id}/invalid")
    public ReturnObject updateShopChannelInvalid(@PathVariable("shopId") Long shopId,
                                                 @PathVariable("id") Long id,
                                                 @LoginUser UserDto user) {
        ReturnObject returnObject = paymentService.updateShopChannelStatus(shopId, id, ShopChannel.INVALID, user);
        return returnObject;
    }

    @Audit(departName = "shops")
    @PutMapping("/channels/{id}/valid")
    public ReturnObject updateChannelValid(@PathVariable("shopId") Long shopId,
                                           @PathVariable("id") Long channelId,
                                           @LoginUser UserDto user) {
        if(PLATFORM != shopId){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "支付渠道", channelId, shopId));
        }
        ReturnObject ret = channelService.updateChannelStatus(channelId, Channel.VALID, user);
        return ret;
    }

    @Audit(departName = "shops")
    @PutMapping("/channels/{id}/invalid")
    public ReturnObject updateChannelInvalid(@PathVariable("shopId") Long shopId,
                                           @PathVariable("id") Long channelId,
                                           @LoginUser UserDto user) {
        if(PLATFORM != shopId){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "支付渠道", channelId, shopId));
        }
        ReturnObject ret = channelService.updateChannelStatus(channelId, Channel.INVALID, user);
        return ret;
    }

    @GetMapping("/refunds/{id}")
    @Audit(departName = "shops")
    public ReturnObject findRefundById(@PathVariable Long shopId, @PathVariable Long id) {
        RefundTransDto refundDto = refundService.findRefundById(shopId, id);
        return new ReturnObject(refundDto);
    }

    @GetMapping("/payments/{id}/refunds")
    @Audit(departName = "shops")
    public ReturnObject retrieveRefunds(@PathVariable Long shopId,
                                        @PathVariable Long id,
                                        @RequestParam(required = false,defaultValue = "1") Integer page,
                                        @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        PageDto<SimpleRefundDto> pageDto = paymentService.retrieveRefunds(shopId, id, page, pageSize);
        return new ReturnObject(pageDto);
    }


    @GetMapping("/shopchannels/{id}/divrefundtrans")
    @Audit(departName = "shops")
    public ReturnObject retrieveDivRefundTrans(@PathVariable Long shopId,
                                               @PathVariable Long id,
                                               @RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beginTime,
                                               @RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  LocalDateTime endTime,
                                               @RequestParam(required = false, defaultValue = "1") Integer page,
                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        logger.debug("retrieveDivRefundTrans: beginTime = {}, endTime = {}, page = {}, pageSize = {}",beginTime, endTime, page, pageSize);
        return new ReturnObject(refundService.retrieveDivRefunds(shopId, beginTime, endTime, id, page, pageSize));
    }

    /**
     * 获取指定台账
     * author:zwr
     */
    @GetMapping("/ledgers/{id}")
    @Audit(departName = "shops")
    public ReturnObject retrieveLedger(@PathVariable Long shopId,
                                       @PathVariable Long id
                                       ){
        return new ReturnObject(paymentService.retrieveLedger(shopId,id));
    }

    /**
     * 调账
     * author:zwr
     */
    @PutMapping("/ledgers/{id}")
    @Audit(departName = "shops")
    public ReturnObject adjust(@PathVariable Long shopId,
                               @PathVariable Long id,
                               @LoginUser UserDto user){
        ReturnObject ret =  paymentService.adjust(shopId, id, user);
        return ret;
    }

    @GetMapping("/shopchannels/{id}/divpaytrans")
    @Audit(departName = "shops")
    public ReturnObject retrieveDivPayTrans(@PathVariable Long shopId,
                                            @PathVariable Long id,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beginTime,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                            @RequestParam(required = false, defaultValue = "1") Integer page,
                                            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return new ReturnObject(paymentService.retrieveDivPayTrans(shopId, beginTime, endTime, id, page, pageSize));
    }

    @GetMapping("/shopchannels/{id}/payments")
    @Audit(departName = "shops")
    public ReturnObject retrievePayments(@PathVariable Long shopId,
                                         @PathVariable Long id,
                                         @RequestParam(required = false) String transNo,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beginTime,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                         @RequestParam(required = false) Long adjustId,
                                         @RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return new ReturnObject(paymentService.retrievePayments(shopId, id, transNo, beginTime, endTime, adjustId, page, pageSize));
    }
}
