//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.template;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.WeightTemplate;
import cn.edu.xmu.oomall.shop.mapper.po.RegionTemplatePo;
import cn.edu.xmu.oomall.shop.mapper.WeightTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.WeightTemplatePo;
import cn.edu.xmu.oomall.shop.service.dto.RegionTemplateDto;
import cn.edu.xmu.oomall.shop.service.dto.WeightTemplateDto;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static cn.edu.xmu.javaee.core.util.Common.cloneObj;
import static cn.edu.xmu.javaee.core.util.Common.copyObj;

@Repository
public class WeightTemplateDao implements TemplateDao{

    private static final Logger logger = LoggerFactory.getLogger(WeightTemplateDao.class);

    private WeightTemplatePoMapper mapper;

    @Autowired
    public WeightTemplateDao(WeightTemplatePoMapper weightTemplatePoMapper) {
        this.mapper = weightTemplatePoMapper;
    }

    @Override
    public RegionTemplate getRegionTemplate(RegionTemplatePo po) throws RuntimeException {
        WeightTemplate bo = cloneObj(po, WeightTemplate.class);
        Optional<WeightTemplatePo> wPo = this.mapper.findById(new ObjectId(po.getObjectId())) ;
        wPo.ifPresent(templatePo ->{
            copyObj(templatePo, bo);
            bo.setObjectId(templatePo.getObjectId().toString());
        });
        return bo;
    }

    @Override
    public RegionTemplateDto getRegionTemplateDto(RegionTemplate bo) {
        UserDto creator=new UserDto();
        creator.setName(bo.getCreatorName());
        creator.setId(bo.getCreatorId());
        UserDto modifier=new UserDto();
        modifier.setName(bo.getModifierName());
        modifier.setId(bo.getModifierId());
        return WeightTemplateDto.builder().id(bo.getId()).unit(bo.getUnit()).firstWeight(((WeightTemplate)bo).getFirstWeight())
                .firstWeightPrice(((WeightTemplate)bo).getFirstWeightPrice()).thresholds(((WeightTemplate)bo).getThresholds()).creator(creator)
                .modifier(modifier).gmtCreate(bo.getGmtCreate()).gmtModified(bo.getGmtModified()).region(bo.getRegion()).build();
    }

    @Override
    public void save(RegionTemplate bo) throws RuntimeException{
        WeightTemplatePo po = cloneObj(bo, WeightTemplatePo.class);
        po.setObjectId(new ObjectId(bo.getObjectId()));
        this.mapper.save(po);
    }

    @Override
    public void deleteById(String id) throws RuntimeException{
        this.mapper.deleteById(new ObjectId(id));
    }

    @Override
    public String insert(RegionTemplate bo) throws RuntimeException {
        WeightTemplatePo po = cloneObj(bo, WeightTemplatePo.class);
        WeightTemplatePo newPo = this.mapper.insert(po);
        return newPo.getObjectId().toString();
    }

}
