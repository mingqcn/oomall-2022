//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.template;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.shop.dao.bo.Shop;
import cn.edu.xmu.oomall.shop.dao.bo.divide.DivideStrategy;
import cn.edu.xmu.oomall.shop.dao.bo.divide.PackAlgorithm;
import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.Template;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import cn.edu.xmu.oomall.shop.mapper.RegionTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.TemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.RegionTemplatePo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

/**
 * 运费模板的dao
 */
@Repository
public class RegionTemplateDao {
    private static final Logger logger = LoggerFactory.getLogger(RegionTemplateDao.class);

    private static final String KEY = "RT%d";

    private static final String RID_TID_KEY="R%dT%d";

    @Value("${oomall.shop.region-template.timeout}")
    private Long timeout;

    @Value("${oomall.shop.region-template.strategy}")
    private String strategy;

    @Value("${oomall.shop.region-template.algorithm}")
    private String algorithm;

    private RegionTemplatePoMapper regionTemplatePoMapper;
    private TemplatePoMapper templatePoMapper;

    private ApplicationContext context;

    private FreightDao freightDao;

    private RedisUtil redisUtil;

    @Autowired
    public RegionTemplateDao(ApplicationContext context,
                             RegionTemplatePoMapper regionTemplatePoMapper,
                             RedisUtil redisUtil,
                             FreightDao freightDao,
                             TemplatePoMapper templatePoMapper
    ){
        this.context = context;
        this.regionTemplatePoMapper = regionTemplatePoMapper;
        this.redisUtil = redisUtil;
        this.freightDao=freightDao;
        this.templatePoMapper=templatePoMapper;
    }

    /**
     * 返回Bean对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-22 16:11
     * @param po
     * @return
     */
    private TemplateDao findTemplateDao(RegionTemplatePo po){
        return (TemplateDao) context.getBean(po.getTemplateDao());
    }

    private RegionTemplate getBo(RegionTemplatePo po, Optional<String> redisKey){
        TemplateDao dao = this.findTemplateDao(po);
        RegionTemplate bo = dao.getRegionTemplate(po);
        this.setBo(bo);
        logger.debug("getBo: bo = {}",bo);
        redisKey.ifPresent(key->redisUtil.set(key,bo,timeout));
        return bo;
    }

    private void setBo(RegionTemplate bo){
        bo.setFreightDao(this.freightDao);
        DivideStrategy divideStrategy;
        PackAlgorithm packAlgorithm;
        try {
            packAlgorithm = (PackAlgorithm) Class.forName(this.algorithm).getDeclaredConstructor().newInstance();
            logger.debug("findById: packAlgorithm = {}",packAlgorithm);

            try {
                divideStrategy = (DivideStrategy) Class.forName(this.strategy).getDeclaredConstructor(PackAlgorithm.class).newInstance(packAlgorithm);
                bo.setStrategy(divideStrategy);
            } catch (Exception e) {
                logger.error("findById: message = {}",e.getMessage());
                throw new BusinessException(ReturnNo.APPLICATION_PARAM_ERR, String.format(ReturnNo.APPLICATION_PARAM_ERR.getMessage(), "oomall.shop.region-template.strategy"));
            }

        } catch (Exception e) {
            logger.error("findById: message = {}",e.getMessage());
            throw new BusinessException(ReturnNo.APPLICATION_PARAM_ERR, String.format(ReturnNo.APPLICATION_PARAM_ERR.getMessage(), "oomall.shop.region-template.algorithm"));
        }
    }

