//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.onsale;

import cn.edu.xmu.oomall.goods.dao.OnsaleDao;
import cn.edu.xmu.oomall.goods.dao.bo.Onsale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获得当前有效的onsale
 */
public class ValidOnsaleExecutor implements OnsaleExecutor{
    private final static Logger logger = LoggerFactory.getLogger(ValidOnsaleExecutor.class);


    private OnsaleDao onsaleDao;

    private Long productId;

    public ValidOnsaleExecutor(OnsaleDao onsaleDao, Long productId) {
        this.onsaleDao = onsaleDao;
        this.productId = productId;
    }

    @Override
    public Onsale execute() {
        logger.debug("execute: productId = {}", this.productId);
        return onsaleDao.findLatestValidOnsaleByProductId(this.productId);
    }
}
