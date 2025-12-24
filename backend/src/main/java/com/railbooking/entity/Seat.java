package com.railbooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 座位实体类
 */
@Data
@TableName("seats")
public class Seat {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 车次ID
     */
    private Long trainId;

    /**
     * 车次编号
     */
    private String trainCode;

    /**
     * 车厢号
     */
    private Integer carriageNo;

    /**
     * 座位号，如 1A、2B
     */
    private String seatNo;

    /**
     * 座位类型：FIRST_CLASS-一等座，SECOND_CLASS-二等座，HARD_SLEEPER-硬卧，SOFT_SLEEPER-软卧
     */
    private String seatType;

    /**
     * 座位状态：0-空闲，1-已锁定（预订中），2-已售出
     */
    private Integer status;

    /**
     * 所属日期
     */
    private LocalDate travelDate;

    /**
     * 价格
     */
    private BigDecimal price;

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
}
