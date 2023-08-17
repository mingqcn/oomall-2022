package cn.edu.xmu.oomall.wechatpay.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ziyi guo
 * @date 2021/11/30
 */
@Data
@AllArgsConstructor
public class WeChatPayPrepayRetVo {

    private String prepayId;

    public WeChatPayPrepayRetVo(){
        this.prepayId = "wx26112221580621e9b071c00d9e093b0000";
    }

}
