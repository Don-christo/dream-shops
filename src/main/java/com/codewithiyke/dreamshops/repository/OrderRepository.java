package com.codewithiyke.dreamshops.repository;

import com.codewithiyke.dreamshops.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
