package com.codewithiyke.dreamshops.service.cart;

import com.codewithiyke.dreamshops.model.Cart;
import java.math.BigDecimal;

public interface ICartService {
  Cart getCart(Long id);

  void clearCart(Long id);

  BigDecimal getTotalPrice(Long id);

  Long initializeNewCart();
}
