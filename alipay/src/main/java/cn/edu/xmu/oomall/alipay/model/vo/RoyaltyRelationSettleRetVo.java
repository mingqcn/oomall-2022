package cn.edu.xmu.oomall.alipay.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cn.edu.xmu.oomall.alipay.util.AlipayReturnNo;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoyaltyRelationSettleRetVo {

	private String code;
	private String msg;

	@JsonProperty("sub_code")
	private String subCode;

	@JsonProperty("sub_msg")
	private String subMsg;

	@JsonProperty("trade_no")
	private String tradeNo;

	@JsonProperty("settle_no")
	private String settleNo;

	public RoyaltyRelationSettleRetVo(AlipayReturnNo alipayReturnNo) {
		this.code = "40004";
		this.msg = "Business Failed";
		this.subCode = alipayReturnNo.getSubCode();
		this.subMsg = alipayReturnNo.getSubMsg();
	}

	public RoyaltyRelationSettleRetVo(String tradeNo, String settleNo) {
		this.code = "10000";
		this.msg = "Success";
		this.tradeNo = tradeNo;
		this.settleNo = settleNo;
	}
}
