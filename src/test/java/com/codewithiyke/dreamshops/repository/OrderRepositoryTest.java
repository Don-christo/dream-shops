package com.codewithiyke.dreamshops.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codewithiyke.dreamshops.model.Order;
import com.codewithiyke.dreamshops.model.OrderItem;
import com.codewithiyke.dreamshops.model.Product;
import com.codewithiyke.dreamshops.model.User;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Transactional
class OrderRepositoryTest {

  @Autowired private OrderRepository orderRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ProductRepository productRepository;

  private User user;
  private Order order;

  @BeforeEach
  void setUp() {
    // Create a test user
    user = new User();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("john@example.com");
    user.setPassword("password123");
    userRepository.save(user);

    // Create a test product
    Product product = new Product();
    product.setName("Laptop");
    product.setDescription("High-end laptop");
    product.setPrice(BigDecimal.valueOf(1500));
    productRepository.save(product);

    // Create an order
    order = new Order();
    order.setUser(user);
    order.setTotalAmount(BigDecimal.valueOf(1500));

    // Create an order item
    OrderItem orderItem = new OrderItem();
    orderItem.setOrder(order);
    orderItem.setProduct(product);
    orderItem.setQuantity(1);
    orderItem.setPrice(BigDecimal.valueOf(1500));

    // Add item to order and save
    order.getOrderItems().add(orderItem);
    order = orderRepository.save(order); // save after adding items

    // Create another order
    Order secondOrder = new Order();
    secondOrder.setUser(user);
    secondOrder.setTotalAmount(BigDecimal.valueOf(200.00));
    orderRepository.save(secondOrder);
  }

  @Test
  void testFindByUserId_ShouldReturnOrdersForUser() {
    List<Order> orders = orderRepository.findByUserId(user.getId());

    assertThat(orders).hasSize(2);
    assertThat(orders)
        .extracting(Order::getTotalAmount)
        .containsExactlyInAnyOrder(BigDecimal.valueOf(1500), BigDecimal.valueOf(200.00));
  }

  @Test
  void testFindByUserId_WhenNoOrders_ShouldReturnEmptyList() {
    List<Order> orders = orderRepository.findByUserId(999L);

    assertThat(orders).isEmpty();
  }

  @Test
  void testFindWithItemsByOrderId_ShouldReturnOrderWithItemsAndProduct() {
    Optional<Order> fetchedOrderOpt = orderRepository.findWithItemsByOrderId(order.getOrderId());

    assertThat(fetchedOrderOpt).isPresent();
    Order fetchedOrder = fetchedOrderOpt.get();

    assertThat(fetchedOrder.getOrderItems()).isNotEmpty();
    assertThat(fetchedOrder.getOrderItems().iterator().next().getProduct().getName())
        .isEqualTo("Laptop");
    assertThat(fetchedOrder.getUser().getEmail()).isEqualTo("john@example.com");
  }

  @Test
  void testFindWithItemsByOrderId_WhenOrderDoesNotExist_ShouldReturnEmpty() {
    Optional<Order> fetchedOrderOpt = orderRepository.findWithItemsByOrderId(999L);

    assertThat(fetchedOrderOpt).isEmpty();
  }
}
