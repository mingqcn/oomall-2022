package cn.edu.xmu.oomall.alipay.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Refund {


    private Byte refundStatus;

    private String outTradeNo;

    /**
     * 退款单号
     */
    private String outRequestNo;

    /**
     * 对应订单总额
     */
    private BigDecimal totalAmount;

    /**
     * 本次退款请求对应金额
     */
    private BigDecimal refundAmount;

    /**
     *
     * 退款时间
     */
    private LocalDateTime gmtRefundPay;

    public RefundStatus getRefundStatus() {
        return RefundStatus.getStatusByCode(Integer.valueOf(refundStatus));
    }

    public void setRefundStatus(RefundStatus refundStatus) {
        Integer code=refundStatus.getCode();
        Byte b=code.byteValue();
        this.refundStatus=b;
    }


    public enum RefundStatus {
        REFUND_SUCCESS(0, "REFUND_SUCCESS");
        private static final Map<Integer, RefundStatus> STATE_MAP;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            STATE_MAP = new HashMap();
            for (RefundStatus enum1 : values()) {
                STATE_MAP.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        RefundStatus(int code, String description) {
            this.code=code;
            this.description=description;
        }

        public static RefundStatus getStatusByCode(Integer code){
            return STATE_MAP.get(code);
        }

        public Integer getCode(){
            return code;
        }

        public String getDescription() {return description;}

    }
}
