//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.javaee.core.config;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.util.JacksonUtil;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionErrorDecoder implements ErrorDecoder {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionErrorDecoder.class);

    @Override
    public Exception decode(String s, Response response) {
        BusinessException exception = null;
        if (null != response){
            String responseString = response.body().toString();
            logger.debug("decode: responseString = {}",responseString);
            Integer errNo = JacksonUtil.parseInteger(responseString, "errno");
            String errMsg = JacksonUtil.parseString(responseString, "errmsg");
            exception =  new BusinessException(ReturnNo.getByCode(errNo), errMsg);
        }
        return exception;
    }
}
