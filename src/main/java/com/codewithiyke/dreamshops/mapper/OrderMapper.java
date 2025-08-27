package com.codewithiyke.dreamshops.mapper;

import com.codewithiyke.dreamshops.dto.OrderDto;
import com.codewithiyke.dreamshops.dto.OrderItemDto;
import com.codewithiyke.dreamshops.model.Order;
import com.codewithiyke.dreamshops.model.OrderItem;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getId());
        dto.setOrderDate(order.getOrderDate() != null ? order.getOrderDate().atStartOfDay() : null);
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderStatus(order.getOrderStatus().name());

        // Convert orderItems
        Set<OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                .map(OrderMapper::toOrderItemDto)
                .collect(Collectors.toSet());
        dto.setOrderItems(orderItemDtos);

        return dto;
    }

    private static OrderItemDto toOrderItemDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setProductBrand(item.getProduct().getBrand());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }
}
