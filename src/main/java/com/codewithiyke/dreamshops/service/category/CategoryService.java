package com.codewithiyke.dreamshops.service.category;

import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Category;
import com.codewithiyke.dreamshops.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
  private final CategoryRepository categoryRepository;

  @Override
  public Category getCategoryById(Long id) {
    return categoryRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
  }

  @Override
  public Category getCategoryByName(String name) {
    return categoryRepository.findByName(name);
  }

  @Override
  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }

  @Override
  public Category addCategory(Category category) {
    return null;
  }

  @Override
  public Category updateCategory(Category category) {
    return null;
  }

  @Override
  public void deleteCategoryById(Long id) {
    categoryRepository
        .findById(id)
        .ifPresentOrElse(
            categoryRepository::delete,
            () -> {
              throw new ResourceNotFoundException("Category not found");
            });
  }
}
