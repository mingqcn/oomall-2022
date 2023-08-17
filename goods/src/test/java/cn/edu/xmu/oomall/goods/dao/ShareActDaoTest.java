package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.oomall.goods.GoodsApplication;
import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import cn.edu.xmu.oomall.goods.dao.activity.ShareActDao;
import cn.edu.xmu.oomall.goods.service.dto.ActivityOnsaleDto;
import cn.edu.xmu.oomall.goods.dao.bo.ShareAct;
import cn.edu.xmu.oomall.goods.mapper.po.ActivityPo;
import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author 黄坤鹏
 * @date 2022/12/1 9:53
 */
@SpringBootTest(classes = GoodsTestApplication.class)
@Transactional
public class ShareActDaoTest {
    @Autowired
    private ShareActDao shareActDao;

    @Test
    public void getActivityTest(){
        ActivityPo activityPo = new ActivityPo();
        activityPo.setId(1L);
        activityPo.setActClass("shareActDao");
        activityPo.setObjectId("56064886ade2f21f36b03134");
        activityPo.setCreatorName("admin");

        ShareAct activity = (ShareAct) shareActDao.getActivity(activityPo);
        assertThat(activity.getThresholds().size()).isEqualTo(1);
    }

    @Test
    public void insertTest() {
        List<ThresholdPo> thresholds = new ArrayList<>();
        thresholds.add(new ThresholdPo(5, (long) 123));
        ShareAct shareAct = new ShareAct(thresholds);
        shareAct.setName("青春飞扬");
        shareAct.setShopId(12L);
        String objectId = shareActDao.insert(shareAct);
        assertThat(objectId).isNotNull();
    }

    @Test
    public void saveTest(){
        List<ThresholdPo> thresholds = new ArrayList<>();
        thresholds.add(new ThresholdPo(5, (long) 123));
        ShareAct shareAct = new ShareAct(thresholds);
        shareAct.setObjectId("56064886ade2f21f36b03134");
        shareAct.setName("青春飞扬");
        shareAct.setShopId(12L);
        shareActDao.save(shareAct);
    }

    @Test
    public void retrieveByActIdTest(){
        List<ActivityOnsaleDto> activityOnsales = shareActDao.retrieveActivityOnsaleByActId(2L);
        assertThat(activityOnsales.size()).isEqualTo(1);
        assertThat(activityOnsales.get(0).getOnsaleId()).isEqualTo(3L);
    }

    @Test
    public void delByByActIdAndOnsaleIdTest(){
        assertThrows(BusinessException.class, () -> shareActDao.delActivityOnsaleByActIdAndOnsaleId(2L, 7L));
    }
}
