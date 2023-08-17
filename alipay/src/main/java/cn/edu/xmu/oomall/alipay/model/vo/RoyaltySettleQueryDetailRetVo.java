package cn.edu.xmu.oomall.alipay.model.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RoyaltySettleQueryDetailRetVo {

	public String operation_type;
	public String execute_dt;
	public String trans_out;
	public String trans_out_type;
	public String trans_in;
	public String trans_in_type;
	public BigDecimal amount;
	public String state;
}
