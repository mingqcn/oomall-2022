//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.mapper.po;

import cn.edu.xmu.oomall.goods.mapper.po.strategy.BaseCouponDiscount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "activity")
public class CouponActPo {
    @MongoId
    private String objectId;
    private LocalDateTime couponTime;
    private Integer quantity;
    private Integer quantityType;
    private Integer validTerm;
    private String strategy;
}
