package com.codewithiyke.dreamshops.repository;

import com.codewithiyke.dreamshops.model.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
  @EntityGraph(
      attributePaths = {"items", "items.product", "items.product.images", "items.product.category"})
  Optional<Cart> findWithItemsById(Long id);

  Cart findByUserId(Long userId);
}
