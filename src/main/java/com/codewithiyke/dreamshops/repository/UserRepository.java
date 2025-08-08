package com.codewithiyke.dreamshops.repository;

import com.codewithiyke.dreamshops.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);
}
