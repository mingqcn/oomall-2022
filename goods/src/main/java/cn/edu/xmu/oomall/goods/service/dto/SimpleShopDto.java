package cn.edu.xmu.oomall.goods.service.dto;

import cn.edu.xmu.javaee.core.aop.CopyFrom;
import cn.edu.xmu.oomall.goods.dao.bo.Shop;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@CopyFrom(Shop.class)
public class SimpleShopDto {

    private Long id;
    private String name;
    private Byte type;


}
