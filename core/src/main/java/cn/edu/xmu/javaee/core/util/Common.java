//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.javaee.core.util;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 通用工具类
 *
 * @author Ming Qiu
 **/
public class Common {
    private static Logger logger = LoggerFactory.getLogger(Common.class);

    private static Pattern setPattern = Pattern.compile("set[A-Z][a-z][a-zA-Z0-9]*");

    private static Pattern getPattern = Pattern.compile("get[A-Z][a-z][a-zA-Z0-9]*");

    private static Pattern firstCharPattern = Pattern.compile("^.");

    /**
     * 生成九位数序号
     * 要保证同一服务的不同实例生成出的序号是不同的
     * @param  platform 机器号 如果一个服务有多个实例，机器号需不同，目前从1至36
     * @return 序号
     */
    public static String genSeqNum(int platform) {
        int maxNum = 36;
        int i;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmssS");
        LocalDateTime localDateTime = LocalDateTime.now();
        String strDate = localDateTime.format(dtf);
        StringBuffer sb = new StringBuffer(strDate);

        int count = 0;
        char[] str = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Random r = new Random();
        while (count < 2) {
            i = Math.abs(r.nextInt(maxNum));
            if (i >= 0 && i < str.length) {
                sb.append(str[i]);
                count++;
            }
        }
        if (platform > 36){
            platform = 36;
        } else if (platform < 1){
            platform = 1;
        }

        sb.append(str[platform-1]);
        return sb.toString();
    }

    /**
     * 将list对象转换承list对象
     * @author xucangbai
     * @param objs
     * @param targetClass
     * @return
     */
    public static <T> List<T> createListObj(List<? extends Object> objs,Class<T> targetClass)
    {
        assert (objs != null);
        return objs.stream().map(x-> cloneObj(x, targetClass)).collect(Collectors.toList());
    }

    /**
     * 将带PageInfo的对象转换为普通的对象
     * @param pageObjs
     * @param targetClass
     * @return
     * @author Ming Qiu
     */
    public static <T> PageDto<T> createPageObj(PageInfo<? extends Object> pageObjs , Class<T> targetClass) {
        if (null == pageObjs){
            return null;
        }
        List voObjs = createListObj(pageObjs.getList(), targetClass);
        return new PageDto(voObjs, pageObjs.getPageNum(),pageObjs.getPageSize());
    }

    /**
     * 根据clazz实例化一个对象，并浅克隆source中对应属性到这个新对象
     * 默认targetClass应该有无参构造函数
     * @author Ming Qiu
     * <p>
     * date: 2022-11-01 12:01
     * @param source      源对象
     * @param targetClass 目标对象类型
     * @return 浅克隆的target对象
     */
    public static <T> T cloneObj(Object source, Class<T> targetClass){
        logger.debug("cloneObj: source = {}",source);
        T target = null;
        try {
            //默认targetClass有无参构造函数
            target = targetClass.getConstructor().newInstance();
        }catch (Exception e){
            logger.error("cloneObj: create target object Exception = {}", e);
        }
        copyObj(source, target);
        logger.debug("cloneObj: source = {}, target = {}",source, target);
        return target;
    }


    /**
     * 把source中对应属性拷贝到target中
     * @author Ming Qiu
     * <p>
     * date: 2022-11-01 12:01
     * @param source      源对象
     * @param target 目标对象
     */
    public static void copyObj(Object source, Object target) {
        logger.debug("copyObj: source = {}, target = {}",source, target);
        Class sourceClass = source.getClass();
        Class targetClass = target.getClass();
        //默认targetClass有无参构造函数
        List<Method> sourceGetters = Arrays.stream(sourceClass.getMethods())
                .filter(method -> getPattern.matcher(method.getName()).matches()
                        && (0 == method.getParameterCount())
                        && !method.getDeclaringClass().equals(Object.class))
                .collect(Collectors.toList());

        Arrays.stream(targetClass.getMethods())
                .filter(method -> (setPattern.matcher(method.getName()).matches()
                        && (1 == method.getParameterCount())
                        && !method.getDeclaringClass().equals(Object.class)))
                .forEach(targetMethod -> {
                    logger.debug("copyObj: setter = {}",targetMethod.getName());
                    String name = String.format("get%s", targetMethod.getName().substring(3));
                    sourceGetters.stream().filter(method -> method.getName().equals(name)).
                            forEach(sourceMethod -> {
                                Object value = null;
                                try {
                                    value = sourceMethod.invoke(source);
                                } catch (Exception e) {
                                    logger.debug("copyObj: source object getter exception={}, name = {}", e, sourceMethod.getName());
                                }
                                logger.debug("copyObj: getter = {}, value = {}",name, value);
                                try {
                                    targetMethod.invoke(target, value);
                                } catch (Exception e2) {
                                    logger.debug("copyObj: target object setter exception={}, name = {}, value = {}", e2, targetMethod.getName(), value);
                                }
                            });
                });
        logger.debug("copyObj: source = {}, target = {}",source, target);
    }

