package cn.edu.xmu.oomall.alipay.model.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cn.edu.xmu.oomall.alipay.util.AlipayReturnNo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoyaltyRelationQueryRetVo {

	private String code;
	private String msg;
	private String result_code;

	private int total_page_num;
	private int total_record_num;
	private int current_page_num;
	private int current_page_size;

	@JsonProperty("sub_code")
	private String subCode;

	@JsonProperty("sub_msg")
	private String subMsg;

	@JsonProperty("receiver_list")
	private List<RoyaltyRelationVo> receiverList;

	public RoyaltyRelationQueryRetVo(AlipayReturnNo alipayReturnNo) {
		this.code = "40004";
		this.msg = "Business Failed";
		this.subCode = alipayReturnNo.getSubCode();
		this.subMsg = alipayReturnNo.getSubMsg();
	}

	public void setDefault() {
		code = "10000";
		msg = "Success";
		result_code = "SUCCESS";
	}

	public RoyaltyRelationQueryRetVo(List<RoyaltyRelationVo> list, int allPage, int count, int pageNum, int pageSize) {
		this.receiverList = list;
		this.total_page_num = allPage;
		this.total_record_num = count;
		this.current_page_num = pageNum;
		this.current_page_size = pageSize;
		this.code = "10000";
		this.msg = "Success";
		this.result_code = "SUCCESS";
	}
}
