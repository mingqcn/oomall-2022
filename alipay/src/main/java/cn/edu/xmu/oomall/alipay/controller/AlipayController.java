package cn.edu.xmu.oomall.alipay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.xmu.oomall.alipay.service.AlipayService;
import cn.edu.xmu.oomall.alipay.util.WarpRetObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author xucangbai
 * @date 2021/11/20
 */
@Api(value = "支付宝接口", tags = "支付宝接口")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class AlipayController {

	@Autowired
	private AlipayService alipayService;

	@ApiOperation(value = "*AliPay支付", produces = "application/json;charset=UTF-8")
	@PostMapping("internal/alipay/gateway.do")
	public Object gatewayDo(@RequestParam(required = false) String app_id, @RequestParam(required = true) String method,
			@RequestParam(required = false) String format, @RequestParam(required = false) String charset,
			@RequestParam(required = false) String sign_type, @RequestParam(required = false) String sign,
			@RequestParam(required = false) String timestamp, @RequestParam(required = false) String notify_url,
			@RequestParam(required = true) String biz_content) {
		WarpRetObject warpRetObject = new WarpRetObject();
		switch (method) {
		case "alipay.trade.wap.pay":
			warpRetObject.setPayRetVo(alipayService.pay(biz_content));
			break;
		case "alipay.trade.query":
			warpRetObject.setPayQueryRetVo(alipayService.payQuery(biz_content));
			break;
		case "alipay.trade.close":
			warpRetObject.setCloseRetVo(alipayService.close(biz_content));
			break;
		case "alipay.trade.refund":
			warpRetObject.setRefundRetVo(alipayService.refund(biz_content));
			break;
		case "alipay.trade.fastpay.refund.query": // 统一收单交易退款查询
			warpRetObject.setRefundQueryRetVo(alipayService.refundQuery(biz_content));
			break;
		case "alipay.trade.refund.query":
			warpRetObject.setRefundQueryRetVo(alipayService.refundQuery(biz_content));
			break;
		case "alipay.data.dataservice.bill.downloadurl.query":
			warpRetObject.setDownloadUrlQueryRetVo(alipayService.downloadUrlQuery());
			break;
		case "alipay.trade.royalty.relation.bind": // 分账关系绑定
			warpRetObject.setRoyaltyRelationBindRetVo(alipayService.royaltyRelationBind(app_id, biz_content));
			break;
		case "alipay.trade.royalty.relation.unbind": // 分账关系解除
			warpRetObject.setRoyaltyRelationUnBindRetVo(alipayService.royaltyRelationUnBind(app_id, biz_content));
			break;
		case "alipay.trade.royalty.relation.batchquery": // 分账关系查询
			warpRetObject.setRoyaltyRelationQueryRetVo(alipayService.royaltyRelationQuery(app_id, biz_content));
			break;
		case "alipay.trade.order.settle": // 分账统一收单交易结算接口
			warpRetObject.setRoyaltyRelationSettleRetVo(alipayService.settleOrder(app_id, biz_content));
			break;
		case "alipay.trade.order.settle.query": // 交易分账查询接口
			warpRetObject.setRoyaltySettleQueryRetVo(alipayService.querySettle(biz_content));
			break;
		default:
			warpRetObject = new WarpRetObject();
		}
		return warpRetObject;
	}
}
