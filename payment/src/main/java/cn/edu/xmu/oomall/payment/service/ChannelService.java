package cn.edu.xmu.oomall.payment.service;

import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.dao.ChannelDao;
import cn.edu.xmu.oomall.payment.dao.ShopChannelDao;
import cn.edu.xmu.oomall.payment.dao.bo.Channel;
import cn.edu.xmu.oomall.payment.dao.bo.ShopChannel;
import cn.edu.xmu.oomall.payment.service.dto.SimpleChannelDto;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.PLATFORM;
import static cn.edu.xmu.javaee.core.util.Common.createPageObj;

/**
 * @author 黄坤鹏
 * @date 2022/11/15 10:39
 */
@Service
@Transactional
public class ChannelService {
    private static  final Logger logger = LoggerFactory.getLogger(ChannelService.class);

    private ChannelDao channelDao;

    private ShopChannelDao shopChannelDao;

    @Autowired
    public ChannelService(ChannelDao channelDao, ShopChannelDao shopChannelDao){
        this.channelDao = channelDao;
        this.shopChannelDao = shopChannelDao;
    }

    /**
     * 修改支付渠道的状态
     * @param channelId 支付渠道ID
     * @param valid 修改的状态
     * @param user 登录用户
     */
    public ReturnObject updateChannelStatus(Long channelId, Byte valid, UserDto user){
        Channel channel = channelDao.findById(channelId);

        channel.setStatus(valid);

        return channelDao.saveById(channel, user);

    }

    /**
     * 获得有效的支付渠道
     * @param shopId 商铺Id
     * @param page 页码
     * @param pageSize 页大小
     */
    public PageDto<SimpleChannelDto> retrieveValidChannel(Long shopId, Integer page, Integer pageSize){
        PageInfo<Channel> channelPageInfo;
        //0 返回当前有效的平台支付渠道
        //其他 返回商铺支持的支付渠道
        if(PLATFORM == shopId) {
            channelPageInfo = channelDao.retrieveValid(page,pageSize);
        } else {
            //获得所有的shopChannel，再判断shopChannel和Channel的状态是否都有效
            List<ShopChannel> shopChannels = shopChannelDao.retrieveByShopId(shopId, page, pageSize, false).getList();
            if (null != shopChannels && shopChannels.size() > 0) {
                List<Channel> ret = shopChannels.stream()
                        .filter(shopChannel -> shopChannel.getStatus().equals(ShopChannel.VALID) &&
                                shopChannel.getChannel().getStatus().equals(Channel.VALID))
                        .map(ShopChannel::getChannel)
                        .skip((long) (page - 1) * pageSize).limit(pageSize)
                        .collect(Collectors.toList());
                channelPageInfo = new PageInfo<>(ret);
            } else {
                channelPageInfo = new PageInfo<>(new ArrayList<>());
            }

        }
        PageDto<SimpleChannelDto> pageObj = createPageObj(channelPageInfo, SimpleChannelDto.class);
        return pageObj;
    }
}

