package com.codewithiyke.dreamshops.service.order;

import com.codewithiyke.dreamshops.model.Order;

import java.util.List;

public interface IOrderService {
  Order placeOrder(Long userId);

  Order getOrder(Long orderId);

  List<Order> getUserOrders(Long userId);
}
