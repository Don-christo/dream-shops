package com.codewithiyke.dreamshops.service.cart;

import com.codewithiyke.dreamshops.model.CartItem;

public interface ICartItemService {
  void addItemToCart(Long cartId, Long productId, int quantity);

  void removeItemFromCart(Long cartId, Long productId);

  void updateQuantity(Long cartId, Long productId, int quantity);

  CartItem getCartItem(Long cartId, Long productId);
}
