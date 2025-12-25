package com.railbooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

/**
 * 班次实体类（汽车班次）
 */
@Data
@TableName("trains")
public class Train {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 班次编号，如 BJ001、GZ002
     */
    private String trainCode;

    /**
     * 车型：EXPRESS-快客，NORMAL-普通班车，SLEEPER-卧铺
     */
    private String trainType;

    /**
     * 出发站ID
     */
    private Long departureStationId;

    /**
     * 出发站名称
     */
    private String departureStationName;

    /**
     * 到达站ID
     */
    private Long arrivalStationId;

    /**
     * 到达站名称
     */
    private String arrivalStationName;

    /**
     * 发车时间
     */
    private LocalTime departureTime;

    /**
     * 到达时间
     */
    private LocalTime arrivalTime;

    /**
     * 运行天数：0-当日到达，1-次日到达
     */
    private Integer runDays;

    /**
     * 运行时长（分钟）
     */
    private Integer duration;

    /**
     * 商务座价格
     */
    private BigDecimal firstClassPrice;

    /**
     * 普通座价格
     */
    private BigDecimal secondClassPrice;

    /**
     * 下铺价格
     */
    private BigDecimal hardSleeperPrice;

    /**
     * 上铺价格
     */
    private BigDecimal softSleeperPrice;

    /**
     * 座位总数
     */
    private Integer totalSeats;

    /**
     * 运营日期范围-开始
     */
    private LocalDate operateDateStart;

    /**
     * 运营日期范围-结束
     */
    private LocalDate operateDateEnd;

    /**
     * 客运公司
     */
    private String busCompany;

    /**
     * 车辆型号
     */
    private String busModel;

    /**
     * 状态：0-停运，1-运营中
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 额外信息（余票等）- 不存储到数据库
     */
    @TableField(exist = false)
    private Map<String, Object> extra;
}
