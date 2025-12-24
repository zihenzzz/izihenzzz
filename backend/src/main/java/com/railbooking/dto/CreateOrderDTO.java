package com.railbooking.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderDTO {
    private Long trainId;
    private String seatType;
    private List<PassengerDTO> passengers;
    private String travelDate;
}

@Data
public class PassengerDTO {
    private String name;
    private String idCard;
}
