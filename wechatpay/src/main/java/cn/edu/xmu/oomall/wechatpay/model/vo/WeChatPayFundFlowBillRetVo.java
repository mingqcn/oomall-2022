package cn.edu.xmu.oomall.wechatpay.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ziyi guo
 * @date 2021/12/1
 */
@Data
@AllArgsConstructor
public class WeChatPayFundFlowBillRetVo {

    private String hashType;
    private String hashValue;
    private String downloadUrl;

    public WeChatPayFundFlowBillRetVo(){
        this.hashType = "SHA1";
        this.hashValue = "79bb0f45fc4c42234a918000b2668d689e2bde04";
        this.downloadUrl = "https://api.mch.weixin.qq.com/v3/billdownload/file?token=xxx";
    }
}
