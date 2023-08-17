package cn.edu.xmu.oomall.region.dao.bo;


import cn.edu.xmu.javaee.core.model.bo.OOMallObject;
import cn.edu.xmu.oomall.region.dao.RegionDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Region extends OOMallObject implements Serializable {
    @ToString.Exclude
    @JsonIgnore
    private final static Logger logger = LoggerFactory.getLogger(Region.class);

    /**
     * 两种特殊id
     * 0 -- 最高级地区
     * -1 -- 不存在
     */
    @ToString.Exclude
    @JsonIgnore
    public static final Long TOP_ID = 0L;
    @ToString.Exclude
    @JsonIgnore
    public static final Long INVALID_ID = -1L;

    /**
     * 共三种状态
     */
    //有效
    @ToString.Exclude
    @JsonIgnore
    public static final Byte VALID = 0;
    //停用
    @ToString.Exclude
    @JsonIgnore
    public static final Byte SUSPENDED = 1;
    //废弃
    @ToString.Exclude
    @JsonIgnore
    public static final Byte ABANDONED = 2;

    @ToString.Exclude
    @JsonIgnore
    public static final Map<Byte, String> STATUSNAMES = new HashMap<>() {
        {
            put(VALID, "有效");
            put(SUSPENDED, "暂停");
            put(ABANDONED, "废弃");
        }
    };

    /**
     * 允许的状态迁移
     */
    @JsonIgnore
    @ToString.Exclude
    private static final Map<Byte, Set<Byte>> toStatus = new HashMap<>() {
        {
            put(VALID, new HashSet<>() {
                {
                    add(SUSPENDED);
                    add(ABANDONED);
                }
            });
            put(SUSPENDED, new HashSet<>() {
                {
                    add(VALID);
                    add(ABANDONED);
                }
            });
        }
    };

    /**
     * 是否允许状态迁移
     */
    public boolean allowStatus(Byte status) {
        boolean ret = false;

        if (null != status && null != this.status) {
            Set<Byte> allowStatusSet = toStatus.get(this.status);
            if (null != allowStatusSet) {
                ret = allowStatusSet.contains(status);
            }
        }
        return ret;
    }

    /**
     * 获得当前状态名称
     */
    @JsonIgnore
    public String getStatusName() {
        return STATUSNAMES.get(this.status);
    }

    @Getter
    @Setter
    private Long pid;
    @Setter
    private Byte level;
    @Getter
    @Setter
    private String areaCode;
    @Getter
    @Setter
    private String zipCode;
    @Getter
    @Setter
    private String cityCode;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String shortName;
    @Getter
    @Setter
    private String mergerName;
    @Getter
    @Setter
    private String pinyin;
    @Getter
    @Setter
    private Double lng;
    @Getter
    @Setter
    private Double lat;
    @Getter
    @Setter
    private Byte status;
    @JsonIgnore
    @ToString.Exclude
    private Region parentRegion;
    @Setter
    @JsonIgnore
    @ToString.Exclude
    private RegionDao regionDao;

    public Region getParentRegion() {
        logger.debug("getParentRegion: pid = {}", this.pid);
        if (!INVALID_ID.equals(this.pid) && null == this.parentRegion && null != this.regionDao) {
            this.parentRegion = this.regionDao.findById(pid);
        }
        return this.parentRegion;
    }

    public Byte getLevel() {
        if (null == this.level) {
            if (TOP_ID.equals(this.pid) || INVALID_ID.equals(this.pid)) {
                this.level = 0;
            } else if (null != this.getParentRegion()) {
                this.level = (byte) (this.getParentRegion().getLevel() + 1);
            }
        }
        return this.level;
    }

    public Region(String name, String shortName, String mergerName, String pinyin, Double lng,
                  Double lat, String areaCode, String zipCode, String cityCode, Byte status) {
        this.name = name;
        this.shortName = shortName;
        this.mergerName = mergerName;
        this.pinyin = pinyin;
        this.lng = lng;
        this.lat = lat;
        this.areaCode = areaCode;
        this.zipCode = zipCode;
        this.cityCode = cityCode;
        this.status = status;
    }
}
