package com.codewithiyke.dreamshops.repository;

import com.codewithiyke.dreamshops.model.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
  @EntityGraph(attributePaths = {"orderItems", "orderItems.product", "user"})
  Optional<Order> findWithItemsByOrderId(Long orderId);

  @EntityGraph(attributePaths = {"orderItems", "orderItems.product", "user"})
  List<Order> findByUserId(Long userId);
}
