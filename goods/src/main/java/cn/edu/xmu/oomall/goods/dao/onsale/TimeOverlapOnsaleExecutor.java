//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.onsale;

import cn.edu.xmu.oomall.goods.dao.OnsaleDao;
import cn.edu.xmu.oomall.goods.dao.bo.Onsale;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

/**
 * 时间重叠的onsale
 */
public class TimeOverlapOnsaleExecutor implements OnsaleExecutor{

    private OnsaleDao onsaleDao;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private Long productId;

    public TimeOverlapOnsaleExecutor(OnsaleDao onsaleDao, LocalDateTime beginTime, LocalDateTime endTime, Long productId) {
        this.onsaleDao = onsaleDao;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.productId = productId;
    }

    @Override
    public Onsale execute() {
        return this.onsaleDao.findOverlapOnsale(this.productId, beginTime, endTime);
    }
}
