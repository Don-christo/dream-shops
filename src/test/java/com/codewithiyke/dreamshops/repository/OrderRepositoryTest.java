package com.codewithiyke.dreamshops.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codewithiyke.dreamshops.model.Order;
import com.codewithiyke.dreamshops.model.User;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Transactional
class OrderRepositoryTest {

  @Autowired private OrderRepository orderRepository;

  @Autowired private UserRepository userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    // Create a test user
    user = new User();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("john@example.com");
    user.setPassword("password123");
    userRepository.save(user);

    // Create two orders for the user
    Order order1 = new Order();
    order1.setUser(user);
    order1.setTotalAmount(BigDecimal.valueOf(100.50));
    orderRepository.save(order1);

    Order order2 = new Order();
    order2.setUser(user);
    order2.setTotalAmount(BigDecimal.valueOf(200.00));
    orderRepository.save(order2);
  }

  @Test
  void testFindByUserId_ShouldReturnOrdersForUser() {
    List<Order> orders = orderRepository.findByUserId(user.getId());

    assertThat(orders).hasSize(2);
    assertThat(orders)
        .extracting(Order::getTotalAmount)
        .containsExactlyInAnyOrder(BigDecimal.valueOf(100.50), BigDecimal.valueOf(200.00));
  }

  @Test
  void testFindByUserId_WhenNoOrders_ShouldReturnEmptyList() {
    List<Order> orders = orderRepository.findByUserId(999L);

    assertThat(orders).isEmpty();
  }
}
