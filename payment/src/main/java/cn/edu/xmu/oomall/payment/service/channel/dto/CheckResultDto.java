package cn.edu.xmu.oomall.payment.service.channel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对账
 * @author Wenbo Li
 * */
@Data
@NoArgsConstructor
public class CheckResultDto {
    private Long id;
    private String cls;
    private Byte status = 2; // 2正确, 3错账, 4 长款

    public CheckResultDto(Long id, String cls, Long amount) {
        this.id = id;
        this.cls = cls;
        if(amount!=0) this.status = 3;
        if(id==null) this.status = 4;
    }
}
