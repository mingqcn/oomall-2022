//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.mapper.po;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;


@Data
@NoArgsConstructor
@Document(collection = "regionTemplate")
public class WeightTemplatePo {

    @MongoId
    private ObjectId objectId;

    private Integer firstWeight;

    private Long firstWeightPrice;

    private List<WeightThresholdPo> thresholds;
}
