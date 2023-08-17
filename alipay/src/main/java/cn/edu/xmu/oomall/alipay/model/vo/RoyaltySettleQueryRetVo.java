package cn.edu.xmu.oomall.alipay.model.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cn.edu.xmu.oomall.alipay.util.AlipayReturnNo;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoyaltySettleQueryRetVo {
	private String code;
	private String msg;

	@JsonProperty("sub_code")
	private String subCode;

	@JsonProperty("sub_msg")
	private String subMsg;

	public String out_request_no;
	
	public String operation_dt;
	
	public List<RoyaltySettleQueryDetailRetVo> royalty_detail_list;
	
	public RoyaltySettleQueryRetVo(AlipayReturnNo alipayReturnNo) {
		this.code = "40004";
		this.msg = "Business Failed";
		this.subCode = alipayReturnNo.getSubCode();
		this.subMsg = alipayReturnNo.getSubMsg();
	}

	public RoyaltySettleQueryRetVo(String out_request_no, String operation_dt, List<RoyaltySettleQueryDetailRetVo> royalty_detail_list) {
		this.code = "10000";
		this.msg = "Success";
		this.out_request_no = out_request_no;
		this.operation_dt = operation_dt;
		this.royalty_detail_list = royalty_detail_list;
	}
}
