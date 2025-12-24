package com.railbooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 车站实体类
 */
@Data
@TableName("stations")
public class Station {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 车站名称
     */
    private String name;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 车站简称
     */
    private String abbreviation;

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
