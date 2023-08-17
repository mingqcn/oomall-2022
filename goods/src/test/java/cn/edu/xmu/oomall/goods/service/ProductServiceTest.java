package cn.edu.xmu.oomall.goods.service;

import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.goods.GoodsTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wuzhicheng
 * @create 2022-12-15 18:31
 */
@SpringBootTest(classes = GoodsTestApplication.class)
@Transactional
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    public void deleteTemplateByTemplateIdTest(){
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setUserLevel(1);
        userDto.setName("admin2");
        userDto.setDepartId(1L);
        productService.deleteTemplateByTemplateId(1L, userDto);
    }
}
