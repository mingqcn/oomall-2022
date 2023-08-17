package cn.edu.xmu.oomall.wechatpay.util;

import lombok.Getter;

/**
 * @author ziyi guo
 * @date 2021/12/2
 */
@Getter
public class WeChatPayReturnObject<T> {

    /**
     * 自定义的错误码
     */
    WeChatPayReturnNo code = WeChatPayReturnNo.OK;

    /**
     * 错误信息
     */
    String errmsg = null;

    /**
     * 返回数据
     */
    private T data = null;

    /**
     * 默认构造函数，错误码为OK
     */
    public WeChatPayReturnObject() {
    }

    /**
     * 带值构造函数
     * @param data 返回值
     */
    public WeChatPayReturnObject(T data) {
        this();
        this.data = data;
    }

    /**
     * 有错误码的构造函数
     * @param code 错误码
     */
    public WeChatPayReturnObject(WeChatPayReturnNo code) {
        this.code = code;
    }

    /**
     * 有错误码和自定义message的构造函数
     * @param code 错误码
     * @param errmsg 自定义message
     */
    public WeChatPayReturnObject(WeChatPayReturnNo code, String errmsg) {
        this(code);
        this.errmsg = errmsg;
    }

    /**
     * 有错误码，自定义message和值的构造函数
     * @param code 错误码
     * @param data 返回值
     */
    public WeChatPayReturnObject(WeChatPayReturnNo code, T data) {
        this(code);
        this.data = data;
    }

    /**
     * 有错误码，自定义message和值的构造函数
     * @param code 错误码
     * @param errmsg 自定义message
     * @param data 返回值
     */
    public WeChatPayReturnObject(WeChatPayReturnNo code, String errmsg, T data) {
        this(code, errmsg);
        this.data = data;
    }

    /**
     * 错误信息
     * @return 错误信息
     */
    public String getErrmsg() {
        if (null != this.errmsg) {
            return this.errmsg;
        }else{
            return this.code.getMessage();
        }
    }

}
