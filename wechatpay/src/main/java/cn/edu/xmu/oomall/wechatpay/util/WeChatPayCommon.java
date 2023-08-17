package cn.edu.xmu.oomall.wechatpay.util;

import cn.edu.xmu.oomall.wechatpay.model.VoObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ziyi guo
 * @date 2021/12/2
 */
public class WeChatPayCommon {

    public static Object decorateReturnObject(WeChatPayReturnObject returnObject) {

        Object data = returnObject.getData();
        Map<String, Object> obj = new LinkedHashMap<>();
        obj.put("errmsg", returnObject.getErrmsg());

        switch (returnObject.getCode()) {
            // 404
            case RESOURCE_NOT_EXISTS:
                return new ResponseEntity(obj, HttpStatus.NOT_FOUND);

            // 500
            case SYSTEM_ERROR:
                return new ResponseEntity(obj,HttpStatus.INTERNAL_SERVER_ERROR);

            // 400
            case ORDER_CLOSED:
            case ORDER_PAID:
            case PARAM_ERROR:
            case INVALID_REQUEST:
                return new ResponseEntity(obj,HttpStatus.BAD_REQUEST);

            //403
            case OUT_TRADE_NO_USED:
            case OUT_REFUND_NO_USED:
            case OUT_DIVPAY_NO_USED:
            case OUT_DIVREFUND_NO_USED:
            case REFUND_TRANSACTION_ERROR:
            case REFUND_AMOUNT_ERROR:
            case NO_AUTH:
            case NOT_ENOUGH:
                return new ResponseEntity(obj,HttpStatus.FORBIDDEN);
            //429
            case FREQUENCY_LIMITED:
            case RATELIMIT_EXCEED:
                return new ResponseEntity(obj,HttpStatus.TOO_MANY_REQUESTS);

            // 200
            case OK:
                obj.put("data", data);
                return new ResponseEntity(obj,HttpStatus.OK);

            default:
                obj.put("data", data);
                return obj;
        }
    }

    public static WeChatPayReturnObject getRetObject(WeChatPayReturnObject<VoObject> returnObject) {
        WeChatPayReturnNo code = returnObject.getCode();
        switch (code){
            case OK:
                VoObject data = returnObject.getData();
                if (data != null){
                    Object voObj = data.createVo();
                    return new WeChatPayReturnObject(voObj);
                }else{
                    return new WeChatPayReturnObject();
                }
            default:
                return new WeChatPayReturnObject(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

}
