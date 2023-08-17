//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.payment.service;

import cn.edu.xmu.javaee.core.exception.BusinessException;
import cn.edu.xmu.javaee.core.model.dto.PageDto;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.dto.StatusDto;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.util.RedisUtil;
import cn.edu.xmu.oomall.payment.dao.*;
import cn.edu.xmu.oomall.payment.dao.bo.*;
import cn.edu.xmu.oomall.payment.service.channel.PayAdaptorFactory;
import cn.edu.xmu.oomall.payment.service.channel.PayAdaptor;
import cn.edu.xmu.oomall.payment.service.channel.dto.CheckResultDto;
import cn.edu.xmu.oomall.payment.service.channel.dto.PostDivPayAdaptorDto;
import cn.edu.xmu.oomall.payment.service.channel.dto.PostPayTransAdaptorDto;
import cn.edu.xmu.oomall.payment.service.dto.*;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.*;
import static cn.edu.xmu.javaee.core.util.Common.*;
import static cn.edu.xmu.oomall.payment.dao.bo.PayTrans.*;


/**
 * 支付的服务
 */
@Service
public class PaymentService {

    private static  final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private ShopChannelDao shopChannelDao;

    private ChannelDao channelDao;

    private PayTransDao payTransDao;

    private LedgerDao ledgerDao;

    private DivPayTransDao divPayTransDao;

    private DivRefundTransDao divRefundTransDao;

    private TransactionDao transactionDao;

    private PayAdaptorFactory factory;

    private RefundTransDao refundTransDao;

    private RedisUtil redisUtil;

    @Autowired
    public PaymentService(ShopChannelDao shopChannelDao,
                          PayTransDao payTransDao,
                          LedgerDao ledgerDao,
                          RefundTransDao refundTransDao,
                          DivRefundTransDao divRefundTransDao,
                          DivPayTransDao divPayTransDao,
                          TransactionDao transactionDao,
                          ChannelDao channelDao,
                          PayAdaptorFactory factory,
                          RedisUtil redisUtil) {
        this.shopChannelDao = shopChannelDao;
        this.channelDao = channelDao;
        this.payTransDao = payTransDao;
        this.ledgerDao = ledgerDao;
        this.factory = factory;
        this.refundTransDao = refundTransDao;
        this.divPayTransDao = divPayTransDao;
        this.divRefundTransDao = divRefundTransDao;
        this.transactionDao = transactionDao;
        this.redisUtil = redisUtil;
    }


    /**
     * 创建一个支付交易
     *
     * @param spOpenid      用户的支付id
     * @param shopChannelId 商铺渠道Id
     * @param amount        支付金额
     * @param user          当前登录用户
     * @return 支付交易对象
     * @author Ming Qiu
     * <p>
     * date: 2022-11-01 19:18
     */
    @Transactional
    public PayTransDto createPayment(LocalDateTime timeBegin, LocalDateTime timeExpire, String spOpenid, Long shopChannelId, Long amount, Long divAmount, UserDto user) throws BusinessException {
        ShopChannel shopChannel = this.shopChannelDao.findById(shopChannelId);
        Channel channel = shopChannel.getChannel();
        logger.debug("createPayment: shop = {}", shopChannel);
        if (ShopChannel.INVALID == shopChannel.getStatus() || Channel.INVALID == channel.getStatus()) {
            throw new BusinessException(ReturnNo.PAY_CHANNEL_INVALID, String.format(ReturnNo.PAY_CHANNEL_INVALID.getMessage(), channel.getName()));
        }

        PayTrans newObj = new PayTrans(timeBegin, timeExpire, spOpenid, amount, divAmount, shopChannel);
        payTransDao.save(newObj, user);
        newObj = payTransDao.findById(newObj.getId());

        PayAdaptor payChannel = this.factory.createPayAdaptor(shopChannel);
        logger.debug("createPayment: payChannel = {}, newObj = {}", payChannel, newObj);

        PostPayTransAdaptorDto adaptorDto = payChannel.createPayment(newObj);
        newObj.setPrepayId(adaptorDto.getPrepayId());
        clearFields(newObj, "id", "prepayId");
        try {
            payTransDao.saveById(newObj, null);
        }catch (BusinessException e){
            if  (ReturnNo.RESOURCE_ID_NOTEXIST == e.getErrno()){
                logger.error("createPayment: 回写支付对象失败。");
                throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR);
            }else{
                throw e;
            }
        }

