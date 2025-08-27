package com.codewithiyke.dreamshops.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;

@Data
public class OrderDto {
  private Long orderId;
  private Long userId;
  private LocalDateTime orderDate;
  private BigDecimal totalAmount;
  private String orderStatus;
  private Set<OrderItemDto> orderItems;
}
