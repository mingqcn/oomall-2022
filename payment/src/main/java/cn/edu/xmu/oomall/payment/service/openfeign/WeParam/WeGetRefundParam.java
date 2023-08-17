package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 查询退款参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class WeGetRefundParam {
    /**
     * 内部退款单号
     * refundTrans.out_no
     * */
    @NonNull
    private String out_trade_no;
}
