//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.bo;

import cn.edu.xmu.oomall.goods.dao.bo.Activity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 预售活动
 */
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(callSuper = true)
@Data
@Builder
public class AdvanceSaleAct extends Activity {

    /**
     * 尾款支付时间
     */
    private LocalDateTime payTime;

    /**
     * 订金
     */
    private Long deposit;

    public static final String ACTCLASS = "advanceSaleActDao";

    public AdvanceSaleAct(Long shopId, String name, String objectId, String actClass, LocalDateTime payTime, Long deposit) {
        super(shopId, name, objectId, actClass);
        this.payTime = payTime;
        this.deposit = deposit;
    }
}
