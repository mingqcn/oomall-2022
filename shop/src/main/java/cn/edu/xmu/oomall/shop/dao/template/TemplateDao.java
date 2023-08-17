//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.template;

import cn.edu.xmu.oomall.shop.dao.bo.template.Piece;
import cn.edu.xmu.oomall.shop.dao.bo.template.RegionTemplate;
import cn.edu.xmu.oomall.shop.dao.bo.template.TemplateType;
import cn.edu.xmu.oomall.shop.dao.bo.template.Weight;
import cn.edu.xmu.oomall.shop.mapper.po.RegionTemplatePo;
import cn.edu.xmu.oomall.shop.service.dto.RegionTemplateDto;

import java.util.HashMap;
import java.util.Map;

public interface TemplateDao {
    public static final String PIECE = "pieceTemplateDao";
    public static final String WEIGHT = "weightTemplateDao";

    public static Map<String, TemplateType> TYPE = new HashMap<>(){
        {
            put( PIECE, new Piece());
            put( WEIGHT, new Weight());
        }
    };

    /**
     * 根据po结合mongodb中查询来的数据，返回RegionTemplate对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-22 13:06
     * @param po
     * @return
     */
    RegionTemplate getRegionTemplate(RegionTemplatePo po);

    RegionTemplateDto getRegionTemplateDto(RegionTemplate bo);

    /**
     * 新增和修改模板对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-22 17:12
     * @param bo
     * @return
     */
    void save(RegionTemplate bo) throws RuntimeException;

    void deleteById(String id) throws RuntimeException;

    String insert(RegionTemplate bo) throws RuntimeException;

}
