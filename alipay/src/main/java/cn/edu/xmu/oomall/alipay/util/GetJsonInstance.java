package cn.edu.xmu.oomall.alipay.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * 用于将json转为vo对象
 */
public class GetJsonInstance {

    public static Object getInstance(String jsonString, Class boClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Object bo = objectMapper.readValue(jsonString, boClass);
            return bo;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