        PayTransDto dto = cloneObj(newObj, PayTransDto.class);
        logger.debug("createPayment: dto = {}", dto);
        return dto;
    }

    /**
     * 查询台账
     * @author Ming Qiu
     * <p>
     * date: 2022-11-12 21:36
     * @param shopId
     * @param beginTime
     * @param endTime
     * @param type
     * @param channelId
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional
    public PageDto<LedgerDto> retrieveLedge(Long shopId, LocalDateTime beginTime, LocalDateTime endTime, Byte type, Long channelId, Integer page, Integer pageSize) {
        PageInfo<Ledger> ledgerList;
        if(channelId != null){
            ShopChannel shopChannel = this.shopChannelDao.findById(channelId);
            logger.debug("retrieveLedge: shopChannel = {}", shopChannel);
            if (PLATFORM != shopChannel.getShopId() && shopId != shopChannel.getShopId()) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "商铺渠道", channelId, shopId));
            }
            ledgerList = this.ledgerDao.retrieveLedgersByShopChannelAndBetweenSuccessTime(beginTime, endTime, type, channelId, page, pageSize);
        }
        else{
            List<Long> shopChannelIds = shopChannelDao.findByShopId(shopId).stream().map(bo->bo.getId()).collect(Collectors.toList());
            ledgerList = this.ledgerDao.retrieveLedgersInShopChannelAndBetweenSuccessTime(beginTime, endTime, type, shopChannelIds, page, pageSize);
        }

        PageDto<LedgerDto> pageDto = createPageObj(ledgerList, LedgerDto.class);
        logger.debug("retrieveLedge: pageDto = {}", pageDto);

        return pageDto;
    }

    /**
     *
     * 查询支付的所有退款单
     * @author Ming Qiu
     * <p>
     * date: 2022-11-12 21:06
     * @param shopId
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional
    public PageDto<SimpleRefundDto> retrieveRefunds(Long shopId, Long id, Integer page, Integer pageSize) {
        PayTrans payTrans = this.payTransDao.findById(id);

        ShopChannel shopChannel = payTrans.getShopChannel();
        if (PLATFORM != shopChannel.getShopId() && shopId != shopChannel.getShopId()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "退款的支付交易", id, shopId));
        }

        PageInfo<RefundTrans> refundList = this.refundTransDao.retrieveByPayTransId(id, page, pageSize);
        PageDto<SimpleRefundDto> pageDto = createPageObj(refundList, SimpleRefundDto.class);

        return pageDto;
    }

    /**
     * 获得商铺的所有支付渠道(有效和无效)
     *
     * @param shopId 商户id
     * @param page 页码
     * @param pageSize 页大小
     * @return
     */
    @Transactional
    public PageDto<ShopChannelDto> retrieveShopChannel(Long shopId, Integer page, Integer pageSize) {
        PageInfo<ShopChannel> shopChannelList = shopChannelDao.retrieveByShopId(shopId, page, pageSize, true);
        PageDto<ShopChannelDto> pageDto = createPageObj(shopChannelList, ShopChannelDto.class);

        return pageDto;
    }

    /**
     * 签约支付渠道
     * @param shopId 商户id
     * @param subMchid 子商户号
     * @param user 登录用户
     * @return
     */
    @Transactional
    public SimpleShopChannelDto createShopChannel(Long shopId, Long id, String subMchid, UserDto user){
        //商家已存在该支付渠道
        if(shopChannelDao.hasByShopIdAndChannelId(shopId, id)){
            throw new BusinessException(ReturnNo.PAY_CHANNEL_EXIST, String.format(ReturnNo.PAY_CHANNEL_EXIST.getMessage(), shopId, id));
        }
        //再判断平台是否还支持该支付渠道
        Channel channel = channelDao.findById(id);
        if (channel.getStatus().equals(Channel.INVALID)) {
            throw new BusinessException(ReturnNo.PAY_CHANNEL_INVALID, String.format(ReturnNo.PAY_CHANNEL_INVALID.getMessage(), "平台"));
        }

        ShopChannel newShopChannel = new ShopChannel(shopId, id, subMchid);
        ShopChannel bo = shopChannelDao.save(newShopChannel, user);
        SimpleShopChannelDto dto = cloneObj(bo, SimpleShopChannelDto.class);
        return dto;
    }

    /**
     * 查询商铺的某一支付渠道
     * @param shopId 商户id
     * @param id 渠道id
     * @return
     */
    @Transactional
    public FullShopChannelDto findShopChannel(Long shopId, Long id){
        ShopChannel shopChannel = shopChannelDao.findById(id);
        if(!shopId.equals(shopChannel.getShopId())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "支付渠道", id, shopId));
        }
        FullShopChannelDto dto = cloneObj(shopChannel, FullShopChannelDto.class);
        //组装DTO数据
        SimpleChannelDto simpleChannel = cloneObj(shopChannel.getChannel(), SimpleChannelDto.class);
        UserDto creator = new UserDto(){
            {
                setId(shopChannel.getCreatorId());
                setName(shopChannel.getCreatorName());
            }
        };
        UserDto modifier = new UserDto() {
            {
                setId(shopChannel.getModifierId());
                setName(shopChannel.getModifierName());
            }
        };
        dto.setChannel(simpleChannel);
        dto.setCreator(creator);
        dto.setModifier(modifier);
        return dto;
    }

    /**
     *解约店铺的账户
     * @param shopId 商铺id
     * @param id 渠道id
     */
    @Transactional
    public ReturnObject delShopChannel(Long shopId, Long id){
        //先找到shopChannel，判断是否存在和有效
        ShopChannel shopChannel = shopChannelDao.findById(id);
        if(!shopId.equals(shopChannel.getShopId())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "支付渠道", id, shopId));
        }
        //如果该商铺支付渠道的状态是有效的，不允许删除
        if(shopChannel.getStatus().equals(ShopChannel.VALID)){
            throw new BusinessException(ReturnNo.STATENOTALLOW);
        }
        ReturnObject ret = shopChannelDao.delById(shopChannel);
        return ret;
    }
    /**
     * 修改商家支付渠道的subMchid
     * @param shopId 商铺Id
     * @param id 渠道Id
     * @param user 修改用户
     */
    @Transactional
    public ReturnObject updateShopChannelSubMchId(Long shopId, Long id, String SubMchid, UserDto user){
        ShopChannel shopChannel = shopChannelDao.findById(id);
        if(!shopId.equals(shopChannel.getShopId())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "支付渠道", id, shopId));
        }
        //如果支付渠道状态是有效的则不可修改
        if(shopChannel.getStatus().equals(ShopChannel.VALID)){
            throw new BusinessException(ReturnNo.STATENOTALLOW, "支付渠道在有效状态下不可修改");
        }
        ShopChannel bo = new ShopChannel();
        bo.setId(shopChannel.getId());
        bo.setSubMchid(SubMchid);
        putUserFields(bo, "modifier", user);
        putGmtFields(bo, "Modified");
        Set<String> keys = shopChannelDao.saveById(bo);
        keys.forEach(key -> redisUtil.del(key));
        return new ReturnObject(ReturnNo.OK);
    }
    /**
     * 修改商家支付渠道的状态
     *
     * @param shopId 商铺Id
     * @param id     渠道Id
     * @param user   修改用户
     */
    @Transactional
    public ReturnObject updateShopChannelStatus(Long shopId, Long id, Byte valid, UserDto user) {
        ShopChannel shopChannel = shopChannelDao.findById(id);
        //修改的支付渠道不是商铺自己的
        if(!shopId.equals(shopChannel.getShopId())){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "支付渠道", id, shopId));
        }
        //如果是将支付渠道状态改为有效
        if(ShopChannel.VALID.equals(valid)){
            //要判断平台是否支持该渠道
            Channel channel = shopChannel.getChannel();
            if(channel.getStatus().equals(Channel.INVALID)){
                throw new BusinessException(ReturnNo.PAY_CHANNEL_INVALID, String.format(ReturnNo.PAY_CHANNEL_INVALID.getMessage(), "平台"));
            }
        }
        shopChannel.setStatus(valid);
        putUserFields(shopChannel, "modifier", user);
        putGmtFields(shopChannel, "Modified");
        Set<String> keys = shopChannelDao.saveById(shopChannel);
        keys.forEach(key -> redisUtil.del(key));
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 判断台账中对应的交易类型，返回交易类型详细信息
     *
     * @param transType 交易类型
     * @param transId   交易号
     * @param dto       台账详情对象
     * @return void
     * @author zwr
     * <p>
     */
    public void retrieveLedgerTrans(Byte transType, Long transId, FullLedgerDto dto) throws BusinessException {
        logger.debug("retrieveLedgerTrans");
        //退款交易
        if (transType.equals(Ledger.REFUND_TYPE)) {
            RefundTrans trans = this.refundTransDao.findById(transId);
            if (null == trans) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "退款交易号", transId));
            }
            dto.setTrans(cloneObj(trans, SimpleRefundTransDto.class));
            dto.setShopChannel(cloneObj(trans.getShopChannel(), SimpleChannelDto.class));
        }
        //支付交易
        else if (transType.equals(Ledger.PAY_TYPE)) {
            PayTrans trans = payTransDao.findById(transId);
            if (null == trans) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "支付交易号", transId));
            }
            dto.setTrans(cloneObj(trans, SimplePayTransDto.class));
            dto.setShopChannel(cloneObj(trans.getShopChannel(), SimpleChannelDto.class));
        }

        //分账退款交易
        else if (transType.equals(Ledger.DIVREFUND_TYPE)) {
            DivRefundTrans trans = this.divRefundTransDao.findById(transId);
            if (null == trans) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "分账退款交易号", transId));
            }
            dto.setTrans(cloneObj(trans, SimpleDivRefundTransDto.class));
            dto.setShopChannel(cloneObj(trans.getShopChannel(), SimpleChannelDto.class));
        }
        //分账支付交易
        else if (transType.equals(Ledger.DIVPAY_TYPE)) {
            DivPayTrans trans = divPayTransDao.findById(transId);
            if (null == trans) {
                throw new BusinessException(ReturnNo.RESOURCE_ID_NOTEXIST, String.format(ReturnNo.RESOURCE_ID_NOTEXIST.getMessage(), "分账支付交易号", transId));
            }
            dto.setTrans(cloneObj(trans, SimpleDivPayTransDto.class));
        }
    }

    /**
     * 核对商铺号码并返回一个台账对象
     *
     * @param shopId 店铺号
     * @param id     台账号
     * @return Ledger
     * @author zwr
     * <p>
     */
    public Ledger findLedgerByIdAndShopId(Long shopId, Long id) throws BusinessException {
        logger.debug("enter");
        Ledger newObj = new Ledger();
        //找到该Ledger

        newObj = this.ledgerDao.findById(id);
        logger.debug("queryLedge: ledger = {}", newObj.getId());

        ShopChannel shopChannel = this.shopChannelDao.findById(newObj.getShopChannelId());
        Channel channel = shopChannel.getChannel();
        //渠道合法性检查
        if (ShopChannel.INVALID == shopChannel.getStatus() || Channel.INVALID == channel.getStatus()) {
            throw new BusinessException(ReturnNo.PAY_CHANNEL_INVALID, String.format(ReturnNo.PAY_CHANNEL_INVALID.getMessage(), channel.getName()));
        }
        //店铺范围检查
        if (PLATFORM != shopChannel.getShopId() && shopId != shopChannel.getShopId()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "台账", newObj.getId(), shopId));
        }

        newObj.setShopChannel(shopChannel);

        return newObj;
    }

    /**
     * 返回一个台账以及对应交易信息
     *
     * @param shopId 店铺号
     * @param id     台账号
     * @return API_model_definition: FullLedgerDto
     * @author zwr
     * <p>
     */
    public FullLedgerDto retrieveLedger(Long shopId, Long id) throws BusinessException {
        //得到该店铺内的一个台账,合法性检查在该函数内部
        Ledger ledger = findLedgerByIdAndShopId(shopId, id);
        logger.debug("retrieveLedger:ledger={}", ledger.getId());
        FullLedgerDto dto = cloneObj(ledger, FullLedgerDto.class);
        //手动赋值调账人
        SimpleAdminUserDto userDto = new SimpleAdminUserDto();
        logger.debug("getAdjust_id = {}", ledger.getAdjust_id());
        userDto.setId(ledger.getAdjust_id());
        userDto.setUserName(ledger.getAdjust_name());
        dto.setAdjustor(userDto);

        //赋值台账对应的交易对象，合法性检查在该函数内部
        logger.debug("ledgertype = {},transId={}", ledger.getType(), ledger.getTransId());
        retrieveLedgerTrans(ledger.getType(), ledger.getTransId(), dto);
        logger.debug("ChannelId = {}", ledger.getShopChannelId());
        Channel channel = channelDao.findById(ledger.getChannelId());
        dto.setShopChannel(cloneObj(channel, SimpleChannelDto.class));
        dto.setCreator(new SimpleAdminUserDto(ledger.getCreatorId(), ledger.getCreatorName()));
        logger.debug("getFullLedgerDto:ledgerDto={}", dto.toString());

        //返回台账和对应不同类型的交易信息
        return dto;
    }

    /**
     * 调账操作，更新台账和对应交易的处理状态
     *
     * @param shopId 店铺号
     * @param id     台账号
     * @param user   登录用户
     * @return ReturnObject
     * @author zwr
     * <p>
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject adjust(Long shopId, Long id, UserDto user) throws BusinessException {
        if (user == null) {
            throw new BusinessException(ReturnNo.AUTH_NEED_LOGIN, ReturnNo.AUTH_NEED_LOGIN.getMessage());
        }
        Ledger ledger = findLedgerByIdAndShopId(shopId, id);
        //若已进行处理，抛出异常回滚事务
        if (ledger.getStatus() != null && ledger.getStatus() == Ledger.SETTLE) {
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(),"台账",id,"已对账"));
        }
        //未进行调账处理，则更改台账处理状态,更新对应交易的状态
        try {
            ledger.setStatus(Ledger.SETTLE);
            this.ledgerDao.saveById(ledger, user);
            this.transactionDao.saveById(ledger.getTransId(), ledger.getType(), user, ledger.getAdjustTime());
        } catch (BusinessException e) {
            if (ReturnNo.RESOURCE_ID_NOTEXIST == e.getErrno()) {
                logger.error("updateLedgerStatus 调账回写失败。");
                throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR);
            } else {
                throw e;
            }
        }
        return new ReturnObject();
    }

    /**
     * 对账操作：调用接口，若成功则标记非分账交易为 Checked,若失败则非分账标记为 WRONG，
     * 若长款，Ledger类型标记为长款，等待挂账处理
     *
     * @param trans 交易列表
     * @param user  对账用户
     * @return void
     * @author zwr
     * <p>
     */
    public void checkTrans(List<? extends Transaction> trans, UserDto user, Long shopChannelId) throws BusinessException {
        if (trans != null) {
            ShopChannel shopChannel = shopChannelDao.findById(shopChannelId);
            PayAdaptor payAdaptor = factory.createPayAdaptor(shopChannel);
            List<CheckResultDto> results = payAdaptor.checkTransaction(trans);
            try {
                int idx = 0;
                for(CheckResultDto result:results){
                    String cls = result.getCls();
                    //对账成功，记录状态
                    if (result.getStatus() == (byte) 2) {
                        if (cls.equals(PayTrans.class.getName()) || cls.equals(RefundTrans.class.getName())) {
                            transactionDao.saveById(result.getId(), cls, (byte) 2,user);
                        }
                    }
                    //对账失败
                    else {
                        Ledger ledger = cloneObj(trans.get(idx),Ledger.class);
                        ledger.setStatus(Ledger.UNSETTLE);
                        ledger.setOutNo(trans.get(idx).getOutNo());
                        ledger.setTransNo(trans.get(idx).getTransNo());
                        if (cls.equals(PayTrans.class.getName()))
                            ledger.setType(Ledger.PAY_TYPE);
                        else if (cls.equals(RefundTrans.class.getName()))
                            ledger.setType(Ledger.REFUND_TYPE);
                        else if (cls.equals(DivPayTrans.class.getName()))
                            ledger.setType(Ledger.DIVPAY_TYPE);
                        else if (cls.equals(DivRefundTrans.class.getName()))
                            ledger.setType(Ledger.DIVREFUND_TYPE);
                        //错账
                        if (result.getStatus() == (byte) 3) {
                            if (result.getCls().equals(PayTrans.class.getName()) || result.getCls().equals(RefundTrans.class.getName())) {
                                transactionDao.saveById(result.getId(), cls, (byte) 3,user);
                            }
                        }
                        //平台长款
                        if (result.getStatus().equals((byte)4)) {
                            ledger.setType(Ledger.CHANNEL_TYPE);
                        }
                        logger.debug("ledger .cloneObj type=  {}", ledger.getType());
                        ledger.setCheckTime(LocalDateTime.now());
                        ledger.setChannelId(shopChannelDao.findById(ledger.getShopChannelId()).getChannelId());
                        ledgerDao.save(ledger, user);
                        idx += 1;
                    }
                }
            } catch (BusinessException e) {
                if (e.getErrno() == ReturnNo.RESOURCE_ID_NOTEXIST) {
                    logger.debug("checkTrans:对账过程回写失败");
                    throw new BusinessException(ReturnNo.INTERNAL_SERVER_ERR, ReturnNo.INTERNAL_SERVER_ERR.getMessage());
                } else
                    throw e;
            }
        }
    }

    /**
     * 对账
     *
     * @param shopId    店铺号
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return ReturnObject
     * @author zwr
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject Check(Long shopId, LocalDateTime beginTime, LocalDateTime endTime, UserDto user) throws BusinessException {
        //查找该店铺所有渠道
        List<ShopChannel> shopChannels = this.shopChannelDao.findByShopId(shopId);
        //若无返回空
        if (shopChannels == null) {
            return new ReturnObject();
        }
        //获取时间段内该商铺一个渠道内所有交易 (没有采用统一的Transaction是为了区分未分帐交易需要在对账时的状态更新）
        shopChannels.stream().map(s -> s.getId())
                .forEach(id -> {
            List<PayTrans> pays = this.payTransDao.retrieveBetweenSuccessTimeAndShopChannelIdEqualsIdAndStatusEqualsSUCCESS(beginTime, endTime, id);
            List<RefundTrans> refunds = this.refundTransDao.retrieveBetweenSuccessTimeAndShopChannelIdEqualsIdAndStatusEqualsSUCCESS(beginTime, endTime, id);
            List<DivPayTrans> divPays = this.divPayTransDao.retrieveBetweenSuccessTimeAndShopChannelIdEqualsId(beginTime, endTime, id);
            List<DivRefundTrans> divRefunds = this.divRefundTransDao.retrieveBetweenSuccessTimeAndShopChannelIdEqualsId(beginTime, endTime, id);
            List<Transaction> trans = new ArrayList<>();
            trans.addAll(pays);
            trans.addAll(refunds);
            trans.addAll(divPays);
            trans.addAll(divRefunds);
            //对每一笔交易进行对账操作，若错账，则会记录到Ledger表中，另外对于支付和退款将会修改交易状态,在check函数里判断了回写失败异常
            checkTrans(trans, user, id);
        });

        return new ReturnObject();
    }

    @Transactional
    public PageDto<DivPayTransDto> retrieveDivPayTrans(Long shopId, LocalDateTime beginTime, LocalDateTime endTime, Long channelId, Integer page, Integer pageSize) {
        ShopChannel shopChannel = null;
        try {
            shopChannel = this.shopChannelDao.findByShopIdAndChannelId(shopId, channelId);
        }catch (BusinessException e) {
            if (e.getErrno() == ReturnNo.RESOURCE_ID_NOTEXIST) {
                return new PageDto<>(new ArrayList<>(), 0, 0);
            }else {
                throw e;
            }
        }
        PageInfo<DivPayTrans> divPayTransPageInfo = this.divPayTransDao.retrieveByShopChannelIdAndSuccessTimeBetween(shopChannel.getId(), beginTime, endTime, page, pageSize);
        PageDto<DivPayTransDto> pageDto = createPageObj(divPayTransPageInfo, DivPayTransDto.class);
        return pageDto;
    }

    @Transactional
    public PageDto<FullPayTransDto> retrievePayments(Long shopId, Long shopChannelId, String transNo, LocalDateTime beginTime, LocalDateTime endTime, Long adjustId, Integer page, Integer pageSize){
        ShopChannel shopChannel = null;
        try {
            shopChannel = this.shopChannelDao.findById(shopChannelId);
        }catch (BusinessException e) {
            if (e.getErrno() == ReturnNo.RESOURCE_ID_NOTEXIST) {
                return new PageDto<>(new ArrayList<>(), 0, 0);
            }else {
                throw e;
            }
        }
        if (shopChannel.getShopId() != shopId) {
            throw new BusinessException(ReturnNo.FIELD_NOTVALID);
        }
        PageInfo<PayTrans> payTransPageInfo = this.payTransDao.retrieveByShopChannelId(shopChannel.getId(), transNo, adjustId, beginTime, endTime, page, pageSize);
        PageDto<FullPayTransDto> pageDto = createPageObj(payTransPageInfo, FullPayTransDto.class);
        return pageDto;
    }

    @Transactional
    public ReturnObject updatePaymentByNotify(PayNotifyDto payNotifyDto, UserDto user){
        if (PayTrans.NEW.equals(payNotifyDto.getStatus())) {
            return new ReturnObject(ReturnNo.OK);
        }

        PayTrans payTrans = this.payTransDao.findByOutNo(payNotifyDto.getOutTradeNo());

        clearFields(payTrans, "id");
        payTrans.setStatus(payNotifyDto.getStatus());
        payTrans.setAmount(payNotifyDto.getAmount());
        payTrans.setSuccessTime(payNotifyDto.getSuccessTime());

        this.payTransDao.saveById(payTrans, user);

        return new ReturnObject(ReturnNo.OK);
    }

    @Transactional
    public ReturnObject retrievePaymentStates(){
        List<StatusDto> statusDtos = PayTrans.STATUSNAMES.keySet().stream().map(key -> new StatusDto(key, PayTrans.STATUSNAMES.get(key))).collect(Collectors.toList());
        return new ReturnObject(statusDtos);
    }

    @Transactional
    public ReturnObject divPayment(LocalDateTime timeBegin, LocalDateTime timeEnd, UserDto user){
        List<PayTrans> payTransList =payTransDao.retrieveBySuccessTimeBetween(timeBegin, timeEnd).stream().filter(
                payTrans -> payTrans.getStatus().equals(CHECKED)
        ).collect(Collectors.toList());

        List<PostDivPayAdaptorDto> collect=payTransList.stream().map(
                pay->{
                    DivPayTrans divTrans = pay.getDivTrans();
                    divTrans.setShopChannelDao(this.shopChannelDao);
                    PayAdaptor payAdaptor = this.factory.createPayAdaptor(divTrans.getShopChannel());
                    return payAdaptor.createDivPay(divTrans);
                }
        ).filter(Objects::nonNull).collect(Collectors.toList());
        return new ReturnObject(collect.size());
    }

    @Transactional
    public FullPayTransDto findPayment(Long shopId, Long id){
        PayTrans payTrans = this.payTransDao.findById(id);
        ShopChannel shopChannel = payTrans.getShopChannel();
        if (shopId != shopChannel.getShopId()) {
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "支付交易", id, shopId));
        }
        FullPayTransDto fullPayTransDto = cloneObj(payTrans, FullPayTransDto.class);
        return fullPayTransDto;
    }

    @Transactional
    public void cancelPayment(Long shopId, Long id, UserDto user){
        PayTrans payTrans = this.payTransDao.findById(id);
        ShopChannel shopChannel = payTrans.getShopChannel();
        if(shopId!=shopChannel.getShopId()){
            throw new BusinessException(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "支付交易", id, shopId));
        }
        if (!payTrans.allowStatus(CANCEL)){
            throw new BusinessException(ReturnNo.STATENOTALLOW, String.format(ReturnNo.STATENOTALLOW.getMessage(), "支付交易",id,payTrans.getStatusName()));
        }
        PayAdaptor payAdaptor = this.factory.createPayAdaptor(payTrans.getShopChannel());
        payAdaptor.cancelOrder(payTrans);
        payTrans.setStatus(CANCEL);
        this.payTransDao.saveById(payTrans,user);
    }
}