    /**
     * 根据关键字找到运费模板
     * @author Ming Qiu
     * <p>
     * date: 2022-11-22 12:22
     * @param id
     * @return
     * @throws RuntimeException
     */
    public RegionTemplate findById(Long id) throws RuntimeException {
        String key = String.format(KEY, id);
        if (redisUtil.hasKey(key)) {
            RegionTemplate bo= (RegionTemplate) redisUtil.get(key);
            this.setBo(bo);
            return bo;
        }
        logger.debug("findById: id = {}",id);

        Optional<RegionTemplatePo> ret = regionTemplatePoMapper.findById(id);
        if (ret.isEmpty()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "运费模板", id));
        } else {
            return getBo(ret.get(),Optional.ofNullable(key));
        }
    }

    /**
    * 根据运费模板id和地区id来查找地区模板信息
    * 如果没有与rid对应的地区模板，则会继续查询rid最近的上级地区模板
    * 用于计算运费
    * @author Zhanyu Liu
    * @date 2022/12/1 10:06
    * @param tid 运费模板id
    * @param rid 地区id
    * @throws RuntimeException
    */
    public RegionTemplate findByTemplateIdAndRegionId(Long tid,Long rid) throws RuntimeException {
        Optional<RegionTemplate> ret=this.retrieveByTemplateIdAndRegionId(tid,rid);
        //若没有与rid对应的地区模板，继续查找最近的上级地区模板
        if(ret.isEmpty()){
            List<Region> pRegions=freightDao.retrieveParentRegionsById(rid).getData();
            /*
             * 由近到远查询地区模板,只要找到一个不为空的地区模板就结束查询
             */
            for(Region r:pRegions){
                ret=this.retrieveByTemplateIdAndRegionId(tid,r.getId());
                if(ret.isPresent()){
                    break;
                }
            }
        }
        if(ret.isPresent()){
            RegionTemplate bo=ret.get();
            logger.debug("findByTemplateIdAndRegionId: regionTemplate={}",bo);
            return bo;
        }else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
    }

    /**
     * 根据运费模板id和地区id来查找地区模板信息
     * 如果没有与rid对应的地区模板，不会继续查询上级地区模板
     * @author Zhanyu Liu
     * @date 2022/12/1 10:06
     * @param tid 运费模板id
     * @param rid 地区id
     * @throws RuntimeException
     */
    public Optional<RegionTemplate> retrieveByTemplateIdAndRegionId(Long tid,Long rid) throws RuntimeException {
        Optional<RegionTemplatePo> ret=Optional.empty();
        String ridAndTidKey=String.format(RID_TID_KEY,rid,tid);
        //先用rid和tid的redisKey来寻找对应的地区模板id
        if(redisUtil.hasKey(ridAndTidKey)){
            Long id=(Long)redisUtil.get(ridAndTidKey);
            String key = String.format(KEY, id);
            //再用找到的地区模板id去redis中查找regionTemplate
            if (redisUtil.hasKey(key)) {
                return Optional.of((RegionTemplate) redisUtil.get(key));
            }
            //否则用地区模板id去mysql中查找
            ret=this.regionTemplatePoMapper.findById(id);
        } else{
            ret=this.regionTemplatePoMapper.findByTemplateIdAndRegionId(tid,rid);
        }
        RegionTemplate bo=null;
        if(ret.isPresent()){
            bo=getBo(ret.get(),Optional.ofNullable(String.format(KEY,ret.get().getId())));
            redisUtil.set(ridAndTidKey,bo.getId(),timeout);
        }
        logger.debug("retrieveByTemplateIdAndRegionId: regionTemplate={}",bo);
        return Optional.ofNullable(bo);
    }

    /**
    * 根据模板id查找所有的地区模板信息
    * @author Zhanyu Liu
    * @date 2022/12/1 14:59
    * @param tid 模板id
    * @param page
    * @param pageSize
    * @throws RuntimeException
    */
    public List<RegionTemplate> retrieveByTemplateId(Long tid,Integer page,Integer pageSize)throws RuntimeException{
        List<RegionTemplatePo> ret=null;
        if(null!=page&&null!=pageSize){
            Pageable pageable= PageRequest.of(page-1,pageSize);
            logger.debug("retrieveByTemplateId:page={},pageSize={}",pageable.getPageNumber(),pageable.getPageSize());
            ret=regionTemplatePoMapper.findByTemplateId(tid,pageable).getContent();
        }
        else{
            ret=regionTemplatePoMapper.findByTemplateId(tid);
        }
        logger.debug("retrieveRegionTemplateById: po = {}",ret);
        List<RegionTemplate> templateList=null;
        if(null!=ret&&!ret.isEmpty()){
            templateList=ret.stream()
                    .map(po ->getBo(po,Optional.ofNullable(String.format(RegionTemplateDao.KEY,po.getId()))))
                    .collect(Collectors.toList());
        }else{
            templateList=new ArrayList<>();
        }
        logger.debug("retrieveRegionTemplateById: bo = {}",templateList);
        return templateList;
    }
    /**
     * 修改模板
     * @author Ming Qiu
     * <p>
     * date: 2022-11-22 17:14
     * @param bo
     * @param user
     * @throws RuntimeException
     */
    public List<String> save(RegionTemplate bo, UserDto user) throws RuntimeException{
        logger.debug("save: bo ={}, user = {}",bo, user);
        List<String> delKeys = new ArrayList<>();
        String key = String.format(KEY, bo.getId());
        RegionTemplatePo po = cloneObj(bo, RegionTemplatePo.class);
        TemplateDao dao = this.findTemplateDao(po);
        if (regionTemplatePoMapper.existsById(bo.getId())){
            dao.save(bo);
            putUserFields(po, "modifier", user);
            putGmtFields(po, "modified");
            delKeys.add(key);
            regionTemplatePoMapper.save(po);
        }else{
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"运费模板", bo.getId()));
        }
        return delKeys;
    }

    /**
     * 删除地区模板
     * @author Zhanyu Liu
     * @date 2022/12/1 14:59
     * @param tid
     * @param rid
     * @throws RuntimeException
     */
    public  List<String> delRegionByTemplateIdAndRegionId(Long tid,Long rid) throws RuntimeException{
        logger.debug("delRegionByTemplateIdAndRegionId: tid ={},rid={}",tid,rid);
        List<String> delKeys = new ArrayList<>();
        Optional<RegionTemplatePo> ret = regionTemplatePoMapper.findByTemplateIdAndRegionId(tid,rid);
        logger.debug("delRegionByTemplateIdAndRegionId: ret ={}",ret);
        if (ret.isPresent()) {
            RegionTemplatePo po=ret.get();
            String key = String.format(KEY, po.getId());
            TemplateDao dao=this.findTemplateDao(po);
            regionTemplatePoMapper.deleteById(po.getId());
            if(redisUtil.hasKey(key))
                redisUtil.del(key);
            delKeys.add(key);
            dao.deleteById(po.getObjectId());
        }else {
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        return delKeys;
    }

    /**
     * 删除运费模板，同步删除该模板所拥有的所有地区模板
     * @author Zhanyu Liu
     * @date 2022/11/30 9:42
     * @param id
     * @throws RuntimeException
     */
    public List<String> deleteTemplate(Long id)throws RuntimeException{
        logger.debug("deleteTemplate: id ={}",id);
        List<String> delKeys = new ArrayList<>();
        if (templatePoMapper.existsById(id)){
            templatePoMapper.deleteById(id);
            List<RegionTemplatePo> ret=regionTemplatePoMapper.deleteByTemplateId(id);
            logger.debug("deleteTemplate: ret ={}",ret);
            ret.forEach(po->{
                String key = String.format(KEY, po.getId());
                TemplateDao dao=this.findTemplateDao(po);
                if(redisUtil.hasKey(key)){
                    redisUtil.del(key);
                    delKeys.add(key);
                }
                dao.deleteById(po.getObjectId());
            });
        }else{
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"运费模板", id));
        }
        return delKeys;
    }

    /**
     * 新增模板
     * @author Ming Qiu
     * <p>
     * date: 2022-11-22 17:14
     * @param bo
     * @param user
     * @throws RuntimeException
     */
    public RegionTemplate insert(RegionTemplate bo, UserDto user) throws RuntimeException{
        logger.debug("save: bo ={}, user = {}",bo, user);
        RegionTemplatePo po = cloneObj(bo, RegionTemplatePo.class);
        TemplateDao dao = this.findTemplateDao(po);
        String objectId = dao.insert(bo);
        po.setObjectId(objectId);
        putUserFields(po, "creator", user);
        putGmtFields(po, "create");
        logger.debug("save: po = {}",po);
        RegionTemplatePo newPo = regionTemplatePoMapper.save(po);
        logger.debug("save: newPo = {}",newPo);
        bo.setId(newPo.getId());
        return bo;
    }
}
