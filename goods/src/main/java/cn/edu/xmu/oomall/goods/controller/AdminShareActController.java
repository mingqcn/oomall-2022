package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.goods.controller.vo.ShareActVo;
import cn.edu.xmu.oomall.goods.service.ShareActService;
import cn.edu.xmu.oomall.goods.service.dto.FullShareActDto;
import cn.edu.xmu.oomall.goods.service.dto.SimpleOnsaleDto;
import cn.edu.xmu.oomall.goods.service.dto.SimpleShareActDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;

/**
 * @author 黄坤鹏
 * @date 2022/11/30 10:19
 */
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/shops/{shopId}", produces = "application/json;charset=UTF-8")
public class AdminShareActController {

    private final Logger logger = LoggerFactory.getLogger(AdminShareActController.class);

    private ShareActService shareActService;

    @Autowired
    public AdminShareActController(ShareActService shareActService){
        this.shareActService = shareActService;
    }

    @Audit(departName = "shops")
    @PostMapping("/shareactivities")
    public ReturnObject createShareAct(@PathVariable Long shopId,
                                       @Validated @RequestBody ShareActVo shareActVo,
                                       @LoginUser UserDto userDto){
        SimpleShareActDto shareAct = shareActService.createShareAct(shopId, shareActVo, userDto);
        return new ReturnObject(shareAct);
    }

    @Audit(departName = "shops")
    @PostMapping("/onsale/{id}/shareact/{sid}")
    public ReturnObject createActivityOnsale(@PathVariable Long shopId,
                                             @PathVariable("id") Long onsaleId,
                                             @PathVariable("sid") Long actId,
                                             @LoginUser UserDto userDto){
        if(PLATFORM != shopId)
            throw new BusinessException(ReturnNo.AUTH_NO_RIGHT);
        SimpleOnsaleDto activityOnsale = shareActService.createActivityOnsale(onsaleId, actId, userDto);
        return new ReturnObject(activityOnsale);
    }

    @Audit(departName = "shops")
    @DeleteMapping("/onsale/{id}/shareact/{sid}")
    public ReturnObject delActivityOnsale(@PathVariable Long shopId,
                                          @PathVariable("id") Long onsaleId,
                                          @PathVariable("sid") Long actId){
        shareActService.cancelActivityOnsale(shopId, onsaleId, actId);
        return new ReturnObject(ReturnNo.OK);
    }

    @Audit(departName = "shops")
    @PutMapping("/shareactivities/{id}")
    public ReturnObject updateShareActByShopIdAndActId(@PathVariable Long shopId,
                                                       @PathVariable("id") Long actId,
                                                       @Validated @RequestBody ShareActVo shareActVo,
                                                       @LoginUser UserDto userDto){
        shareActService.updateByActId(shopId, actId, shareActVo, userDto);
        return new ReturnObject(ReturnNo.OK);
    }

    //无需登录api
    @GetMapping("/shareactivities/{id}")
    public ReturnObject findById(@PathVariable Long shopId,
                                 @PathVariable("id") Long actId){
        FullShareActDto fullShareActDto = shareActService.findByShopIdAndActId(shopId, actId);
        return new ReturnObject(fullShareActDto);
    }

    //无需登录api
    @GetMapping("/shareactivities")
    public ReturnObject retrieveByShopId(@PathVariable Long shopId,
                                         @RequestParam Long productId,
                                         @RequestParam Long onsaleId,
                                         @RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        PageDto<SimpleShareActDto> shareActivities = shareActService.retrieveByShopIdAndOnsaleIdAndProductId(shopId, productId, onsaleId, page, pageSize);
        return new ReturnObject(shareActivities);
    }

}
