//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service.channel;

import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.payment.dao.bo.*;
import cn.edu.xmu.oomall.payment.service.channel.dto.*;
import cn.edu.xmu.oomall.payment.service.openfeign.AliPayService;
import cn.edu.xmu.oomall.payment.service.openfeign.AliParam.*;

import java.text.Format;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.ArrayList;


/**
 * 微信支付适配器
 */
@Service("aliPayChannel")
public class AliPayAdaptor implements PayAdaptor {

    private Logger logger = LoggerFactory.getLogger(AliPayAdaptor.class);

    @Resource
    private AliPayService aliPayService;

    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");



//    public AliPayAdaptor(AliPayService aliPayService) {
//        this.aliPayService = aliPayService;
//    }

    /**
     * 支付交易
     */

    @Override
    public PostPayTransAdaptorDto createPayment(PayTrans payTrans) {
        PublicRequestParam param = new PublicRequestParam();
        AliPostTransParam p = new AliPostTransParam();
        ShopChannel shop = payTrans.getShopChannel();
        assert shop != null : "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null : "channel不能为空";
        /*set param*/
        p.setOut_trade_no(payTrans.getOutNo());
        p.setSubject("订单");
        p.setTotal_amount(String.valueOf(payTrans.getAmount()));
        /*set public param*/

        param.setPublicParam(channel.getAppid(), "alipay.trade.app.pay",
                channel.getSpMchid(),
                LocalDateTime.now().format(df), JacksonUtil.toJson(p));
        param.setNotify_url(channel.getNotifyUrl());

        AliPostTransRetObj retObj = aliPayService.postTransaction(param).getData();
        PostPayTransAdaptorDto postPayTransAdaptorDto = new PostPayTransAdaptorDto();
        /*set ret*/
        postPayTransAdaptorDto.setPrepayId(null);
        return postPayTransAdaptorDto;
    }

    /**
     * 查询订单信息，用于下单后未收到微信返回信息
     * 更新payTrans中的数据
     */
    @Override
    public GetPayTransAdaptorDto returnOrderByOutNo(PayTrans payTrans) {
        PublicRequestParam param = new PublicRequestParam();
        AliGetTransParam p = new AliGetTransParam();
        ShopChannel shop = payTrans.getShopChannel();
        assert shop != null : "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null : "channel不能为空";
        /*set param*/
        p.setOut_trade_no(payTrans.getOutNo());
        assert p.getOut_trade_no() != null: "out_trade_no不能为空";
        /*set public param*/
        param.setPublicParam(channel.getAppid(), "alipay.trade.query",
                channel.getSpMchid(),
                LocalDateTime.now().format(df), JacksonUtil.toJson(p));

        AliGetTransRetObj retObj = aliPayService.getOrderByOutNo(param).getData();
        GetPayTransAdaptorDto ret = new GetPayTransAdaptorDto();
        /*set ret*/

        return ret;
    }


    /**
     * 买家查询名下订单信息
     */
    @Override
    public GetPayTransAdaptorDto returnOrderByTransId(PayTrans payTrans) {
        PublicRequestParam param = new PublicRequestParam();
        AliGetTransParam p = new AliGetTransParam();
        ShopChannel shop = payTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        /*set param*/
        p.setTransId(payTrans.getTransNo());
        assert p.getTransId() != null: "transId不能为空";
        /*set public param*/
        param.setPublicParam(channel.getAppid(), "alipay.trade.query",
                channel.getSpMchid(),
                LocalDateTime.now().format(df), JacksonUtil.toJson(p));

        AliGetTransRetObj retObj = aliPayService.getOrderByTransId(param).getData();
        GetPayTransAdaptorDto ret = new GetPayTransAdaptorDto(); // 返回的商品信息
        /*set ret*/
        ret.setAmount((long) (retObj.getTotal_amount() * 100));
        return ret;
    }

