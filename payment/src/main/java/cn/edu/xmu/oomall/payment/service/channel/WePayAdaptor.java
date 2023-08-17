//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service.channel;

import cn.edu.xmu.oomall.payment.dao.bo.Channel;
import cn.edu.xmu.oomall.payment.dao.bo.ShopChannel;
import cn.edu.xmu.oomall.payment.dao.bo.*;
import cn.edu.xmu.oomall.payment.service.channel.dto.*;
import cn.edu.xmu.oomall.payment.service.openfeign.WePayService;
import cn.edu.xmu.oomall.payment.service.openfeign.WeParam.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 微信支付适配器
 * @author Ming Qiu
 * @Modified Wenbo Li
 */
@Service("wePayChannel")
public class WePayAdaptor implements PayAdaptor {

    private static final Logger logger = LoggerFactory.getLogger(WePayAdaptor.class);

    @Resource
    private WePayService wePayService;

    /**
     * 支付交易
     * */
    @Override
    public PostPayTransAdaptorDto createPayment(PayTrans payTrans) {
        WePostTransParam param = new WePostTransParam();
        /*Set param*/
        ShopChannel shop = payTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";

        WePostTransParam.Amount amount1 = param.new Amount();
        amount1.setTotal(payTrans.getAmount());
        amount1.setCurrency("CNY");

        WePostTransParam.Payer payer = param.new Payer();
        payer.setOpenid(payTrans.getSpOpenid());

        param.setMchid(channel.getSpMchid());
        param.setOut_trade_no(payTrans.getOutNo());
        param.setAppid(channel.getAppid());
        param.setDescription("发起支付交易");
        param.setNotify_url(channel.getNotifyUrl());
        param.setAmount(amount1);
        param.setPayer(payer);

        WePostTransRetObj retObj = wePayService.postTransaction(param).getData();
        PostPayTransAdaptorDto ret = new PostPayTransAdaptorDto();
        /*Set ret*/
        ret.setPrepayId(retObj.getPrepayId());
        return ret;
    }

    /**
     * 查询订单信息，用于下单后未收到微信返回信息
     * 更新payTrans中的数据
     * */
    @Override
    public GetPayTransAdaptorDto returnOrderByOutNo(PayTrans payTrans) {
        WeGetTransParam param = new WeGetTransParam();
        ShopChannel shop = payTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        /*set param*/
        param.setMchid(channel.getSpMchid());
        param.setOut_trade_no(payTrans.getOutNo());
        assert param.getOut_trade_no() != null: "out_trade_no不能为空";

        WeGetTransRetObj RetObj = wePayService.getOrderByOutNo(param).getData();
        GetPayTransAdaptorDto ret = new GetPayTransAdaptorDto();
        /*set ret*/
        return ret;
    }


    /**
     * 买家查询名下订单信息
     * */
    @Override
    public GetPayTransAdaptorDto returnOrderByTransId(PayTrans payTrans) {
        WeGetTransParam param = new WeGetTransParam();
        ShopChannel shop = payTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        /*set param*/
        param.setMchid(channel.getSpMchid());
        param.setTransId(payTrans.getTransNo());
        assert param.getTransId() != null: "trans_id不能为空";

        WeGetTransRetObj retObj = wePayService.getOrderByTransId(param).getData();
        GetPayTransAdaptorDto ret = new GetPayTransAdaptorDto(); // 返回的商品信息
        /*set ret*/
        ret.setAmount(retObj.getAmount().getTotal());
        return ret;
    }

    /**
     * 取消订单
     * */
    @Override
    public CancelOrderAdaptorDto cancelOrder(PayTrans payTrans) {
        WeCancelOrderParam param = new WeCancelOrderParam();
        ShopChannel shop = payTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        /*set param*/
        param.setOut_trade_no(payTrans.getOutNo());
        param.setMchid(channel.getSpMchid());

        WeCancelOrderRetObj retObj = wePayService.cancelOrder(param).getData();
        CancelOrderAdaptorDto ret = new CancelOrderAdaptorDto();
        /*set ret*/
        return ret;
    }

