package cn.edu.xmu.oomall.payment.service.openfeign.WeParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 查询分账信息参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class WeGetDivPayParam {
    /*必选*/
    /**
     * 微信订单号
     * divPayTrans.payTrans.trans_no
     * */
    @NonNull
    private String transaction_id;

    /**
     * 内部分账单号
     * divPayTrans.out_no
     * */
    @NonNull
    private String out_order_no;
}