    /**
     * 取消订单
     */
    @Override
    public CancelOrderAdaptorDto cancelOrder(PayTrans payTrans) {
        PublicRequestParam param = new PublicRequestParam();
        AliCancelOrderParam p = new AliCancelOrderParam();
        ShopChannel shop = payTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        /*set param*/
        p.setOut_trade_no(payTrans.getOutNo());
        p.setTrade_no(payTrans.getTransNo());
        assert p.getTrade_no() != null || p.getOut_trade_no() != null:
                "trade_no和out_trade_no不能都为空";
        /*set public param*/
        param.setPublicParam(channel.getAppid(), "alipay.trade.close",
                channel.getSpMchid(),
                LocalDateTime.now().format(df), JacksonUtil.toJson(p));
        
        AliCancelOrderRetObj retObj = aliPayService.cancelOrder(param).getData();
        CancelOrderAdaptorDto ret = new CancelOrderAdaptorDto();
        /*set ret*/
        return ret;
    }

    /**
     * 退款交易
     */
    @Override
    public PostRefundAdaptorDto createRefund(RefundTrans refundTrans) {
        PublicRequestParam param = new PublicRequestParam();
        AliPostRefundParam p = new AliPostRefundParam();
        PayTrans payTrans = refundTrans.getPayTrans();
        assert payTrans != null: "payTrans不能为空";
        ShopChannel shop = refundTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        /*set param*/
        p.setRefund_amount((double) (refundTrans.getAmount() / 100)); // 默认用分存储，但支付宝以元为单位
        p.setTrade_no(payTrans.getTransNo());
        p.setOut_request_no(refundTrans.getOutNo()); // 退款单号
        p.setOut_trade_no(payTrans.getOutNo());
        assert p.getOut_trade_no() != null || p.getTrade_no() != null:
                "out_trade_no和trade_no不能都为空";
        /*set public param*/
        param.setPublicParam(channel.getAppid(), "alipay.trade.refund",
                channel.getSpMchid(),
                LocalDateTime.now().format(df), JacksonUtil.toJson(p));

        AliPostRefundRetObj retObj = aliPayService.postRefundTransaction(param).getData();
        PostRefundAdaptorDto ret = new PostRefundAdaptorDto();
        /*set ret*/
        ret.setStatus(null);
        ret.setUserReceivedAccount(retObj.getBuyer_user_id());
        ret.setAmount((long) (retObj.getRefund_fee() * 100));
        ret.setTransNo(retObj.getTrade_no());
        ret.setSuccessTime(LocalDateTime.now());
        return ret;
    }

    /**
     * 管理员查询退款信息
     */
    @Override
    public GetRefundAdaptorDto returnRefund(RefundTrans refundTrans) {
        PublicRequestParam param = new PublicRequestParam();
        AliGetRefundParam p = new AliGetRefundParam();
        PayTrans payTrans = refundTrans.getPayTrans();
        assert payTrans != null: "payTrans不能为空";
        ShopChannel shop = refundTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        /*set param*/
        if (refundTrans.getTransNo() == null)
            p.setOut_request_no(payTrans.getOutNo());
        else
            p.setOut_request_no(refundTrans.getTransNo());
        p.setOut_trade_no(payTrans.getOutNo());
        p.setTrade_no(payTrans.getTransNo());
        assert p.getTrade_no() != null || p.getOut_trade_no() != null:
                "trade_no和out_trade_no不能都为空";
        /*set public param*/
        param.setPublicParam(channel.getAppid(), "alipay.trade.fastpay.refund.query",
                channel.getSpMchid(),
                LocalDateTime.now().format(df), JacksonUtil.toJson(p));

        AliGetRefundRetObj retObj = aliPayService.getRefund(param).getData();
        GetRefundAdaptorDto ret = new GetRefundAdaptorDto();
        /*set ret*/
        ret.setAmount((long) (retObj.getRefund_amount() * 100));
        return ret;
    }


