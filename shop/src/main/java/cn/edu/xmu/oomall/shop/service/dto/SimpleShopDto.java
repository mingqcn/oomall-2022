package cn.edu.xmu.oomall.shop.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenyz
 * @date 2022-11-26 11:26
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleShopDto {

    private Long id;
    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺状态
     */
    private Byte status;

    /**
     * 类型 0 电商 1 服务商
     */
    private Byte type;
}
