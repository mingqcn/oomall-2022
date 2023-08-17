package cn.edu.xmu.oomall.goods.controller;


import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.goods.controller.vo.*;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.oomall.goods.dao.bo.Product;
import cn.edu.xmu.oomall.goods.dao.bo.Template;
import cn.edu.xmu.oomall.goods.controller.vo.CreateCategoryVo;
import cn.edu.xmu.oomall.goods.controller.vo.UpdateCategoryVo;
import cn.edu.xmu.oomall.goods.service.CategoryService;
import cn.edu.xmu.oomall.goods.service.OnsaleService;
import cn.edu.xmu.oomall.goods.service.ProductDraftService;
import cn.edu.xmu.oomall.goods.dao.bo.Onsale;
import cn.edu.xmu.oomall.goods.service.ProductService;
import cn.edu.xmu.oomall.goods.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.TextScore;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.util.Common.cloneObj;
import static cn.edu.xmu.javaee.core.util.Common.createPageObj;

/**
 * 商品控制器
 * @author Ming Qiu
 */
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/shops/{shopId}", produces = "application/json;charset=UTF-8")
public class AdminProductController{

    private final Logger logger = LoggerFactory.getLogger(AdminProductController.class);


    private OnsaleService onsaleService;
    private ProductService productService;
    private ProductDraftService productDraftService;
    private CategoryService categoryService;

    @Autowired
    public AdminProductController(ProductService productService, OnsaleService onSaleService, ProductDraftService productDraftService, CategoryService categoryService) {
        this.productService = productService;
        this.onsaleService=onSaleService;
        this.productDraftService = productDraftService;
        this.categoryService = categoryService;
    }

    @GetMapping("/onsales/{id}")
    public ReturnObject getOnsaleById(@PathVariable Long shopId, @PathVariable Long id) {
        return new ReturnObject(onsaleService.findById(id));

    }

    @PostMapping("/products/{id}/onsales")
    @Audit(departName = "shops")
    public ReturnObject addOnsale(@PathVariable Long shopId,
                                  @PathVariable("id") Long id,
                                  @Validated@RequestBody OnSaleVo body,
                                  @LoginUser UserDto user){

        if (body.getBeginTime().isAfter(body.getEndTime())){
            throw new BusinessException(ReturnNo.LATE_BEGINTIME);
        }
        Onsale onsale = Onsale.builder().price(body.getPrice()).beginTime(body.getBeginTime()).endTime(body.getEndTime()).maxQuantity(body.getMaxQuantity())
                .quantity(body.getQuantity()).type(body.getType()).build();
        SimpleOnsaleDto dto = onsaleService.insert(shopId,id, onsale, user);
        return new ReturnObject(dto);
    }

    @GetMapping("/products/{id}/onsales")
    @Audit(departName = "shops")
    public ReturnObject getAllOnsale(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @PathVariable(value = "id",required = true) Long id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        PageDto<SimpleOnsaleDto> pageDto = onsaleService.retrieveByProductId(shopId,id, page, pageSize);
        return new ReturnObject(pageDto);
    }

    @PutMapping("/onsales/{id}/valid")
    @Audit(departName = "shops")
    public ReturnObject validOnsale(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @PathVariable(value = "id",required = true) Long id,
            @LoginUser UserDto user
    ){
        onsaleService.validateOnsale(shopId,id,user);
        return new ReturnObject();
    }

    @PutMapping("/onsales/{id}/invalid")
    @Audit(departName = "shops")
    public ReturnObject invalidOnsale(
            @PathVariable(value = "shopId",required = true) Long shopId,
            @PathVariable(value = "id",required = true) Long id,
            @LoginUser UserDto user
    ){
        onsaleService.invalidateOnsale(shopId,id,user);
        return new ReturnObject();
    }


    @PutMapping("/onsales/{id}")
    @Audit(departName = "shops")
    public ReturnObject putOnsaleId(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @Validated @RequestBody OnSaleVo body,
            @LoginUser UserDto user
            )
    {
        Onsale onsale = Onsale.builder().price(body.getPrice()).beginTime(body.getBeginTime()).endTime(body.getEndTime()).maxQuantity(body.getMaxQuantity())
                .quantity(body.getQuantity()).type(body.getType()).build();
        onsaleService.save(shopId, id, onsale, user);

        return new ReturnObject();
    }

