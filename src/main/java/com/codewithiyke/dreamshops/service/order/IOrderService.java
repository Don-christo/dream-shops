package com.codewithiyke.dreamshops.service.order;

import com.codewithiyke.dreamshops.model.Order;

public interface IOrderService {
  Order placeOrder(Long userId);

  Order getOrder(Long orderId);
}
