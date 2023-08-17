package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 请求分账回退参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class WePostDivRefundParam {
    /*二选一*/
    /**
     * 微信分账单号
     * divRefundTrans.divPayTrans.trans_no
     * */
    private String order_id;

    /**
     * 微信分账单号
     * divRefundTrans.divPayTrans.out_no
     * */
    private String out_order_no;

    /*必选*/
    /**
     * 商户回退单号
     * divRefundTrans.out_no
     * */
    @NonNull
    private String out_return_no;

    /**
     * 分账接受方账号（平台）
     * spMchid
     * */
    @NonNull
    private String return_mchid;
    /**
     * 回退金额
     * divRefundTrans.amount
     * */
    @NonNull
    private Long amount;
    @NonNull
    private String description;

}
