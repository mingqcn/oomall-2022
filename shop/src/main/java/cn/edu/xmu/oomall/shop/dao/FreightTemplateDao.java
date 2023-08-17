package cn.edu.xmu.oomall.shop.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;

import cn.edu.xmu.oomall.shop.dao.bo.template.Template;
import cn.edu.xmu.oomall.shop.dao.template.RegionTemplateDao;
import cn.edu.xmu.oomall.shop.dao.template.TemplateDao;
import cn.edu.xmu.oomall.shop.mapper.RegionTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.TemplatePoMapper;

import cn.edu.xmu.oomall.shop.mapper.po.RegionTemplatePo;
import cn.edu.xmu.oomall.shop.mapper.po.TemplatePo;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class FreightTemplateDao {
    private static final Logger logger = LoggerFactory.getLogger( FreightTemplateDao.class);

    private TemplatePoMapper templatePoMapper;

    private RegionTemplatePoMapper regionTemplatePoMapper;

    private RedisUtil redisUtil;
    @Autowired
    public FreightTemplateDao(TemplatePoMapper templatePoMapper,RedisUtil redisUtil,RegionTemplatePoMapper regionTemplatePoMapper){
        this.templatePoMapper=templatePoMapper;
        this.regionTemplatePoMapper=regionTemplatePoMapper;
        this.redisUtil=redisUtil;
    }

    /**
    * 查询运费模板
    * @author Zhanyu Liu
    * @date 2022/11/30 9:40
    * @param shopId
    * @param name
    * @param page
    * @param pageSize
    * @throws RuntimeException
    */
    public List<Template> retrieveTemplateByName(Long shopId, String name, Integer page, Integer pageSize)throws RuntimeException{
        Pageable pageable= PageRequest.of(page-1,pageSize);
        name=new StringBuilder(name).append("%").toString();
        logger.debug("retrieveTemplateByName: shopId = {},name={},page={},pageSize={}",shopId,name,page,pageSize);
        Page<TemplatePo> ret=null;
        if(shopId==PLATFORM){
            ret = templatePoMapper.findByNameLike(name,pageable);
        }else{
            ret = templatePoMapper.findByShopIdAndNameLike(shopId,name,pageable);
        }
        logger.debug("dao:retrieveTemplateByName: pageList={},page={},pageSize={}",ret.getContent(),ret.getPageable().getPageNumber(),ret.getPageable().getPageSize());
        List<Template> templateList=new ArrayList<>();
        if(!ret.isEmpty()){
            templateList.addAll(ret.stream()
                    .map(po -> cloneObj(po, Template.class))
                    .collect(Collectors.toList()));
        }
        return  templateList;
    }

    /**
    * 新增模板
    * @author Zhanyu Liu
    * @date 2022/11/30 9:40
    * @param template
    * @param user
    * @throws RuntimeException
    */
    public Template insertTemplate(Template template, UserDto user)throws RuntimeException{
        logger.debug("insertTemplate: template = {}",template);
        TemplatePo po=cloneObj(template,TemplatePo.class);
        putUserFields(po, "creator",user);
        putGmtFields(po, "create");
        logger.debug("insertTemplate: po = {}",po);
        TemplatePo newPo=templatePoMapper.save(po);
        logger.debug("insertTemplate: newPo = {}",newPo);
        template.setId(newPo.getId());
        return template;
    }

    /**
    * 通过id来查找模板
    * @author Zhanyu Liu
    * @date 2022/11/30 9:42
    * @param id
    * @throws RuntimeException
    */
    public Template findTemplateById(Long id) throws RuntimeException{
        AtomicReference<Template> template = new AtomicReference<>();
        logger.debug("findTemplateById: id = {}",id);
        Optional<TemplatePo> ret = this.templatePoMapper.findById(id);
        ret.ifPresent(po -> template.set(cloneObj(po, Template.class)));
        if (null == template.get()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "运费模板", id));
        }
        return template.get();
    }

    /**
     * 查询某商店的默认模板
     * @author Zhanyu Liu
     * @date 2022/11/30 9:42
     * @param shopId
     * @throws RuntimeException
     */
    public Optional<Template> retrieveTemplateByShopIdAndDefaultModel(Long shopId) throws RuntimeException{
        logger.debug("retrieveTemplateByShopIdAndDefaultModel: shopId = {}",shopId);
        Optional<TemplatePo> ret=templatePoMapper.findByShopIdAndDefaultModel(shopId, Template.DEFAULT);
        if(ret.isPresent()){
            return Optional.of(cloneObj(ret.get(), Template.class));
        }
        return Optional.empty();
    }

    /**
    * 修改运费模板
    * @author Zhanyu Liu
    * @date 2022/11/30 9:42
    * @param bo
    * @param user
    * @throws RuntimeException
    */
    public void saveTemplateById(Template bo,UserDto user)throws RuntimeException{
        logger.debug("saveTemplateById: bo ={}, user = {}",bo, user);
        TemplatePo po = cloneObj(bo, TemplatePo.class);
        if (templatePoMapper.existsById(bo.getId())){
            putUserFields(po, "modifier", user);
            putGmtFields(po, "modified");
            templatePoMapper.save(po);
        }else{
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"运费模板", bo.getId()));
        }
        logger.debug("saveTemplateById: po ={}",po);
    }

}
