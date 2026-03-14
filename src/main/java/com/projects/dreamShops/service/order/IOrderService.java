package com.projects.dreamShops.service.order;

import com.projects.dreamShops.dto.OrderDto;
import com.projects.dreamShops.model.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto convertToDto(Order order);
}
