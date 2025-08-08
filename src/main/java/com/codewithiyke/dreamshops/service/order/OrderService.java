package com.codewithiyke.dreamshops.service.order;

import com.codewithiyke.dreamshops.enums.OrderStatus;
import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Cart;
import com.codewithiyke.dreamshops.model.Order;
import com.codewithiyke.dreamshops.model.OrderItem;
import com.codewithiyke.dreamshops.model.Product;
import com.codewithiyke.dreamshops.repository.OrderRepository;
import com.codewithiyke.dreamshops.repository.ProductRepository;
import com.codewithiyke.dreamshops.service.cart.CartService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final CartService cartService;

  @Override
  public Order placeOrder(Long userId) {
    Cart cart = cartService.getCartByUserId(userId);

    Order order = createOrder(cart);
    List<OrderItem> orderItemList = createOrderItems(order, cart);

    order.setOrderItems(new HashSet<>(orderItemList));
    order.setTotalAmount(calculateTotalAmount(orderItemList));
    Order savedOrder = orderRepository.save(order);

    cartService.clearCart(cart.getId());
    return savedOrder;
  }

  private Order createOrder(Cart cart) {
    Order order = new Order();
    order.setUser(cart.getUser());
    order.setOrderStatus(OrderStatus.PENDING);
    order.setOrderDate(LocalDate.now());
    return order;
  }

  private List<OrderItem> createOrderItems(Order order, Cart cart) {
    return cart.getItems().stream()
        .map(
            cartItem -> {
              Product product = cartItem.getProduct();
              product.setInventory(product.getInventory() - cartItem.getQuantity());
              productRepository.save(product);
              return new OrderItem(order, product, cartItem.getQuantity(), cartItem.getUnitPrice());
            })
        .toList();
  }

  private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList) {
    return orderItemList.stream()
        .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public Order getOrder(Long orderId) {
    return orderRepository
        .findById(orderId)
        .orElseThrow(() -> new ResourceNotFoundException("No order found"));
  }

  @Override
  public List<Order> getUserOrders(Long userId) {
    return orderRepository.findByUserId(userId);
  }
}
