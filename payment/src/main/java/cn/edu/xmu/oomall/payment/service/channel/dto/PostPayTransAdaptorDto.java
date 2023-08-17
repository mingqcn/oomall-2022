//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service.channel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求支付
 * */
@Data
@NoArgsConstructor
public class PostPayTransAdaptorDto {

    /**
     * 预支付单号
     * wepay： prepay_id
     * alipay: null
     */
    private String prepayId;
}
