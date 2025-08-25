package com.codewithiyke.dreamshops.service.order;

import com.codewithiyke.dreamshops.dto.OrderDto;
import com.codewithiyke.dreamshops.enums.OrderStatus;
import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.*;
import com.codewithiyke.dreamshops.repository.OrderRepository;
import com.codewithiyke.dreamshops.repository.ProductRepository;
import com.codewithiyke.dreamshops.service.cart.CartService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final CartService cartService;
  private final ModelMapper modelMapper;

  @Override
  @Transactional
  public Order placeOrder(Long userId) {
    Cart cart = cartService.getCartByUserId(userId);

    if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
      throw new IllegalStateException("Cart is empty.");
    }

    // Validate inventory first (no side effects)
    for (CartItem item : cart.getItems()) {
      Product p = item.getProduct();
      if (p.getInventory() < item.getQuantity()) {
        throw new IllegalStateException("Insufficient inventory for product: " + p.getName());
      }
    }

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
  public OrderDto getOrder(Long orderId) {
    return orderRepository
        .findById(orderId)
        .map(this::convertToDto)
        .orElseThrow(() -> new ResourceNotFoundException("No order found"));
  }

  @Override
  public List<OrderDto> getUserOrders(Long userId) {
    List<Order> orders = orderRepository.findByUserId(userId);
    return orders.stream().map(this::convertToDto).toList();
  }

  @Transactional
  @Override
  public Order cancelOrder(Long orderId) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

    if (order.getOrderStatus() != OrderStatus.PENDING) {
      throw new IllegalStateException("Order cannot be cancelled as it is already processed.");
    }

    // Restore inventory
    if (order.getOrderItems() != null) {
      for (OrderItem item : order.getOrderItems()) {
        Product product = item.getProduct();
        int restoredInventory = product.getInventory() + item.getQuantity();
        product.setInventory(restoredInventory);
        productRepository.save(product);
      }
    }

    // Update order status
    order.setOrderStatus(OrderStatus.CANCELLED);
    return orderRepository.save(order);
  }

  @Override
  public OrderDto convertToDto(Order order) {
    return modelMapper.map(order, OrderDto.class);
  }
}
