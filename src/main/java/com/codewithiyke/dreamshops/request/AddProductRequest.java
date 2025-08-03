package com.codewithiyke.dreamshops.request;

import com.codewithiyke.dreamshops.model.Category;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class AddProductRequest {
  private Long id;
  private String name;
  private String brand;
  private BigDecimal price;
  private int inventory;
  private String description;
  private Category category;
}
