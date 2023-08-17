package cn.edu.xmu.oomall.payment.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotifyDto {
    String code;
    String message;
    public NotifyDto(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
