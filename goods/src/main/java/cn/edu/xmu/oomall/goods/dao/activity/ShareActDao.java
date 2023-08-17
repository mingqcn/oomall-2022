//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.activity;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.goods.dao.bo.Activity;
import cn.edu.xmu.oomall.goods.service.dto.ActivityOnsaleDto;
import cn.edu.xmu.oomall.goods.dao.bo.ShareAct;
import cn.edu.xmu.oomall.goods.mapper.jpa.ActivityOnsalePoMapper;
import cn.edu.xmu.oomall.goods.mapper.mongo.ShareActPoMapper;
import cn.edu.xmu.oomall.goods.mapper.po.ActivityOnsalePo;
import cn.edu.xmu.oomall.goods.mapper.po.ActivityPo;
import cn.edu.xmu.oomall.goods.mapper.po.ShareActPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;
import static cn.edu.xmu.javaee.core.util.Common.putGmtFields;

@Repository
public class ShareActDao implements ActivityInf{

    private Logger logger = LoggerFactory.getLogger(ShareActDao.class);

    private ShareActPoMapper actPoMapper;

    private ActivityOnsalePoMapper activityOnsalePoMapper;

    @Autowired
    public ShareActDao(ShareActPoMapper actPoMapper, ActivityOnsalePoMapper activityOnsalePoMapper) {
        this.actPoMapper = actPoMapper;
        this.activityOnsalePoMapper = activityOnsalePoMapper;
    }

    @Override
    public Activity getActivity(ActivityPo po) throws RuntimeException {
        ShareAct bo = cloneObj(po, ShareAct.class);
        Optional<ShareActPo> ret = this.actPoMapper.findById(po.getObjectId());
        ret.ifPresent(actPo -> {
            copyObj(actPo, bo);
        } );
        return bo;
    }

    @Override
    public String insert(Activity bo) throws RuntimeException{
        ShareActPo po = cloneObj(bo, ShareActPo.class);
        ShareActPo newPo = this.actPoMapper.insert(po);
        return newPo.getObjectId();
    }

    @Override
    public void save(Activity bo) throws RuntimeException{
        ShareActPo po = cloneObj(bo, ShareActPo.class);
        this.actPoMapper.save(po);
    }

    public List<ActivityOnsaleDto> retrieveActivityOnsaleByActId(Long actId){
        List<ActivityOnsalePo> ret = activityOnsalePoMapper.findByActIdEquals(actId);
        return ret.stream().map(po -> cloneObj(po, ActivityOnsaleDto.class)).collect(Collectors.toList());
    }

    public void insertActivityOnsale(ActivityOnsaleDto bo, UserDto user) throws RuntimeException{
        ActivityOnsalePo po = cloneObj(bo, ActivityOnsalePo.class);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        activityOnsalePoMapper.save(po);
    }

    //删除中间表关系以取消活动
    public void delActivityOnsaleByActIdAndOnsaleId(Long actId, Long onsaleId){
        List<ActivityOnsalePo> activityOnsalePoList = activityOnsalePoMapper.findByActIdAndOnsaleId(actId, onsaleId);
        if(null == activityOnsalePoList || activityOnsalePoList.size() == 0){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        activityOnsalePoMapper.deleteById(activityOnsalePoList.get(0).getId());
    }
}
