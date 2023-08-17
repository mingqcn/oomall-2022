package cn.edu.xmu.oomall.shop.service.listener;

import cn.edu.xmu.javaee.core.util.JacksonUtil;
import cn.edu.xmu.oomall.shop.mapper.RegionTemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.TemplatePoMapper;
import cn.edu.xmu.oomall.shop.mapper.po.RegionTemplatePo;
import cn.edu.xmu.oomall.shop.mapper.po.TemplatePo;
import cn.edu.xmu.oomall.shop.service.TemplateService;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
@RocketMQTransactionListener
public class DelTemplateListener implements RocketMQLocalTransactionListener{

    private TemplateService templateService;

    private TemplatePoMapper templatePoMapper;

    private RegionTemplatePoMapper regionTemplatePoMapper;

    @Autowired
    public DelTemplateListener(TemplateService templateService,
                               TemplatePoMapper templatePoMapper,
                               RegionTemplatePoMapper regionTemplatePoMapper
    ){
        this.templateService=templateService;
        this.regionTemplatePoMapper=regionTemplatePoMapper;
        this.templatePoMapper=templatePoMapper;
    }
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String body = new String((byte[]) msg.getPayload(), StandardCharsets.UTF_8);
        Long templateId=JacksonUtil.toObj(body, Long.class);

        try{
            this.templateService.deleteTemplate(templateId);
        }catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        String body = new String((byte[]) msg.getPayload(), StandardCharsets.UTF_8);
        Long templateId=JacksonUtil.toObj(body, Long.class);
        Optional<TemplatePo> templatePo=templatePoMapper.findById(templateId);
        List<RegionTemplatePo> regionTemplatePoList=regionTemplatePoMapper.findByTemplateId(templateId);
        if(templatePo.isPresent()||!regionTemplatePoList.isEmpty()){
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }
}
