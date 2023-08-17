package cn.edu.xmu.oomall.goods.service.dto;

import cn.edu.xmu.javaee.core.aop.CopyFrom;
import cn.edu.xmu.oomall.goods.dao.bo.AdvanceSaleAct;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@CopyFrom(AdvanceSaleAct.class)
public class AdvanceSaleActDto {

    private Long id;
    private String name;
    private LocalDateTime payTime;
    private Long deposit;
    private SimpleShopDto shop;


}
