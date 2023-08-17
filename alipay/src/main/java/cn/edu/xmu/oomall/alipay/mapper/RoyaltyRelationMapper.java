package cn.edu.xmu.oomall.alipay.mapper;

import java.util.List;

import cn.edu.xmu.oomall.alipay.model.vo.RoyaltyRelationVo;

public interface RoyaltyRelationMapper {

	public int bindRoyaltyRelation(RoyaltyRelationVo vo);

	public int unbindRoyaltyRelation(String app_id, String string, String string2);

	public List<RoyaltyRelationVo> queryRoyaltyRelationQuery(String app_id, int startnum, int pageSize);

	public int coutRoyaltyRelation(String app_id);

	public RoyaltyRelationVo existRelation(String app_id, String transOut, String transOutType);

}