    @DeleteMapping("/onsales/{id}")
    @Audit(departName = "shops")
    public ReturnObject delOnsaleId(
            @PathVariable("shopId") Long shopId,
            @PathVariable("id") Long id,
            @LoginUser UserDto user
    )
    {
        onsaleService.delete(shopId,id,user);
        return new ReturnObject();
    }

    /**
     * 商铺管理员申请增加新的Product
     * @author wuzhicheng
     * @param shopId
     * @param user
     * @return
     */
    @PostMapping("/draftproducts")
    public ReturnObject createSku(@PathVariable Long shopId,
                                  @RequestBody @Validated ProductDraftVo productDraftVo,
                                  @LoginUser UserDto user){
        SimpleProductDraftDto sku = this.productDraftService.createSku(shopId, productDraftVo.getName(), productDraftVo.getOriginalPrice(),
                productDraftVo.getCategoryId(), productDraftVo.getOriginPlace(), user);
        return new ReturnObject(ReturnNo.CREATED, ReturnNo.CREATED.getMessage(), sku);
    }

    /**
     * 管理员或店家物理删除审核中的Products
     * @author wuzhicheng
     * @param shopId
     * @param id
     * @param user
     * @return
     */
    @DeleteMapping("/draftproducts/{id}")
    public ReturnObject delProducts(@PathVariable Long shopId, @PathVariable Long id,
                                    @LoginUser UserDto user){
        this.productDraftService.delProducts(shopId, id, user);
        return new ReturnObject();
    }

    /**
     * 管理员或店家修改审核中的Products
     * @author wuzhicheng
     * @param shopId
     * @param id
     * @param user
     * @param productDraftVo
     * @return
     */
    @PutMapping("/draftproducts/{id}")
    public ReturnObject modifySKU(@PathVariable Long shopId, @PathVariable Long id, @LoginUser UserDto user,
                                  @Validated @RequestBody ProductDraftVo productDraftVo){
        this.productDraftService.modify(shopId, id, productDraftVo, user);
        return new ReturnObject();
    }

    /**
     * 店家查看货品信息详情
     * @author wuzhicheng
     * @param shopId
     * @param id
     * @return
     */
    @GetMapping("products/{id}")
    public ReturnObject getProductId(@PathVariable Long shopId, @PathVariable Long id){
        FullProductDto productById = this.productService.getAdminProductById(shopId, id);
        return new ReturnObject(productById);
    }

    /**
     * 店家修改货品信息
     * @author wuzhicheng
     * @param shopId
     * @param id
     * @param user
     * @return
     */
    @PutMapping("products/{id}")
    public ReturnObject putProductId(@PathVariable Long shopId, @PathVariable Long id, @LoginUser UserDto user,
                                     @RequestBody ProductModVo productModVo){
        this.productService.updateProduct(shopId, id, user, productModVo);
        return new ReturnObject();
    }

    /**
     * 查询商品的运费模板
     * @author wuzhicheng
     * @param shopId
     * @param id
     * @return
     */
    @GetMapping("/products/{id}/templates")
    public ReturnObject getProductTempalte(@PathVariable Long shopId, @PathVariable Long id){
        Template productTemplate = this.productService.getProductTemplate(shopId, id);
        return new ReturnObject(productTemplate);
    }

    /**
     * 管理员查看运费模板用到的商品
     * @param shopId
     * @param fid
     * @return
     */
    @GetMapping("/templates/{fid}/products")
    public ReturnObject getTempalteProduct(@PathVariable Long shopId, @PathVariable Long fid,
                                           @RequestParam(required = false,defaultValue = "1") Integer page,
                                           @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        PageDto<IdNameDto> templateProduct = this.productService.getTemplateProduct(shopId, fid, page, pageSize);
        return new ReturnObject(templateProduct);
    }

    /**
     * 货品发布
     * @param shopId
     * @param id
     * @param user
     * @return
     */
    @PutMapping("/draftproducts/{id}/publish")
    public ReturnObject putGoods(@PathVariable Long shopId, @PathVariable Long id, @LoginUser UserDto user){
        IdNameDto idNameDto = this.productDraftService.putGoods(shopId, id, user);
        return new ReturnObject(idNameDto);
    }

