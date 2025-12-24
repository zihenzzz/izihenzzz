package com.railbooking.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应DTO
 */
@Data
public class OrderDTO {
    private Long id;
    private String orderNo;
    private String trainCode;
    private String departureStation;
    private String arrivalStation;
    private String departureTime;
    private String arrivalTime;
    private String travelDate;
    private String passengerName;
    private String seatType;
    private String carriageNo;
    private String seatNo;
    private BigDecimal amount;
    private Integer status;
    private LocalDateTime payDeadline;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private String statusText;
}
