package com.codewithiyke.dreamshops.service.cart;

import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Cart;
import com.codewithiyke.dreamshops.model.User;
import com.codewithiyke.dreamshops.repository.CartItemRepository;
import com.codewithiyke.dreamshops.repository.CartRepository;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;

  @Override
  @Transactional(readOnly = true)
  public Cart getCart(Long id) {
    return cartRepository
        .findWithItemsById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
  }

  @Transactional
  @Override
  public void clearCart(Long id) {
    Cart cart = getCart(id);
    cartItemRepository.deleteAllByCartId(id);
    cart.getItems().clear();
    //    cartRepository.deleteById(id);
    cart.setTotalAmount(BigDecimal.ZERO);
    cartRepository.save(cart);
  }

  @Override
  public BigDecimal getTotalPrice(Long id) {
    Cart cart = getCart(id);
    return cart.getTotalAmount();
  }

  @Override
  public Cart initializeNewCart(User user) {
    return Optional.ofNullable(getCartByUserId(user.getId()))
        .orElseGet(
            () -> {
              Cart cart = new Cart();
              cart.setUser(user);
              return cartRepository.save(cart);
            });
  }

  @Override
  public Cart getCartByUserId(Long userId) {
    return cartRepository.findByUserId(userId);
  }
}
