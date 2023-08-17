//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao.bo;

import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 商户支付渠道
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Channel extends OOMallObject implements Serializable {

    /**
     * 有效
     */
    public static Byte VALID = 0;
    /**
     * 无效
     */
    public static Byte INVALID = 1;

    /**
     * 状态和名称的对应
     */
    public static final Map<Byte, String> STATUSNAMES = new HashMap(){
        {
            put(VALID, "有效");
            put(INVALID, "无效");
        }
    };

    /**
     * 是否允许状态迁移
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:25
     * @param status
     * @return
     */
    public boolean allowStatus(Byte status){
        return true;
    }

    /**
     * 平台应用id
     */
    private String appid;

    /**
     * 渠道名称
     */
    private String name;

    /**
     * 平台商户号
     */
    private String spMchid;

    /**
     * 开始时间
     */
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     *  状态
     */
    private Byte status;

    /**
     * 适配对象名
     */
    private String beanName;

    /**
     * 通知地址
     */
    private String notifyUrl;
}