    /**
     * 分账交易
     */
    @Override
    public PostDivPayAdaptorDto createDivPay(DivPayTrans divPayTrans) {
        PublicRequestParam param = new PublicRequestParam();
        AliPostDivPayParam p = new AliPostDivPayParam();
        PayTrans payTrans = divPayTrans.getTrans();
        assert payTrans != null: "payTrans不能为空";
        ShopChannel shop = divPayTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        /*set param*/
        List<AliPostDivPayParam.OpenApiRoyaltyDetailInfoPojo> royalties = new ArrayList<>();
        AliPostDivPayParam.OpenApiRoyaltyDetailInfoPojo royalty = p.new OpenApiRoyaltyDetailInfoPojo();
        royalty.setTrans_out(shop.getSubMchid());
        royalty.setTrans_in(channel.getSpMchid());
        royalties.add(royalty);

        p.setOut_request_no(divPayTrans.getOutNo());
        p.setTrade_no(payTrans.getTransNo());
        p.setRoyalty_parameters(royalties);
        /*set public param*/
        param.setPublicParam(channel.getAppid(), "alipay.trade.order.settle",
                channel.getSpMchid(),
                LocalDateTime.now().format(df), JacksonUtil.toJson(p));

        AliPostDivPayRetObj retObj = aliPayService.postDivPay(param).getData();
        PostDivPayAdaptorDto ret = new PostDivPayAdaptorDto();
        /*set ret*/
        return ret;
    }


    /**
     * 查询分账信息
     */
    @Override
    public GetDivPayAdaptorDto returnDivPay(DivPayTrans divPayTrans) {
        PublicRequestParam param = new PublicRequestParam();
        AliGetDivPayParam p = new AliGetDivPayParam();
        PayTrans payTrans = divPayTrans.getTrans();
        assert payTrans != null: "payTrans不能为空";
        ShopChannel shop = divPayTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        /*set param*/
        p.setSettle_no(divPayTrans.getTransNo());
        p.setOut_request_no(divPayTrans.getOutNo());
        p.setTrade_no(payTrans.getTransNo());
        assert p.getSettle_no() != null || 
                (p.getOut_request_no() != null && p.getTrade_no() != null):
                "settle_no不能为空，或者out_request_no和trade_no not null都不为空";
        
        /*set public param*/
        param.setPublicParam(channel.getAppid(), "alipay.trade.order.settle.query",
                channel.getSpMchid(),
                LocalDateTime.now().format(df), JacksonUtil.toJson(p));

        AliGetDivPayRetObj retObj = aliPayService.getDivPay(param).getData();
        GetDivPayAdaptorDto ret = new GetDivPayAdaptorDto();
        /*set ret*/
        ret.setAmount((long) (retObj.getRoyalty_detail_list().get(0).getAmount() * 100));
        return ret;
    }

    /**
     * 分账退款
     */
    @Override
    public PostDivRefundAdaptorDto createDivRefund(DivRefundTrans divRefundTrans) {
        PublicRequestParam param = new PublicRequestParam();
        AliPostRefundParam p = new AliPostRefundParam();
        RefundTrans refundTrans = divRefundTrans.getRefundTrans();
        assert refundTrans != null: "refundTrans不能为空";
        PayTrans payTrans = refundTrans.getPayTrans();
        assert payTrans != null: "payTrans不能为空";
        ShopChannel shop = divRefundTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        AliPostRefundParam.OpenApiRoyaltyDetailInfoPojo refund_param = p.new OpenApiRoyaltyDetailInfoPojo();
        List<AliPostRefundParam.OpenApiRoyaltyDetailInfoPojo> refund_params = new ArrayList<>();
        /*set param*/
        refund_param.setTrans_in(shop.getSubMchid());
        refund_param.setAmount((double) divRefundTrans.getAmount() / 100);
        refund_params.add(refund_param);
        refund_param.setTrans_out(channel.getSpMchid());
        // 退款金额设为分账金额
        p.setRefund_amount((double) (divRefundTrans.getAmount() / 100)); // 默认用分存储，但支付宝以元为单位
        p.setTrade_no(payTrans.getTransNo()); // 订单号
        p.setOut_request_no(refundTrans.getTransNo()); // 退款单号
        p.setOut_trade_no(payTrans.getOutNo());
        p.setRefund_royalty_parameters(refund_params);
        /*set public param*/
        param.setPublicParam(channel.getAppid(), "alipay.trade.refund",
                channel.getSpMchid(),
                LocalDateTime.now().format(df), JacksonUtil.toJson(p));

        AliPostRefundRetObj retObj = aliPayService.postRefundTransaction(param).getData();
        PostDivRefundAdaptorDto ret = new PostDivRefundAdaptorDto();
        /*set ret*/

        return ret;
    }

