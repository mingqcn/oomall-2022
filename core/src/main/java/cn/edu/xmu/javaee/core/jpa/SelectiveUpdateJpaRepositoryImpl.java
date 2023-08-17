package cn.edu.xmu.javaee.core.jpa;

import org.apache.ibatis.javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static cn.edu.xmu.javaee.core.model.Constants.IDNOTEXIST;

/**
 * 重写save方法
 * @param <T>
 * @param <ID>
 */
public class SelectiveUpdateJpaRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;

    private static Logger logger = LoggerFactory.getLogger(SelectiveUpdateJpaRepositoryImpl.class);

    @Autowired
    public SelectiveUpdateJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.em = entityManager;
    }

    /**
     * 通用save方法 ：新增/选择性更新
     * 没有id为insert
     * 存在id为update
     * 如果update不成功(数据库没有这条信息),则会将entity的id设为-1
     */
    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        Assert.notNull(entity, "Entity must not be null.");
        if (entityInformation.isNew(entity)) {
            em.persist(entity);
        } else {
            String sql = null;
            try {
                sql = getSql(entity);
            } catch (NotFoundException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            Query x = em.createNativeQuery(sql);
            if(0 == x.executeUpdate()) setEntityId(entity, IDNOTEXIST);
        }
        return entity;
    }

    /**
     * 获取update的Sql语句
     * @author jslsb
     * @param bo
     * @return
     * @throws NotFoundException
     * @throws IllegalAccessException
     */
    private String getSql(Object bo) throws NotFoundException, IllegalAccessException {
        Class<?> target = bo.getClass();
        StringBuffer stringBuffer = new StringBuffer("update ");
        logger.debug("getSql : className = {}",target.getName());
        //获取表名
        String table = target.getAnnotation(Table.class).name();
        logger.debug("getSql : table = {}",table);
        stringBuffer.append(table).append(" set ");
        //获取所有属性
        Field[] field = target.getDeclaredFields();
        //id列
        StringBuffer id = new StringBuffer("");
        //判断属性是否为null
        Arrays.stream(field).distinct().forEach(pp->{
            pp.setAccessible(true);
            boolean Is_String = pp.getType().getSimpleName().equals("String")||pp.getType().getSimpleName().equals("LocalDateTime");
            Object object = getValueByPropertyName(bo, pp.getName());
            logger.debug("getSql : pp = {}, object  = {}",pp,object);
            String column = getColumn(pp.getName());
            if(column.equals("id"))id.append(object);
            logger.debug("getSql : columns = {}",column);
            if(null != object && !column.equals("id")){
                stringBuffer.append(column).append(" = ");
                StringBuffer s = new StringBuffer();
                if(Is_String)s.append("'").append(object.toString()).append("'").append(", ");
                else s.append(object.toString()).append(", ");
                stringBuffer.append(s.toString());
            }
        });
        stringBuffer.deleteCharAt(stringBuffer.length()-2).append("where id = ").append(id.toString()).append(";");
        logger.info("getSql : sql = {}",stringBuffer.toString());

        return stringBuffer.toString();
    }

    /**
     * 获取列
     * @param name
     * @return
     */
    private String getColumn(String name){
        StringBuffer stringBuffer = new StringBuffer("");
        for(int i = 0; i<name.length(); i++){
            char ch = name.charAt(i);
            if(Character.isUpperCase(ch))stringBuffer.append('_');
            stringBuffer.append(Character.toLowerCase(ch));
        }
        return stringBuffer.toString();
    }

    /**
     * 获取值
     * @param obj
     * @param propertyName
     * @return
     */
    private Object getValueByPropertyName(Object obj, String propertyName) {
        StringBuffer stringBuffer = new StringBuffer("get");
        stringBuffer.append(Character.toUpperCase(propertyName.charAt(0))).append(propertyName.substring(1));
        logger.info("getValueByPropertyName : {} = {}","method",stringBuffer.toString());
        try {
            return obj.getClass().getMethod(stringBuffer.toString()).invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 设置实体id
     * @param bo
     * @param id
     */
    private void setEntityId(Object bo,Object id){
        try {
            bo.getClass().getMethod("setId",Object.class).invoke(bo,id);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}

