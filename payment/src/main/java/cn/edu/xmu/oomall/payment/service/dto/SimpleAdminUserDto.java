package cn.edu.xmu.oomall.payment.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SimpleAdminUserDto {
    @NotNull
    private Long id;

    private String userName;
}
