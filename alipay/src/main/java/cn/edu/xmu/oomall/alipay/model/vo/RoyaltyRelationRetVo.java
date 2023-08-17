package cn.edu.xmu.oomall.alipay.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cn.edu.xmu.oomall.alipay.util.AlipayReturnNo;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoyaltyRelationRetVo {

	private String code;
	private String msg;
	private String result_code;

	@JsonProperty("sub_code")
	private String subCode;
	@JsonProperty("sub_msg")
	private String subMsg;

	public RoyaltyRelationRetVo(AlipayReturnNo alipayReturnNo) {
		this.code = "40004";
		this.msg = "Business Failed";
		this.subCode = alipayReturnNo.getSubCode();
		this.subMsg = alipayReturnNo.getSubMsg();
	}

	public RoyaltyRelationRetVo() {
		code = "10000";
		msg = "Success";
		result_code = "SUCCESS";
	}
}