    /**
     * 查询分账退款
     */
    @Override
    public GetDivRefundAdaptorDto returnDivRefund(DivRefundTrans divRefundTrans) {
        PublicRequestParam param = new PublicRequestParam();
        AliGetRefundParam p = new AliGetRefundParam();
        RefundTrans refundTrans = divRefundTrans.getRefundTrans();
        assert refundTrans != null: "refundTrans不能为空";
        PayTrans payTrans = refundTrans.getPayTrans();
        assert payTrans != null: "payTrans不能为空";
        ShopChannel shop = divRefundTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        /*set param*/
        if (payTrans.getTransNo()==null)
            p.setOut_request_no(payTrans.getOutNo());
        else
            p.setOut_request_no(refundTrans.getTransNo());
        p.setOut_trade_no(payTrans.getOutNo());
        p.setTrade_no(payTrans.getTransNo());
        /*set public param*/
        param.setPublicParam(channel.getAppid(), "alipay.trade.fastpay.refund.query",
                channel.getSpMchid(),
                LocalDateTime.now().format(df), JacksonUtil.toJson(p));

        AliGetRefundRetObj retObj = aliPayService.getRefund(param).getData();
        GetDivRefundAdaptorDto ret = new GetDivRefundAdaptorDto();
        /*set ret*/
        ret.setAmount((long) (retObj.getRefund_amount() * 100));
        ret.setOutNo(retObj.getOut_trade_no());
        ret.setSuccessTime(LocalDateTime.now());
        ret.setStatus(retObj.getDivRefundByteResult());
        ret.setTransNo(retObj.getTrade_no());
        return ret;
    }

    @Override
    public List<CheckResultDto> checkTransaction(List<? extends Transaction> trans) {
        List<CheckResultDto> ret = new ArrayList<>();
        if (trans.get(0).getClass() == PayTrans.class) {
            trans.forEach(payTrans -> {
                GetPayTransAdaptorDto dto = returnOrderByTransId((PayTrans) payTrans);
                ret.add(new CheckResultDto(payTrans.getId(), PayTrans.class.getName(), dto.getAmount() - payTrans.getAmount()));
            });
        }
        if (trans.get(0).getClass() == RefundTrans.class) {
            trans.forEach(refundTrans -> {
                GetRefundAdaptorDto dto = returnRefund((RefundTrans) refundTrans);
                ret.add(new CheckResultDto(refundTrans.getId(), RefundTrans.class.getName(), dto.getAmount() - refundTrans.getAmount()));
            });
        }
        if (trans.get(0).getClass() == DivPayTrans.class) {
            trans.forEach(divPayTrans -> {
                GetDivPayAdaptorDto dto = returnDivPay((DivPayTrans) divPayTrans);
                ret.add(new CheckResultDto(divPayTrans.getId(), DivPayTrans.class.getName(), dto.getAmount() - divPayTrans.getAmount()));
            });
        }
        if (trans.get(0).getClass() == DivRefundTrans.class) {
            trans.forEach(divRefundTrans -> {
                GetDivRefundAdaptorDto dto = returnDivRefund((DivRefundTrans) divRefundTrans);
                ret.add(new CheckResultDto(divRefundTrans.getId(), DivRefundTrans.class.getName(), dto.getAmount() - divRefundTrans.getAmount()));
            });
        }
        return ret;
    }
}
