package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 请求分账退回参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class WeGetDivRefundParam {
    /*必选*/
    /**
     * 内部分账请求号
     * divRefundTrans.divPayTrans.out_no
     * */
    @NonNull
    private String out_order_no;

    /**
     * 商户回退单号
     * divRefundTrans.trans_no
     * */
    @NonNull
    private String out_return_no;

}
