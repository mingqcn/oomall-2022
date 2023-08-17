package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.BloomFilter;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.javaee.core.utils.CloneFactory;
import cn.edu.xmu.oomall.goods.controller.vo.CreateCategoryVo;
import cn.edu.xmu.oomall.goods.controller.vo.UpdateCategoryVo;
import cn.edu.xmu.oomall.goods.dao.CategoryDao;
import cn.edu.xmu.oomall.goods.dao.ProductDao;
import cn.edu.xmu.oomall.goods.dao.ProductDraftDao;
import cn.edu.xmu.oomall.goods.dao.bo.Category;
import cn.edu.xmu.oomall.goods.dao.bo.Product;
import cn.edu.xmu.oomall.goods.dao.bo.ProductDraft;
import cn.edu.xmu.oomall.goods.service.dto.CategoryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.clearFields;

@Service
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private ProductDao productDao;

    private ProductDraftDao productDraftDao;

    private CategoryDao categoryDao;

    private RedisUtil redisUtil;

    @Autowired
    public CategoryService(ProductDao productDao, ProductDraftDao productDraftDao, CategoryDao categoryDao, RedisUtil redisUtil) {
        this.productDao = productDao;
        this.productDraftDao = productDraftDao;
        this.categoryDao = categoryDao;
        this.redisUtil = redisUtil;
    }

    /**
     * 获取某个商品类目的所有子类目
     *
     * @param id 商品类目Id
     * @return 子类目列表
     */
    @Transactional
    public PageDto<CategoryDto> retrieveSubCategories(Long id) {
        logger.debug("getSubCategories: id = {}", id);
        String key = BloomFilter.PRETECT_FILTERS.get("CategoryPid");
        if (redisUtil.bfExist(key, id)) {
            return new PageDto<>(new ArrayList<>(), 0, 0);
        }
        PageDto<Category> categories = categoryDao.retrieveByPidEquals(id);
        if (categories.getPageSize() == 0) {
            redisUtil.bfAdd(key, id);
        }
        return new PageDto<>(categories.getList().stream().map(category -> CloneFactory.copy(new CategoryDto(), category)).collect(Collectors.toList()), categories.getPage(), categories.getPageSize());
    }

    /**
     * 获取没有父类目（pid=-1）的商品类目
     * @return
     */
    @Transactional
    public PageDto<CategoryDto> retrieveOrphonCategories() {
        PageDto<Category> orphonCategories = categoryDao.retrieveByPidEquals(-1L);
        if (orphonCategories.getPageSize() == 0)
            return new PageDto<>(new ArrayList<>(), 0, 0);
        return new PageDto<>(orphonCategories.getList().stream().map(category -> CloneFactory.copy(new CategoryDto(), category)).collect(Collectors.toList()), orphonCategories.getPage(), orphonCategories.getPageSize());
    }

    /**
     * 创建商品类目
     * @param id 父类目id，若为0则为顶级类目
     * @param createCategoryVo 类目信息
     * @param creator 创建者
     * @return 创建的类目
     */
    @Transactional
    public CategoryDto createSubCategory(Long id, CreateCategoryVo createCategoryVo, UserDto creator) {
//        Category category = cloneObj(createCategoryVo, Category.class);
        Category category = CloneFactory.copy(new Category(), createCategoryVo);
        if (0 != id && null == this.categoryDao.findById(id))
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商品类目", id));
        category.setPid(id);
        this.categoryDao.insert(category, creator);
//        return cloneObj(category, CategoryDto.class);
        return CloneFactory.copy(new CategoryDto(), category);
    }

    /**
     * 修改商品类目
     * @param id 类目id
     * @param updateCategoryVo 类目信息
     * @param modifier 修改者
     */
    @Transactional
    public void updateCategory(Long id, UpdateCategoryVo updateCategoryVo, UserDto modifier) {
        Category parent = categoryDao.findById(updateCategoryVo.getPid());
        if (null == parent)
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "商品类目", updateCategoryVo.getPid()));
        if (parent.getPid() == id)
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, String.format(ReturnNo.FIELD_NOTVALID.getMessage(), "父类目Id"));

//        Category category = cloneObj(updateCategoryVo, Category.class);
        Category category = CloneFactory.copy(new Category(), updateCategoryVo);
        category.setId(id);
        this.categoryDao.save(category, modifier);
    }

    @Transactional
    public void deleteCategory(Long id, UserDto userDto) {
        {
            Category category = new Category();
            clearFields(category);
            category.setPid(-1L);
            this.categoryDao.saveByPid(id, category, userDto);
        } {
            Product product = new Product();
            clearFields(product);
            product.setCategoryId(-1L);
            this.productDao.saveByCategoryId(id, product, userDto);
        } {
            ProductDraft productDraft = new ProductDraft();
            clearFields(productDraft);
            productDraft.setCategoryId(-1L);
            this.productDraftDao.saveByCategoryId(id, productDraft);
        }
        this.categoryDao.delById(id);
    }
}
