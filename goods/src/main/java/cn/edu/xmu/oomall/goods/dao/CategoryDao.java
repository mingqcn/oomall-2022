//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.javaee.core.utils.CloneFactory;
import cn.edu.xmu.oomall.goods.dao.bo.Category;
import cn.edu.xmu.oomall.goods.mapper.jpa.CategoryPoMapper;
import cn.edu.xmu.oomall.goods.mapper.po.CategoryPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.MAX_RETURN;
import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
@RefreshScope
public class CategoryDao {
    private final static Logger logger = LoggerFactory.getLogger(CategoryDao.class);

    private final static String KEY = "C%d";

    @Value("${oomall.category.timeout}")
    private int timeout;

    private CategoryPoMapper categoryPoMapper;

    private RedisUtil redisUtil;

    @Autowired
    public CategoryDao(CategoryPoMapper categoryPoMapper, RedisUtil redisUtil) {
        this.categoryPoMapper = categoryPoMapper;
        this.redisUtil = redisUtil;
    }

    private Category getBo(CategoryPo po, Optional<String> redisKey){
//        Category bo = Category.builder().id(po.getId()).creatorId(po.getCreatorId()).creatorName(po.getCreatorName()).gmtCreate(po.getGmtCreate()).gmtModified(po.getGmtModified()).modifierId(po.getModifierId()).modifierName(po.getModifierName())
//                .pid(po.getPid()).name(po.getName()).commissionRatio(po.getCommissionRatio()).build();
        Category bo = CloneFactory.copy(new Category(), po);
        this.setBo(bo);
        redisKey.ifPresent(key -> redisUtil.set(key, bo, timeout));
        return bo;
    }

    private void setBo(Category bo){
        bo.setCategoryDao(this);
    }

    public Category findById(Long id) throws RuntimeException{
        if (null == id){
            return null;
        }

        String key = String.format(KEY, id);

        if (redisUtil.hasKey(key)){
            Category bo = (Category) redisUtil.get(key);
            setBo(bo);
            return bo;
        }

        Optional<CategoryPo> ret = this.categoryPoMapper.findById(id);
        if (ret.isPresent()){
            return this.getBo(ret.get(), Optional.of(key));
        } else{
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(),"商品类目", id));
        }
    }

    /*
     * 通过父类目id查找子类目
     */
    public PageDto<Category> retrieveByPidEquals(Long pid) throws RuntimeException {
        List<Category> ret = new ArrayList<>();
        Page<CategoryPo> poList = categoryPoMapper.findByPidEquals(pid, PageRequest.of(0, MAX_RETURN));
        if (poList.getSize() > 0)
            ret = poList.stream().map(po -> this.getBo(po, Optional.of(String.format(KEY, po.getId())))).collect(Collectors.toList());
        return new PageDto<>(ret, 0, ret.size());
    }

    /*
     * 创建类目
     */
    public void insert(Category bo, UserDto userDto) throws RuntimeException {
//        CategoryPo po = cloneObj(bo, CategoryPo.class);
        CategoryPo po = CloneFactory.copy(new CategoryPo(), bo);
        putUserFields(po, "creator", userDto);
        putGmtFields(po, "create");
        this.categoryPoMapper.save(po);
        bo.setId(po.getId());
    }

    /*
     * 根据Category.Id更新类目
     */
    public void save(Category bo, UserDto userDto) throws RuntimeException {
//        CategoryPo po = cloneObj(bo, CategoryPo.class);
        CategoryPo po = CloneFactory.copy(new CategoryPo(), bo);
        putUserFields(po, "modifier", userDto);
        putGmtFields(po, "modified");
        if (!this.categoryPoMapper.existsById(bo.getId()))
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商品类目", bo.getId()));
        this.categoryPoMapper.save(po);
        redisUtil.del(String.format(KEY, bo.getId()));
    }

    /*
     * 根据Category.Pid更新类目
     */
    public void saveByPid(Long pid, Category bo, UserDto userDto) throws RuntimeException {
//        CategoryPo po = cloneObj(bo, CategoryPo.class);
        CategoryPo po = CloneFactory.copy(new CategoryPo(), bo);
        putUserFields(po, "modifier", userDto);
        putGmtFields(po, "modified");
        logger.debug("Category po: " + po);
        this.categoryPoMapper.findByPidEquals(pid, PageRequest.of(0, MAX_RETURN)).forEach(category -> {
            po.setId(category.getId());
            this.categoryPoMapper.save(po);
            redisUtil.del(String.format(KEY, category.getId()));
        });
    }

    /*
     * 根据Category.Id删除类目
     */
    public void delById(Long id) throws RuntimeException {
        if (null == id)
            return;
        if (!this.categoryPoMapper.existsById(id))
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商品类目", id));
        this.categoryPoMapper.deleteById(id);
        redisUtil.del(String.format(KEY, id));
    }
}
