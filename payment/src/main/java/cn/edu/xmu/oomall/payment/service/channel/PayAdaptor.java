//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service.channel;

import cn.edu.xmu.oomall.payment.dao.bo.DivPayTrans;
import cn.edu.xmu.oomall.payment.dao.bo.DivRefundTrans;
import cn.edu.xmu.oomall.payment.dao.bo.PayTrans;
import cn.edu.xmu.oomall.payment.dao.bo.RefundTrans;
import cn.edu.xmu.oomall.payment.service.channel.dto.*;
import cn.edu.xmu.oomall.payment.dao.bo.Transaction;

import java.util.List;

/**
 * 支付渠道适配器接口
 * 适配器模式
 */
public interface PayAdaptor {

    /**
     * 创建支付交易单
     * @author Ming Qiu
     * <p>
     * date: 2022-11-01 19:26
     * @param payTrans 支付交易
     * @return
     */
    PostPayTransAdaptorDto createPayment(PayTrans payTrans);

    /**
     * 向第三方平台查询订单
     * */
    GetPayTransAdaptorDto returnOrderByTransId(PayTrans payTrans);

    /**
     * 向第三方平台查询订单(未收到下单返回值)
     * */
    GetPayTransAdaptorDto returnOrderByOutNo(PayTrans payTrans);

    /**
     * 取消订单
     * */
    CancelOrderAdaptorDto cancelOrder(PayTrans payTrans);


    PostRefundAdaptorDto createRefund(RefundTrans refundTrans);

    /**
     * 查询退货单
     * */
    GetRefundAdaptorDto returnRefund(RefundTrans refundTrans);

    /**
     * 分账交易
     * */
    PostDivPayAdaptorDto createDivPay(DivPayTrans divPayTrans);

    /**
     * 查询分账信息
     * */
    GetDivPayAdaptorDto returnDivPay(DivPayTrans divPayTrans);

    /**
     * 退款分账交易
     * */
    PostDivRefundAdaptorDto createDivRefund(DivRefundTrans divRefundTrans);

    /**
     * 查询分账退款信息
     * */
    GetDivRefundAdaptorDto returnDivRefund(DivRefundTrans divRefundTrans);

    List<CheckResultDto> checkTransaction(List<? extends Transaction> trans);
}
