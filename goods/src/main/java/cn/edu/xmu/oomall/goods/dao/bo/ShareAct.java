package cn.edu.xmu.oomall.goods.dao.bo;

import cn.edu.xmu.oomall.goods.mapper.po.ThresholdPo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jdk.jfr.Threshold;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author 黄坤鹏
 * @date 2022/11/30 13:41
 */
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(callSuper = true)
@Data
public class ShareAct extends Activity{
    private List<ThresholdPo> thresholds;
}
