package cn.edu.xmu.oomall.alipay.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import cn.edu.xmu.oomall.alipay.model.vo.CloseRetVo;
import cn.edu.xmu.oomall.alipay.model.vo.DownloadUrlQueryRetVo;
import cn.edu.xmu.oomall.alipay.model.vo.PayQueryRetVo;
import cn.edu.xmu.oomall.alipay.model.vo.PayRetVo;
import cn.edu.xmu.oomall.alipay.model.vo.RefundQueryRetVo;
import cn.edu.xmu.oomall.alipay.model.vo.RefundRetVo;
import cn.edu.xmu.oomall.alipay.model.vo.RoyaltyRelationQueryRetVo;
import cn.edu.xmu.oomall.alipay.model.vo.RoyaltyRelationRetVo;
import cn.edu.xmu.oomall.alipay.model.vo.RoyaltyRelationSettleRetVo;
import cn.edu.xmu.oomall.alipay.model.vo.RoyaltyRelationUnBindRetVo;
import cn.edu.xmu.oomall.alipay.model.vo.RoyaltySettleQueryRetVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xucangbai
 * 最上层的包装对象
 * 当某一字段空时，不写入
 *
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarpRetObject {
    @JsonProperty("alipay_trade_wap_pay_response")
    private PayRetVo payRetVo;

    @JsonProperty("alipay_trade_query_response")
    private PayQueryRetVo payQueryRetVo;

    @JsonProperty("alipay_trade_close_response")
    private CloseRetVo closeRetVo;

    @JsonProperty("alipay_trade_refund_response")
    private RefundRetVo refundRetVo;

    @JsonProperty("alipay_trade_fastpay_refund_query_response")
    private RefundQueryRetVo refundQueryRetVo;

    @JsonProperty("alipay_data_dataservice_bill_downloadurl_query_response")
    private DownloadUrlQueryRetVo downloadUrlQueryRetVo;

    @JsonProperty("alipay_trade_royalty_relation_bind_response")
    private RoyaltyRelationRetVo royaltyRelationBindRetVo;
    
    @JsonProperty("alipay_trade_royalty_relation_unbind_response")
    private RoyaltyRelationUnBindRetVo royaltyRelationUnBindRetVo;
    
    @JsonProperty("alipay_trade_royalty_relation_batchquery_response")
    private RoyaltyRelationQueryRetVo royaltyRelationQueryRetVo;
    
    @JsonProperty("alipay_trade_order_settle_response")
    private RoyaltyRelationSettleRetVo royaltyRelationSettleRetVo;
    
    @JsonProperty("alipay_trade_order_settle_query_response")
    private RoyaltySettleQueryRetVo royaltySettleQueryRetVo;

    /**
     * 固定:ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE
     */
    private String sign="ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE";
}
