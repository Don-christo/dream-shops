package com.codewithiyke.dreamshops.service.cart;

import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Cart;
import com.codewithiyke.dreamshops.model.CartItem;
import com.codewithiyke.dreamshops.model.Product;
import com.codewithiyke.dreamshops.repository.CartItemRepository;
import com.codewithiyke.dreamshops.repository.CartRepository;
import com.codewithiyke.dreamshops.service.product.IProductService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {
  private final CartItemRepository cartItemRepository;
  private final IProductService productService;
  private final ICartService cartService;
  private final CartRepository cartRepository;

  @Override
  public void addItemToCart(Long cartId, Long productId, int quantity) {
    //      1. Get the cart
    //      2. Get the product
    //      3. Check if the product is already in the cart
    //      4. If yes,then increase the quantity with the requested quantity
    //      5. If no, then initiate a new CartItem entry.
    Cart cart = cartService.getCart(cartId);
    Product product = productService.getProductById(productId);
    CartItem cartItem =
        cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .orElse(new CartItem());
    if (cartItem.getId() == null) {
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(quantity);
      cartItem.setUnitPrice(product.getPrice());
    } else {
      cartItem.setQuantity(cartItem.getQuantity() + quantity);
    }
    cartItem.setTotalPrice();
    cart.addItem(cartItem);

    cartItemRepository.save(cartItem);
    cartRepository.save(cart);
  }

  @Override
  @Transactional
  public void removeItemFromCart(Long cartId, Long productId) {
    Cart cart = cartService.getCart(cartId);
    CartItem itemToRemove = getCartItem(cartId, productId);
    cart.removeItem(itemToRemove);
    cartRepository.save(cart);
  }

  @Override
  public void updateQuantity(Long cartId, Long productId, int quantity) {
    Cart cart = cartService.getCart(cartId);
    cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst()
        .ifPresent(
            item -> {
              item.setQuantity(quantity);
              item.setUnitPrice(item.getProduct().getPrice());
              item.setTotalPrice();
            });
    BigDecimal totalAmount =
        cart.getItems().stream()
            .map(CartItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    cart.setTotalAmount(totalAmount);
    cartRepository.save(cart);
  }

  @Override
  public CartItem getCartItem(Long cartId, Long productId) {
    Cart cart = cartService.getCart(cartId);
    return cart.getItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
  }
}