    /**
     * 退款交易
     * */
    @Override
    public PostRefundAdaptorDto createRefund(RefundTrans refundTrans) {
        WePostRefundParam param = new WePostRefundParam();
        PayTrans payTrans = refundTrans.getPayTrans();
        assert payTrans != null: "payTrans不能为空";
        /*set param*/

        param.setTransaction_id(payTrans.getTransNo());
        param.setOut_trade_no(payTrans.getOutNo());
        param.setOut_refund_no(refundTrans.getOutNo());
        param.setReason("退款");
        WePostRefundParam.Amount amount = param.new Amount();
        amount.setTotal(refundTrans.getPayTrans().getAmount());
        amount.setCurrency("CNY");
        amount.setRefund(refundTrans.getAmount());
        assert param.getTransaction_id() != null || param.getOut_trade_no() != null:
                "trans_id和out_trade_no不能都为空";

        WePostRefundRetObj retObj = wePayService.postRefundTransaction(param).getData();
        PostRefundAdaptorDto ret = new PostRefundAdaptorDto();
        /*set ret*/
        ret.setAmount(retObj.getAmount().getRefund());
        ret.setUserReceivedAccount(retObj.getUser_received_account());
        ret.setTransNo(retObj.getTransaction_id());
        ret.setSuccessTime(retObj.getSuccess_time());
        ret.setStatus(retObj.getByteStatus());
        return ret;
    }

    /**
     * 管理员查询退款信息
     * */
    @Override
    public GetRefundAdaptorDto returnRefund(RefundTrans refundTrans) {
        WeGetRefundParam param = new WeGetRefundParam();
        /*set param*/
        param.setOut_trade_no(refundTrans.getOutNo());

        WeGetRefundRetObj retObj = wePayService.getRefund(param).getData();
        GetRefundAdaptorDto ret = new GetRefundAdaptorDto();
        /*set ret*/
        ret.setAmount(retObj.getAmount().getRefund());
        return ret;
    }


    /**
     * 分账交易
     * */
    @Override
    public PostDivPayAdaptorDto createDivPay(DivPayTrans divPayTrans) {
        WePostDivPayParam param = new WePostDivPayParam();
        ShopChannel shop = divPayTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        /*set param*/
        List<WePostDivPayParam.Receiver> receivers = new ArrayList<>();
        WePostDivPayParam.Receiver receiver = param.new Receiver();
        receiver.setAccount(channel.getSpMchid());
        receiver.setAmount(divPayTrans.getAmount());
        receivers.add(receiver);

        param.setAppid(divPayTrans.getShopChannel().getChannel().getAppid());
        param.setOut_order_no(divPayTrans.getOutNo());
        param.setTransaction_id(divPayTrans.getTrans().getTransNo());
        param.setReceivers(receivers);

        WePostDivPayRetObj retObj = wePayService.postDivPay(param).getData();
        PostDivPayAdaptorDto ret = new PostDivPayAdaptorDto();
        /*set ret*/
        return ret;
    }

    /**
     * 查询分账信息
     * */
    @Override
    public GetDivPayAdaptorDto returnDivPay(DivPayTrans divPayTrans) {
        WeGetDivPayParam param = new WeGetDivPayParam();
        /*set param*/
        param.setOut_order_no(divPayTrans.getOutNo());
        PayTrans payTrans = divPayTrans.getTrans();
        assert payTrans != null: "payTrans不能为空";
        param.setTransaction_id(payTrans.getTransNo());

        WeGetDivPayRetObj retObj = wePayService.getDivPay(param).getData();
        GetDivPayAdaptorDto ret = new GetDivPayAdaptorDto();
        /*set ret*/
        ret.setAmount(retObj.getReceivers().getAmount());
        return ret;
    }

