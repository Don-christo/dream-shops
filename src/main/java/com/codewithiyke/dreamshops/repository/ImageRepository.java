package com.codewithiyke.dreamshops.repository;

import com.codewithiyke.dreamshops.model.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
  List<Image> findByProductId(Long id);
}
