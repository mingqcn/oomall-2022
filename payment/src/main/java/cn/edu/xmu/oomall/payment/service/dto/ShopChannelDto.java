package cn.edu.xmu.oomall.payment.service.dto;

import cn.edu.xmu.oomall.payment.dao.bo.Channel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄坤鹏
 * @date 2022/11/9 1:16
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopChannelDto {
    private Long id;
    private String subMchid;
    private Byte status;
}
