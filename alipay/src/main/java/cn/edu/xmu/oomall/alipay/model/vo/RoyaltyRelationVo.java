package cn.edu.xmu.oomall.alipay.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoyaltyRelationVo {
	
	private Long id;
	
	private String outRequestNo;
	
	private String appId;
	
	@JsonProperty("login_name")
	private String loginName;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("memo")
	private String memo;
	
	@JsonProperty("bind_login_name")
	private String bindLoginName;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("account")
	private String account;
}
