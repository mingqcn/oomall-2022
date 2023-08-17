//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import cn.edu.xmu.oomall.payment.service.openfeign.WePayService;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 预下单返回参数
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WePostTransRetObj {
    /**
     * 预支付交易会话标识
     */
    private String prepayId;

    public WePostTransRetObj(String prepayId) {
        this.prepayId = prepayId;
    }
}
