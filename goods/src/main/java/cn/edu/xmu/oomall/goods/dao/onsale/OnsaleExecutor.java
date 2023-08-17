//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.onsale;

import cn.edu.xmu.oomall.goods.dao.bo.Onsale;
import org.springframework.stereotype.Repository;

/**
 * OnsaleDao的command模式
 * 用于返回不同的onsale
 */
public interface OnsaleExecutor {
    public Onsale execute();
}