    /**
     * 管理员解禁商品
     * @param shopId
     * @param id
     * @param user
     * @return
     */
    @PutMapping("/products/{id}/allow")
    public ReturnObject allowGoods(@PathVariable Long shopId, @PathVariable Long id, @LoginUser UserDto user){
        this.productService.allowGoods(shopId, id, user);
        return new ReturnObject();
    }

    /**
     * 平台管理员禁售商品
     * @param shopId
     * @param id
     * @param user
     * @return
     */
    @PutMapping("/products/{id}/prohibit")
    public ReturnObject prohibitGoods(@PathVariable Long shopId, @PathVariable Long id, @LoginUser UserDto user){
        this.productService.prohibitGoods(shopId, id, user);
        return new ReturnObject();
    }

    /**
     * 店家查看草稿商品
     * @param shopId
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/draftproducts")
    public ReturnObject getAllProductDraft(@PathVariable Long shopId,
                                           @RequestParam(required = false,defaultValue = "1") Integer page,
                                           @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        PageDto<ProductDraftDto> allProductDraft = productDraftService.getAllProductDraft(shopId, page, pageSize);
        return new ReturnObject(allProductDraft);
    }

    /**
     * 店家查看草稿商品详情
     * @param shopId
     * @param id
     * @return
     */
    @GetMapping("/draftproducts/{id}")
    public ReturnObject getProductDraft(@PathVariable Long shopId, @PathVariable Long id){
        ProductDraftDto productDraft = this.productDraftService.getProductDraft(shopId, id);
        return new ReturnObject(productDraft);
    }

    /**
     * 将两个商品设为相关
     * @param shopId
     * @param id
     * @param user
     * @return
     */
    @PostMapping("/products/{id}/relations")
    public ReturnObject relateProductId(@PathVariable Long shopId, @PathVariable Long id, @RequestBody @Validated RelateProductVo relateProductVo,
                                        @LoginUser UserDto user){
        this.productService.relateProductId(shopId, id, relateProductVo.getProductId(), user);
        return new ReturnObject();
    }

    /**
     * 将两个商品取消相关
     * @param shopId
     * @param id
     * @param user
     * @return
     */
    @DeleteMapping("/products/{id}/relations")
    public ReturnObject delRelateProduct(@PathVariable Long shopId, @PathVariable Long id, @RequestParam Long productId,
                                         @LoginUser UserDto user){
        this.productService.delRelateProduct(shopId, id, productId, user);
        return new ReturnObject();
    }

    @GetMapping("/orphoncategories")
    @Audit(departName = "shops")
    public ReturnObject getOrphonCategories(@PathVariable("shopId") Long shopId) {
        if (PLATFORM != shopId)
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT, "ShopId 必须为0");
        return new ReturnObject(categoryService.retrieveOrphonCategories());
    }

    // ? id作用?
    @PostMapping("/categories/{id}/subcategories")
    @Audit(departName = "shops")
    public ReturnObject createSubCategories(@PathVariable("shopId") Long shopId,
                                            @PathVariable("id") Long id,
                                            @Validated @RequestBody CreateCategoryVo createCategoryVo,
                                            @LoginUser UserDto creator) {
        if (PLATFORM != shopId)
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT, "ShopId 必须为0");
        return new ReturnObject(categoryService.createSubCategory(id, createCategoryVo, creator));
    }

    @PutMapping("/categories/{id}")
    @Audit(departName = "shops")
    public ReturnObject updateCategory(@PathVariable("shopId") Long shopId,
                                       @PathVariable("id") Long id,
                                       @Validated @RequestBody UpdateCategoryVo updateCategoryVo,
                                       @LoginUser UserDto modifier) {
        if (PLATFORM != shopId)
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT, "ShopId 必须为0");
        if (id == updateCategoryVo.getPid())
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, String.format(ReturnNo.FIELD_NOTVALID.getMessage(), "pid"));
        categoryService.updateCategory(id, updateCategoryVo, modifier);
        return new ReturnObject();
    }

    @DeleteMapping("/categories/{id}")
    @Audit(departName = "shops")
    public ReturnObject deleteCategory(@PathVariable("shopId") Long shopId,
                                       @PathVariable("id") Long id,
                                       @LoginUser UserDto userDto) {
        if (PLATFORM != shopId)
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT, "ShopId 必须为0");
        categoryService.deleteCategory(id, userDto);
        return new ReturnObject();
    }
}
