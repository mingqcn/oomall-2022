//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.dao.bo.Channel;
import cn.edu.xmu.oomall.payment.mapper.generator.ChannelPoMapper;
import cn.edu.xmu.oomall.payment.mapper.generator.po.ChannelPo;
import cn.edu.xmu.oomall.payment.mapper.generator.po.ChannelPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.util.Common.*;

@Repository
public class ChannelDao{

    private static final Logger logger = LoggerFactory.getLogger(ChannelDao.class);

    public static final String KEY = "C%d";

    private RedisUtil redisUtil;

    private ChannelPoMapper channelPoMapper;

    @Autowired
    public ChannelDao(RedisUtil redisUtil, ChannelPoMapper channelPoMapper) {
        this.redisUtil = redisUtil;
        this.channelPoMapper = channelPoMapper;
    }

    /**
     * 按照主键获得对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-20 11:44
     * @param id
     * @return
     * @throws RuntimeException
     */
    public Channel findById(Long id) throws RuntimeException {
        Channel channel = null;
        if (null != id) {
            logger.debug("findObjById: id = {}",id);
            String key = String.format(KEY, id);
            if (redisUtil.hasKey(key)) {
                channel = (Channel) redisUtil.get(key);
            } else {
                ChannelPo po = this.channelPoMapper.selectByPrimaryKey(id);
                if (null == po) {
                    throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "支付渠道", id));
                }
                channel = cloneObj(po, Channel.class);
                //永不过期
                redisUtil.set(key, channel, -1);
            }
        }
        return channel;
    }

    /**
     * 查询所有有效的支付渠道
     * @param page 页码
     * @param pageSize 页大小
     * @return
     */
    public PageInfo<Channel> retrieveValid(Integer page, Integer pageSize){
        List<Channel> ret;
        ChannelPoExample channelPoExample = new ChannelPoExample();
        ChannelPoExample.Criteria criteria = channelPoExample.createCriteria();
        criteria.andStatusEqualTo(Channel.VALID);
        PageHelper.startPage(page, pageSize, false);
        List<ChannelPo> validChannels = channelPoMapper.selectByExample(channelPoExample);

        if(null != validChannels && validChannels.size() > 0){
            ret = validChannels.stream().map(po -> cloneObj(po, Channel.class)).collect(Collectors.toList());
        } else {
            ret = new ArrayList<>();
        }
        return new PageInfo<>(ret);
    }

    /**
     * 更新支付渠道的状态
     */
    public ReturnObject saveById(Channel channel, UserDto user){
        ChannelPo po = cloneObj(channel, ChannelPo.class);
        putUserFields(po, "modifier", user);
        putGmtFields(po, "Modified");
        int ret = channelPoMapper.updateByPrimaryKeySelective(po);
        if(0 == ret){
            throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "支付渠道",po.getId()));
        }
        redisUtil.del(String.format(ChannelDao.KEY, po.getId()));
        return new ReturnObject(ReturnNo.OK);
    }

}
