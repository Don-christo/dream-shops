package com.codewithiyke.dreamshops.repository;

import com.codewithiyke.dreamshops.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Category findByName(String name);
}