    /**
     * 设置对象的user类属性
     * @author Ming Qiu
     * <p>
     * date: 2022-11-02 1:26
     * @param obj 对象
     * @param field 属性前缀
     * @param user 用户对象
     * @return
     */
    public static void putUserFields(Object obj, String field, UserDto user) throws BusinessException{
        if (null != user) {

            Class<?> aClass = obj.getClass();
            String upperCaseField = firstCharPattern.matcher(field).replaceFirst(m -> m.group().toUpperCase());
            try {
                Method idSetter = aClass.getMethod(String.format("set%sId", upperCaseField), Long.class);
                logger.debug("putUserFields: obj = {}, field = {}",obj, upperCaseField);
                idSetter.invoke(obj, user.getId());
            } catch (IllegalAccessException ex) {
                logger.info("putUserFields: obj = {}, e = {}", obj, ex);
            } catch (NoSuchMethodException e) {
                logger.info("putUserFields: obj = {}, e = {}", obj, e);
            } catch (InvocationTargetException e) {
                logger.info("putUserFields: obj = {}, e = {}", obj, e);
            }

            try {
                Method nameSetter = aClass.getMethod(String.format("set%sName", upperCaseField), String.class);
                logger.debug("putUserFields: obj = {}, field = {}",obj, upperCaseField);
                nameSetter.invoke(obj, user.getName());
            }catch (IllegalAccessException ex) {
                logger.info("putUserFields: obj = {}, e = {}", obj, ex);
            } catch (InvocationTargetException e) {
                logger.info("putUserFields: obj = {}, e = {}", obj, e);
            } catch (NoSuchMethodException e) {
                logger.info("putUserFields: obj = {}, e = {}", obj, e);
            }
        }
    }

    /**
     * 设置gmt属性为服务器当前时间
     * @author Ming Qiu
     * <p>
     * date: 2022-11-02 1:28
     * @param obj 对象
     * @param dateField 属性后缀
     * @return
     */
    public static void putGmtFields(Object obj, String dateField) throws BusinessException {
        try {
            Class<?> aClass = obj.getClass();
            String upperCaseField = firstCharPattern.matcher(dateField).replaceFirst(m -> m.group().toUpperCase());
            Method dateSetter = aClass.getMethod(String.format("setGmt%s", upperCaseField), LocalDateTime.class);
            dateSetter.invoke(obj, LocalDateTime.now());
        } catch (IllegalAccessException | NoSuchMethodException ex) {
            logger.info("putGmtFields: obj = {}, e = {}",obj,ex);
        } catch (InvocationTargetException e) {
            logger.info("putGmtFields: obj = {}, e = {}",obj,e);
        }
    }

    /**
     * 将除keep中属性以外的其他属性设成null
     * @author Ming Qiu
     * <p>
     * date: 2022-11-01 18:10
     * @param obj 清理对象
     * @param exceptFields 保留的属性列表
     */
    public static boolean clearFields(Object obj, String... exceptFields){
        Set<String> fieldSet = Set.of(exceptFields);
        Class<?> aClass = obj.getClass();
        Field[] fields = aClass.getDeclaredFields();
        logger.debug("clearFields: fields = {}, exceptFields = {} ", fields, exceptFields);
        AtomicBoolean ret = new AtomicBoolean(true);
        Arrays.stream(fields)
                .filter(field -> {
                    boolean noIt = !fieldSet.contains(field.getName());
                    logger.debug("clearFields: field = {}, noIt = {} ", field.getName(), noIt);
                    return noIt;})
                .forEach(field -> {
                    int mod = field.getModifiers();
                    if (!Modifier.isStatic(mod) && !Modifier.isFinal(mod)) {
                        logger.debug("clearFields: set {} of {} to null", field.getName(), obj);
                        field.setAccessible(true);
                        try {
                            field.set(obj, null);
                        } catch (IllegalAccessException e) {
                            logger.info("clearFields: can not set the {} of {} to null", field.getName(), obj);
                            ret.set(false);
                        }
                    }
                });
        return ret.get();
    }


}
