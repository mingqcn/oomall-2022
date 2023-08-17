//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.javaee.core.functional;

import java.util.Optional;

@FunctionalInterface
public interface GetBo<B, P>{
    B getBo(P po, Optional<String> redisKey);
}
