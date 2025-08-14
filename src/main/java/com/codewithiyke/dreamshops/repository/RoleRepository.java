package com.codewithiyke.dreamshops.repository;

import com.codewithiyke.dreamshops.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String role);
}
