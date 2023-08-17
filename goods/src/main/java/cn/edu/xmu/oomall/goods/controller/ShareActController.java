package cn.edu.xmu.oomall.goods.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.goods.service.ShareActService;
import cn.edu.xmu.oomall.goods.service.dto.ShareActDto;
import cn.edu.xmu.oomall.goods.service.dto.SimpleShareActDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 黄坤鹏
 * @date 2022/11/30 17:46
 */
@RestController
@RequestMapping(value = "/shareactivities", produces = "application/json;charset=UTF-8")
public class ShareActController {
    private final Logger logger = LoggerFactory.getLogger(UnAuthorizedController.class);

    private ShareActService shareActService;

    @Autowired
    public ShareActController(ShareActService shareActService){
        this.shareActService = shareActService;
    }

    /*
    查询分享活动
     */
    @Audit(departName = "shops")
    @GetMapping
    public ReturnObject retrieveShareAct(@RequestParam Long shopId,
                                         @RequestParam Long productId,
                                         @RequestParam(required = false, defaultValue = "1") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        PageDto<SimpleShareActDto> ret = shareActService.retrieveByShopIdAndProductIdAndInvalidEquals(shopId, productId, page, pageSize);
        return new ReturnObject(ret);
    }

    /*
    查询分享活动详情
     */
    @Audit(departName = "shops")
    @GetMapping("/{id}")
    public ReturnObject findShareActById(@PathVariable Long id){
        ShareActDto ret = shareActService.findById(id);
        return new ReturnObject(ret);
    }

    /*
    计算分享返点
     */
    @Audit
    @GetMapping("/{id}/calculate")
    public ReturnObject calculateRebate(@PathVariable Long id,
                                        @RequestParam(required = false) Integer quantity,
                                        @LoginUser UserDto userDto){
        if(null != quantity && quantity < 0)
            throw new BusinessException(ReturnNo.FIELD_NOTVALID, String.format(ReturnNo.FIELD_NOTVALID.getMessage(), "分享成功下单数"));
        Long rebate = shareActService.caluculateShareActRebate(quantity, id);
        return new ReturnObject(rebate);
    }
}
