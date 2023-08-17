package cn.edu.xmu.oomall.payment.service.channel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询分账
 * */
@Data
@NoArgsConstructor
public class GetDivPayAdaptorDto {
    /**
     * 分账金额
     * 微信：receivers.amount
     * 支付宝: royalty_detail_list.amount
     * */
    private Long amount;
}
