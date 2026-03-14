package com.projects.dreamShops.dto;

import com.projects.dreamShops.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDto {
    private Long orderId;
    private Long userId;
    private LocalDate orderDate;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;
    private List<OrderItemDto> items;
}
