//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.activity;

import cn.edu.xmu.oomall.goods.dao.bo.Activity;
import cn.edu.xmu.oomall.goods.dao.bo.GrouponAct;
import cn.edu.xmu.oomall.goods.mapper.mongo.GrouponActPoMapper;
import cn.edu.xmu.oomall.goods.mapper.po.ActivityPo;
import cn.edu.xmu.oomall.goods.mapper.po.GrouponActPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class GrouponActDao implements ActivityInf{

    private final Logger logger = LoggerFactory.getLogger(GrouponActDao.class);

    private final GrouponActPoMapper actPoMapper;

    @Autowired
    public GrouponActDao(GrouponActPoMapper actPoMapper) {
        this.actPoMapper = actPoMapper;
    }

    @Override
    public Activity getActivity(ActivityPo po) throws RuntimeException{
        GrouponAct bo = cloneObj(po, GrouponAct.class);
        Optional<GrouponActPo> ret = this.actPoMapper.findById(po.getObjectId());
        ret.ifPresent(actPo -> {
            copyObj(actPo, bo);
        } );
        return bo;
    }

    @Override
    public String insert(Activity bo) throws RuntimeException{
        GrouponActPo po = cloneObj(bo, GrouponActPo.class);
        GrouponActPo newPo = this.actPoMapper.insert(po);
        return newPo.getObjectId();
    }

    @Override
    public void save(Activity bo) throws RuntimeException{
        GrouponActPo po = cloneObj(bo, GrouponActPo.class);
        this.actPoMapper.save(po);
    }

}
