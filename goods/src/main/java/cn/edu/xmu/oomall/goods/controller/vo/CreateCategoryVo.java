package cn.edu.xmu.oomall.goods.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateCategoryVo {
    @NotNull(message = "类目名称不能为空")
    private String name;

    @Min(value = 0, message = "抽佣比例不能小于0")
    @Max(value = 100, message = "抽佣比例不能大于100")
    private Integer commissionRatio;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCommissionRatio() {
        return commissionRatio;
    }

    public void setCommissionRatio(Integer commissionRatio) {
        this.commissionRatio = commissionRatio;
    }
}
