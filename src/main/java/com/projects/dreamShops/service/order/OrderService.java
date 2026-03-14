package com.projects.dreamShops.service.order;

import com.projects.dreamShops.dto.OrderDto;
import com.projects.dreamShops.dto.OrderItemDto;
import com.projects.dreamShops.enums.OrderStatus;
import com.projects.dreamShops.exceptions.ResourceNotFoundException;
import com.projects.dreamShops.model.*;
import com.projects.dreamShops.repository.OrderRepository;
import com.projects.dreamShops.repository.ProductRepository;
import com.projects.dreamShops.service.cart.ICartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ICartService cartService;
    private final ModelMapper modelMapper;

    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);

        Order order = createOrder(cart);
        List<OrderItem> orderItemList = createOrderItem(order,cart);
        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalPrice(calculateTotalAmount(orderItemList));
        Order savedOrder = orderRepository.save(order);
        cartService.deleteCart(cart.getId());

        return savedOrder;
    }

    @Override
    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToDto)
                .orElseThrow(()->new ResourceNotFoundException("Order not found"));
    }

    private Order createOrder(Cart cart){
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;
    }

    private List<OrderItem> createOrderItem(Order order, Cart cart){
        return cart.getItems().stream()
                .map(cartItem ->{
                    Product product = cartItem.getProduct();
                    product.setInventory(product.getInventory() - cartItem.getQuantity());
                    productRepository.save(product);
                    return new OrderItem(
                            order,
                            product,
                            cartItem.getQuantity(),
                            cartItem.getUnitPrice());
                }).toList();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList){
        return orderItemList.stream()
                .map(item-> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId){
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::convertToDto).toList();
    }

//    @Override
//    public OrderDto convertToDto(Order order){
//        return modelMapper.map(order,OrderDto.class);
//    }

    @Override
    public OrderDto convertToDto(Order order) {
        OrderDto dto = modelMapper.map(order, OrderDto.class);

        // map userId
        dto.setUserId(order.getUser().getId());



        // map items
        dto.setItems(order.getOrderItems().stream()
                .map(item -> {
                   OrderItemDto itemDto =  modelMapper.map(item, OrderItemDto.class);
                   itemDto.setBrand(item.getProduct().getBrand());
                   return itemDto;
                }).collect(Collectors.toList()));

        return dto;
    }

}
