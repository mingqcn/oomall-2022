//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.onsale;

import cn.edu.xmu.oomall.goods.dao.OnsaleDao;
import cn.edu.xmu.oomall.goods.dao.bo.Onsale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获得某个特定的onsale
 */
public class SpecOnSaleExecutor implements OnsaleExecutor{
    private final static Logger logger = LoggerFactory.getLogger(OnsaleExecutor.class);


    private OnsaleDao onsaleDao;

    private Long onsaleId;

    public SpecOnSaleExecutor(OnsaleDao onsaleDao, Long onsaleId) {
        this.onsaleDao = onsaleDao;
        this.onsaleId = onsaleId;
    }

    @Override
    public Onsale execute() {
        logger.debug("execute: onsaleId = {}", this.onsaleId);
        return this.onsaleDao.findById(this.onsaleId);
    }
}
