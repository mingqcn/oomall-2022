package cn.edu.xmu.oomall.goods.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "activity")
public class GrouponActPo {

    @MongoId
    private String objectId;

    private List<ThresholdPo> thresholds;
}