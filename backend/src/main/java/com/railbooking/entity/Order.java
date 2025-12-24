package com.railbooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号（唯一）
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 车次ID
     */
    private Long trainId;

    /**
     * 车次编号
     */
    private String trainCode;

    /**
     * 出发站
     */
    private String departureStation;

    /**
     * 到达站
     */
    private String arrivalStation;

    /**
     * 出发时间
     */
    private String departureTime;

    /**
     * 到达时间
     */
    private String arrivalTime;

    /**
     * 乘车日期
     */
    private String travelDate;

    /**
     * 乘客姓名
     */
    private String passengerName;

    /**
     * 乘客身份证号
     */
    private String passengerIdCard;

    /**
     * 座位类型
     */
    private String seatType;

    /**
     * 车厢号
     */
    private Integer carriageNo;

    /**
     * 座位号
     */
    private String seatNo;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已退票
     */
    private Integer status;

    /**
     * 支付截止时间
     */
    private LocalDateTime payDeadline;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

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
     * 备注
     */
    private String remark;
}
