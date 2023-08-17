//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.shop.dao.bo;

import cn.edu.xmu.javaee.core.model.InternalReturnObject;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.shop.dao.ShopDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.FreightDao;
import cn.edu.xmu.oomall.shop.dao.openfeign.bo.Region;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 商铺对象
 */
@ToString(callSuper = true)
@NoArgsConstructor
public class Shop extends OOMallObject implements Serializable {

    /**
     * 申请
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte NEW = 0;
    /**
     * 下线
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte OFFLINE = 1;
    /**
     * 上线
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte ONLINE = 2;
    /**
     * 停用
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte ABANDON = 3;

    /**
     * 状态和名称的对应
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Map<Byte, String> STATUSNAMES = new HashMap(){
        {
            put(NEW, "申请");
            put(OFFLINE, "下线");
            put(ONLINE, "上线");
            put(ABANDON, "停用");
        }
    };

    /**
     * 允许的状态迁移
     */
    @JsonIgnore
    @ToString.Exclude
    private static final Map<Byte, Set<Byte>> toStatus = new HashMap<>(){
        {
            put(OFFLINE, new HashSet<>(){
                {
                    add(ONLINE);
                    add(ABANDON);
                }
            });
            put(ONLINE, new HashSet<>(){
                {
                    add(OFFLINE);
                }
            });
        }
    };

    /**
     * 是否允许状态迁移
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:25
     * @param status
     * @return
     */
    public boolean allowStatus(Byte status){
        boolean ret = false;

        if (null != status && null != this.status){
            Set<Byte> allowStatusSet = toStatus.get(this.status);
            if (null != allowStatusSet) {
                ret = allowStatusSet.contains(status);
            }
        }
        return ret;
    }

    /**
     * 获得当前状态名称
     * @author Ming Qiu
     * <p>
     * date: 2022-11-13 0:43
     * @return
     */
    @JsonIgnore
    public String getStatusName(){
        return STATUSNAMES.get(this.status);
    }

    /**
     * 服务商
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte SERVICE = 1;
    /**
     * 电商
     */
    @JsonIgnore
    @ToString.Exclude
    public static final Byte RETAILER = 0;

    /**
     * 商铺名称
     */
    @Setter
    @Getter
    private String name;

    /**
     * 商铺保证金
     */
    @Setter
    @Getter
    private Long deposit;

    /**
     * 商铺保证金门槛
     */
    @Setter
    @Getter
    private Long depositThreshold;

    /**
     * 状态
     */
    @Setter
    @Getter
    private Byte status;

    /**
     * 详细地址
     */
    @Setter
    @Getter
    private String address;

    /**
     * 联系人
     */
    @Setter
    @Getter
    private String consignee;

    /**
     * 电话
     */
    @Setter
    @Getter
    private String mobile;

    /**
     * 免邮门槛
     */
    @Setter
    @Getter
    private Integer freeThreshold;

    /**
     * 类型 0：电商 1：服务商
     */
    @Setter
    @Getter
    private Byte type;

    @Setter
    @JsonIgnore
    @ToString.Exclude
    private ShopDao shopDao;

    /**
     * 地区
     */
    @Setter
    @Getter
    private Long regionId;


    @ToString.Exclude
    @JsonIgnore
    private Region region;

    @Setter
    @JsonIgnore
    @ToString.Exclude
    private FreightDao freightDao;


    public Region getRegion(){
        if (null != this.regionId && null == this.region) {
            InternalReturnObject<Region> ret=this.freightDao.findRegionById(this.regionId);
            if (ReturnNo.OK == ReturnNo.getByCode(ret.getErrno())){
                this.region = ret.getData();
            }
        }
        return this.region;
    }

    @Builder
    public Shop(Long id, Long creatorId, String creatorName, Long modifierId, String modifierName, LocalDateTime gmtCreate, LocalDateTime gmtModified, String name, Long deposit, Long depositThreshold, Byte status, String address, String consignee, String mobile, Byte type, Long regionId, Integer freeThreshold) {
        super(id, creatorId, creatorName, modifierId, modifierName, gmtCreate, gmtModified);
        this.name = name;
        this.deposit = deposit;
        this.depositThreshold = depositThreshold;
        this.status = status;
        this.address = address;
        this.consignee = consignee;
        this.mobile = mobile;
        this.type = type;
        this.regionId = regionId;
        this.freeThreshold = freeThreshold;
    }
}
