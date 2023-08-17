//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.activity;

import cn.edu.xmu.oomall.goods.dao.bo.Activity;
import cn.edu.xmu.oomall.goods.dao.bo.CouponAct;
import cn.edu.xmu.oomall.goods.mapper.mongo.CouponActPoMapper;
import cn.edu.xmu.oomall.goods.mapper.po.ActivityPo;
import cn.edu.xmu.oomall.goods.mapper.po.CouponActPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;
import static cn.edu.xmu.javaee.core.util.Common.copyObj;

@Repository
public class CouponActDao implements ActivityInf{

    private Logger logger = LoggerFactory.getLogger(CouponActDao.class);

    private CouponActPoMapper actPoMapper;

    @Autowired
    public CouponActDao(CouponActPoMapper actPoMapper) {
        this.actPoMapper = actPoMapper;
    }

    @Override
    public Activity getActivity(ActivityPo po)  throws RuntimeException {
        CouponAct bo = cloneObj(po, CouponAct.class);
        Optional<CouponActPo> ret = this.actPoMapper.findById(po.getObjectId());
        ret.ifPresent(couponActPo -> {
            copyObj(couponActPo, bo);
        } );
        return bo;
    }

    @Override
    public String insert(Activity bo) throws RuntimeException{
        CouponActPo po = cloneObj(bo, CouponActPo.class);
        CouponActPo newPo = this.actPoMapper.insert(po);
        return newPo.getObjectId();
    }

    @Override
    public void save(Activity bo) throws RuntimeException{
        CouponActPo po = cloneObj(bo, CouponActPo.class);
        this.actPoMapper.save(po);
    }
}
