package cn.edu.xmu.oomall.alipay.mapper;

import cn.edu.xmu.oomall.alipay.model.vo.RoyaltySettleVo;

public interface RoyaltySettleMapper {
	int saveSettle(String out_request_no,String trade_no,String royalty,String capturetime);
	
	RoyaltySettleVo querySettle(String out_request_no,String trade_no);
}
