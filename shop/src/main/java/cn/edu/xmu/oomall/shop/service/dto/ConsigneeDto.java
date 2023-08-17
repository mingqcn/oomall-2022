//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsigneeDto {
    private String name;
    private String mobile;
    private Long regionId;
    private String address;
}
