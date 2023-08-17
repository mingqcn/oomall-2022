//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.goods.controller.vo;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 商品视图对象
 * @author Ming Qiu
 **/
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductVo {

    private Long id;

    private String skuSn;

    @NotBlank(message="商品名称不能为空")
    private String name;

    @Min(0)
    private Long originalPrice;

    @Min(0)
    private Long weight;

    private String imageUrl;

    private String barcode;

    private String unit;

    private String originPlace;

    private Byte status;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private UserDto creator;

    private UserDto modifier;

}
