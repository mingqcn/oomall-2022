package cn.edu.xmu.oomall.goods.dao.bo;

import cn.edu.xmu.oomall.goods.service.dto.IdNameDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author wuzhicheng
 * @create 2022-12-13 20:20
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class Template {
    Long id;
    String name;
    Byte defaultModel;
    IdNameDto creator;
    IdNameDto modifier;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;
}
