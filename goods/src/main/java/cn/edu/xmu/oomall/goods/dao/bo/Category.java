//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao.bo;

import cn.edu.xmu.javaee.core.aop.CopyFrom;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.goods.controller.vo.CreateCategoryVo;
import cn.edu.xmu.oomall.goods.controller.vo.UpdateCategoryVo;
import cn.edu.xmu.oomall.goods.dao.CategoryDao;
import cn.edu.xmu.oomall.goods.mapper.po.CategoryPo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@CopyFrom({ CategoryPo.class, CreateCategoryVo.class, UpdateCategoryVo.class })
public class Category extends OOMallObject implements Serializable {
    @ToString.Exclude
    @JsonIgnore
    private final static Logger logger = LoggerFactory.getLogger(Category.class);

    @ToString.Exclude
    @JsonIgnore
    private final static Long PARENTID = 0L;

    @Builder
    public Category(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, String name, Long pid, Integer commissionRatio) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.name = name;
        this.pid = pid;
        this.commissionRatio = commissionRatio;
    }

    private String name;

    private Long pid;

    private Category parent;

    @Setter
    @JsonIgnore
    private CategoryDao categoryDao;

    public Category getParent(){
        if (null == this.parent && null != this.categoryDao){
            try {
                this.parent = this.categoryDao.findById(this.pid);
            }catch (BusinessException e){
                if (ReturnNo.RESOURCE_ID_NOTEXIST == e.getErrno()){
                    logger.error("getParent: category(id = {})'s pid is invalid...");
                }
            }
        }
        return this.parent;
    }

    private Integer commissionRatio;

    public Integer getCommissionRatio(){
        if (null == this.commissionRatio && null != this.getParent()){
            Category category = this.getParent();
            this.commissionRatio = this.parent.getCommissionRatio();
        }
        return this.commissionRatio;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public void setCommissionRatio(Integer commissionRatio) {
        this.commissionRatio = commissionRatio;
    }

    public String getName() {
        return name;
    }

    public Long getPid() {
        return pid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Long getModifierId() {
        return modifierId;
    }

    public void setModifierId(Long modifierId) {
        this.modifierId = modifierId;
    }

    public String getModifierName() {
        return modifierName;
    }

    public void setModifierName(String modifierName) {
        this.modifierName = modifierName;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }
}
