package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import cn.edu.xmu.oomall.goods.dao.activity.ActivityDao;
import cn.edu.xmu.oomall.goods.dao.activity.GrouponActDao;
import cn.edu.xmu.oomall.goods.dao.bo.GrouponAct;
import cn.edu.xmu.oomall.goods.mapper.po.ActivityPo;
import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author prophesier
 * @create 2022-12-07 1:59
 */
@SpringBootTest(classes = GoodsTestApplication.class)
@Transactional
public class GrouponActDaoTest {
    @Autowired
    private GrouponActDao grouponActDao;

    @Autowired
    private ActivityDao activityDao;

    @Test
    public void getActivityTest(){
        ActivityPo activityPo = new ActivityPo();
        activityPo.setId(1L);
        activityPo.setActClass("grouponActDao");
        activityPo.setObjectId("56064886ade2f21f36b03134");
        GrouponAct activity = cloneObj(grouponActDao.getActivity(activityPo), GrouponAct.class);
        assertThat(activity.getThresholds().size()).isEqualTo(1);
        assertThat(activity.getThresholds().get(0).getQuantity()).isEqualTo(52);
    }

    @Test
    public void insertTest() {
        List<ThresholdPo> thresholds = new ArrayList<>();
        thresholds.add(new ThresholdPo(52, (long) 12));
        GrouponAct grouponAct = new GrouponAct(thresholds);
        grouponAct.setName("groupon1");
        grouponAct.setShopId(12L);
        String objectId = grouponActDao.insert(grouponAct);
        assertThat(objectId).isNotNull();
    }

    @Test
    public void saveTest(){
        List<ThresholdPo> thresholds = new ArrayList<>();
        thresholds.add(new ThresholdPo(52, (long) 12));
        GrouponAct grouponAct = new GrouponAct(thresholds);
        grouponAct.setObjectId("56064886ade2f21f36b03134");
        grouponAct.setName("groupon1");
        grouponAct.setShopId(12L);
        grouponActDao.save(grouponAct);
    }

    @Test
    public void retrieveOnsaleIdByActIdTest(){
        assertThat(activityDao.retrieveActivityOnsaleByActId(2L).get(0).getOnsaleId()).isEqualTo(2L);
    }

    @Test
    public void insertActivityOnsaleTest(){
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("test1");
        user.setUserLevel(1);
        assertThat( activityDao.insertActivityOnsale(1L,10L, user)).isEqualTo(ReturnNo.OK);
    }

}
