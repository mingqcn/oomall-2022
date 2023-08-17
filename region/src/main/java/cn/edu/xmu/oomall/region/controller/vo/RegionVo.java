package cn.edu.xmu.oomall.region.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 地区视图对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegionVo {
    @NotBlank(message = "地区名不能为空")
    private String name;
    @NotBlank(message = "地区简称不能为空")
    private String shortName;
    @NotBlank(message = "地区全称不能为空")
    private String mergerName;
    @NotBlank(message = "地区拼音不能为空")
    private String pinyin;
    @NotBlank(message = "经度不能为空")
    private String lng;
    @NotBlank(message = "纬度不能为空")
    private String lat;
    @NotBlank(message = "地区码不能为空")
    private String areaCode;
    @NotBlank(message = "邮政编码不能为空")
    private String zipCode;
    @NotBlank(message = "电话区号不能为空")
    private String cityCode;
}
