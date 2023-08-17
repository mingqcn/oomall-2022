package cn.edu.xmu.oomall.shop.dao.bo.template;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;

/**
 * 运费模板对象
 */
@ToString(callSuper = true)
@NoArgsConstructor
@Data
public class Template extends OOMallObject implements Serializable, Cloneable {
    /**
     * 默认模板
     */
    public static final Byte DEFAULT = 1;
    public static final Byte COMMON = 0;
    /**
     * 商铺id
     */
    private Long shopId;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 1 默认
     */
    private Byte defaultModel;

    @Override
    public Object clone() throws CloneNotSupportedException {
        Template template = (Template)  super.clone();
        template.setDefaultModel(COMMON);
        return template;
    }


    @Builder
    public Template(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName,
                    LocalDateTime gmtCreate, LocalDateTime gmtModified,Long shopId,String name,Byte defaultModel)
    {
        super(id,creatorId,creatorName,modifierId,modifierName,gmtCreate,gmtModified);
        this.shopId=shopId;
        this.name=name;
        this.defaultModel=defaultModel;
    }
}
