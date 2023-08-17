package cn.edu.xmu.oomall.payment.service.openfeign.AliParam;

import lombok.*;

/**
 * 查询分账信息参数
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class AliGetDivPayParam {
    /*传入第一个或后面两个*/
    /**
     * 分账请求单号
     * divPayTrans.trans_no
     * */
    private String settle_no;

    /**
     * 内部分账请求号
     * divPayTrans.out_no
     * */
    private String out_request_no;

    /**
     * 交易单号
     * divPayTrans.payTrans.trans_no
     * */
    private String trade_no;

}
