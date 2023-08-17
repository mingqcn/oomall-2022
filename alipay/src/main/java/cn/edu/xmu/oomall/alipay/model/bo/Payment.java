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
public class Payment {

    private Byte tradeStatus;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 订单总金额。
     * 单位为分
     */
    private BigDecimal totalAmount;

    /**
     * 订单实际支付金额
     */
    private BigDecimal buyerPayAmount;

    /**
     * 支付时间，如果没有支付那么这里为空
     */
    private LocalDateTime sendPayDate;



    public TradeStatus getTradeStatus() {
        return TradeStatus.getStatusByCode(Integer.valueOf(tradeStatus));
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        Integer code=tradeStatus.getCode();
        Byte b=code.byteValue();
        this.tradeStatus=b;
    }

    public enum TradeStatus {
        TRADE_CLOSED(0, "TRADE_CLOSED"),
        TRADE_SUCCESS(1, "TRADE_SUCCESS"),
        WAIT_BUYER_PAY(2, "WAIT_BUYER_PAY"),
        TRADE_FINISHED(3, "TRADE_FINISHED");
        private static final Map<Integer, TradeStatus> STATE_MAP;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            STATE_MAP = new HashMap();
            for (TradeStatus enum1 : values()) {
                STATE_MAP.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        TradeStatus(int code, String description) {
            this.code=code;
            this.description=description;
        }

        public static TradeStatus getStatusByCode(Integer code){
            return STATE_MAP.get(code);
        }

        public Integer getCode(){
            return code;
        }

        public String getDescription() {return description;}

    }
}