    /**
     * 分账退款
     * */
    @Override
    public PostDivRefundAdaptorDto createDivRefund(DivRefundTrans divRefundTrans) {
        WePostDivRefundParam param = new WePostDivRefundParam();
        DivPayTrans divPayTrans = divRefundTrans.getDivPayTrans();
        assert divPayTrans != null: "divPayTrans不能为空";
        ShopChannel shop = divRefundTrans.getShopChannel();
        assert shop != null: "shopChannel不能为空";
        Channel channel = shop.getChannel();
        assert channel != null: "channel不能为空";
        /*set param*/
        param.setOrder_id(divPayTrans.getTransNo());
        param.setOut_order_no(divPayTrans.getOutNo());
        param.setOut_return_no(divRefundTrans.getOutNo());
        param.setReturn_mchid(channel.getSpMchid());
        param.setAmount(divRefundTrans.getAmount());
        param.setDescription("分账回退");
        assert param.getOrder_id() != null || param.getOut_order_no() != null:
                "order_id和out_order_no不能都为空";

        WePostDivRefundRetObj retObj = wePayService.postDivRefund(param).getData();
        PostDivRefundAdaptorDto ret = new PostDivRefundAdaptorDto();
        /*set ret*/
        return ret;
    }

    @Override
    public GetDivRefundAdaptorDto returnDivRefund(DivRefundTrans divRefundTrans) {
        WeGetDivRefundParam param = new WeGetDivRefundParam();
        DivPayTrans divPayTrans = divRefundTrans.getDivPayTrans();
        assert divPayTrans != null: "divPayTrans不能为空";
        /*set param*/
        param.setOut_return_no(divRefundTrans.getOutNo());
        param.setOut_order_no(divPayTrans.getOutNo());

        WeGetDivRefundRetObj retObj = wePayService.getDivRefund(param).getData();
        /*set ret*/
        GetDivRefundAdaptorDto ret = new GetDivRefundAdaptorDto();
        ret.setAmount(retObj.getAmount());
        ret.setStatus(retObj.getByteResult());
        ret.setOutNo(retObj.getOut_return_no());
        ret.setTransNo(retObj.getReturn_id());
        ret.setSuccessTime(retObj.getFinish_time());
        return ret;
    }

    @Override
    public List<CheckResultDto> checkTransaction(List<? extends Transaction> trans) {
        List<CheckResultDto> ret = new ArrayList<>();
        if(trans.get(0).getClass()==PayTrans.class) {
            trans.forEach(payTrans -> {
                        GetPayTransAdaptorDto dto = returnOrderByTransId((PayTrans) payTrans);
                        ret.add(new CheckResultDto(payTrans.getId(), PayTrans.class.getName(), dto.getAmount()- payTrans.getAmount()));
                    });
        }
        if(trans.get(0).getClass()==RefundTrans.class) {
            trans.forEach(refundTrans -> {
                GetRefundAdaptorDto dto = returnRefund((RefundTrans) refundTrans);
                ret.add(new CheckResultDto(refundTrans.getId(), RefundTrans.class.getName(), dto.getAmount()- refundTrans.getAmount()));
            });
        }
        if(trans.get(0).getClass()==DivPayTrans.class) {
            trans.forEach(divPayTrans -> {
                GetDivPayAdaptorDto dto = returnDivPay((DivPayTrans) divPayTrans);
                ret.add(new CheckResultDto(divPayTrans.getId(), DivPayTrans.class.getName(), dto.getAmount()- divPayTrans.getAmount()));
            });
        }
        if(trans.get(0).getClass()==DivRefundTrans.class) {
            trans.forEach(divRefundTrans -> {
                GetDivRefundAdaptorDto dto = returnDivRefund((DivRefundTrans) divRefundTrans);
                ret.add(new CheckResultDto(divRefundTrans.getId(), DivRefundTrans.class.getName(), dto.getAmount()- divRefundTrans.getAmount()));
            });
        }
        return ret;
    }
}
