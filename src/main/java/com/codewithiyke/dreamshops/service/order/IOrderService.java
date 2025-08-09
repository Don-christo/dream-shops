package com.codewithiyke.dreamshops.service.order;

import com.codewithiyke.dreamshops.dto.OrderDto;
import com.codewithiyke.dreamshops.model.Order;
import java.util.List;

public interface IOrderService {
  Order placeOrder(Long userId);

  OrderDto getOrder(Long orderId);

  List<OrderDto> getUserOrders(Long userId);

  OrderDto convertToDto(Order order);
}
