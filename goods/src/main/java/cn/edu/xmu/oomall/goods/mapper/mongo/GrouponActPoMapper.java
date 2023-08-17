//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.mapper.mongo;

import cn.edu.xmu.oomall.goods.mapper.po.GrouponActPo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrouponActPoMapper extends MongoRepository<GrouponActPo, String> {

}
