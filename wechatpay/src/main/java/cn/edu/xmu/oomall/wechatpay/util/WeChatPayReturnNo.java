package cn.edu.xmu.oomall.wechatpay.util;

/**
 * @author ziyi guo
 * @date 2021/12/2
 */
public enum WeChatPayReturnNo {

    //200
    OK("成功"),

    //400
    ORDER_CLOSED("订单已关闭"),
    ORDER_PAID("订单已支付"),
    PARAM_ERROR("参数错误"),
    INVALID_REQUEST("请求参数符合参数格式，但不符合业务规则"),

    //403
    OUT_TRADE_NO_USED("商户订单号重复"),
    OUT_REFUND_NO_USED("商户退款单号重复"),
    OUT_DIVPAY_NO_USED("商户分账单号重复"),
    OUT_DIVREFUND_NO_USED("商户回退单号重复"),
    OUT_RECEIVER_NO_USED("分账接收方重复"),
    REFUND_TRANSACTION_ERROR("对应支付单未成功支付"),
    REFUND_AMOUNT_ERROR("退款金额错误"),
    NO_AUTH("商户无权限"),
    NOT_ENOUGH("分账金额不足"),

    //404
    RESOURCE_NOT_EXISTS("查询的资源不存在"),

    //429
    FREQUENCY_LIMITED("请求频率过高"),
    RATELIMIT_EXCEED("添加/删除接收方频率过高"),

    //500
    SYSTEM_ERROR("系统错误");

    private String message;

    WeChatPayReturnNo(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
