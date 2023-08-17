//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.goods.service.dto.ConsigneeDto;
import cn.edu.xmu.oomall.goods.service.dto.IdNameDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class Shop extends OOMallObject {

    private Byte status;

    private ConsigneeDto consignee;

    private Byte type;

    private Long deposit;

    private Long depositThreshold;

    private String name;

    @Builder
    public Shop(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, Byte status, ConsigneeDto consignee, Byte type, Long deposit, Long depositThreshold, String name) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.status = status;
        this.consignee = consignee;
        this.type = type;
        this.deposit = deposit;
        this.depositThreshold = depositThreshold;
        this.name = name;
    }
}