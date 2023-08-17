package cn.edu.xmu.oomall.shop.jpa;

import cn.edu.xmu.javaee.core.util.Common;
import cn.edu.xmu.oomall.shop.ShopApplication;
import cn.edu.xmu.oomall.shop.ShopTestApplication;
import cn.edu.xmu.oomall.shop.dao.bo.template.Template;
import cn.edu.xmu.oomall.shop.mapper.ShopPoMapper;
import cn.edu.xmu.oomall.shop.mapper.TemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.ShopPo;
import cn.edu.xmu.oomall.shop.mapper.po.TemplatePo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = ShopTestApplication.class)
@Transactional
public class JpaTest {

    @Autowired
    ShopPoMapper shopPoMapper;
    @Autowired
    TemplatePoMapper templatePoMapper;
    @MockBean
    RocketMQTemplate rocketMQTemplate;
    @Test
    public void JapTest1(){
        ShopPo po = new ShopPo();
        po.setId(10L);
        po.setName("jslsb");
        Common.putGmtFields(po,"modified");
        shopPoMapper.save(po);
        Optional<ShopPo> newPo = shopPoMapper.findById(10L);
        assertThat(newPo.get().getName().equals("jslsb"));
        assertThat(newPo.get().getDeposit().equals(5000000L));
    }

    @Test
    public void JapTest2(){
        String name="123434567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678912341";

        TemplatePo po=new TemplatePo();
        po.setId(1L);
        po.setShopId(2L);
        po.setName(name);
        po.setDefaultModel(Template.COMMON);
        templatePoMapper.save(po);
        Optional<TemplatePo> template= templatePoMapper.findById(po.getId());
        TemplatePo tmp=template.orElse(null);
        assertThat(tmp.getId()).isEqualTo(po.getId());
        assertThat(tmp.getDefaultModel()).isEqualTo(Template.COMMON);
        assertThat(tmp.getName().substring(0,4)).isEqualTo("1234");
        assertThat(tmp.getCreatorId()).isEqualTo(1L);
    }
}
