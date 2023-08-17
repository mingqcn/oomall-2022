package cn.edu.xmu.oomall.goods.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author keyu zhu
 * @date 2022/12/2
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SimpleOnsaleDto {
    Long id;
    IdNameDto product;
    Long price;//价格
    LocalDateTime beginTime;//开始时间
    LocalDateTime endTime;//结束时间
    Integer quantity;//数量
    Integer maxQuantity;//最大数量
    Byte type;//商品类型
}
