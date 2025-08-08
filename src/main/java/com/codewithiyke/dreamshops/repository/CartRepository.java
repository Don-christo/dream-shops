package com.codewithiyke.dreamshops.repository;

import com.codewithiyke.dreamshops.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
  Cart findByUserId(Long userId);
}
