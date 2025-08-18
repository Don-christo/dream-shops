package com.codewithiyke.dreamshops.service.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.codewithiyke.dreamshops.exceptions.AlreadyExistsException;
import com.codewithiyke.dreamshops.model.Category;
import com.codewithiyke.dreamshops.model.Product;
import com.codewithiyke.dreamshops.repository.CategoryRepository;
import com.codewithiyke.dreamshops.repository.ProductRepository;
import com.codewithiyke.dreamshops.request.AddProductRequest;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
  @Mock private ProductRepository productRepository;

  @Mock private CategoryRepository categoryRepository;

  @InjectMocks private ProductService productService;

  private Product testProduct;
  private Category testCategory;
  private AddProductRequest addProductRequest;

  @BeforeEach
  void setup() {
    testCategory = new Category();
    testCategory.setId(1L);
    testCategory.setName("Electronics");

    testProduct = new Product();
    testProduct.setId(1L);
    testProduct.setName("iPhone 14");
    testProduct.setBrand("Apple");
    testProduct.setPrice(BigDecimal.valueOf(999.99));
    testProduct.setInventory(10);
    testProduct.setDescription("Latest iPhone");
    testProduct.setCategory(testCategory);

    addProductRequest = new AddProductRequest();
    addProductRequest.setName("iPhone 14");
    addProductRequest.setBrand("Apple");
    addProductRequest.setPrice(BigDecimal.valueOf(999.99));
    addProductRequest.setInventory(10);
    addProductRequest.setDescription("Latest iPhone");
    addProductRequest.setCategory(testCategory);
  }

  @Test
  void addProduct_ShouldReturnProduct_WhenValidRequest() {
    //    Given
    when(productRepository.existsByNameAndBrand("iPhone 14", "Apple")).thenReturn(false);
    when(categoryRepository.findByName("Electronics")).thenReturn(testCategory);
    when(productRepository.save(any(Product.class))).thenReturn(testProduct);

    //    When
    Product result = productService.addProduct(addProductRequest);

    //    Then
    assertNotNull(result);
    assertEquals("iPhone 14", result.getName());
    assertEquals("Apple", result.getBrand());
    assertEquals(BigDecimal.valueOf(999.99), result.getPrice());

    verify(productRepository).save(any(Product.class));
  }

  @Test
  void addProduct_ShouldThrowException_WhenProductAlreadyExists() {
    //    Given
    when(productRepository.existsByNameAndBrand("iPhone 14", "Apple")).thenReturn(true);

    //    When & Then
    AlreadyExistsException ex =
        assertThrows(
            AlreadyExistsException.class, () -> productService.addProduct(addProductRequest));

    assertEquals(
        "Apple iPhone 14 already exists, you may update this product instead!", ex.getMessage());

    verify(productRepository, never()).save(any(Product.class));
    verify(categoryRepository, never()).save(any(Category.class));
  }

  @Test
  void addProduct_ShouldCreateNewCategory_WhenCategoryDoesNotExist() {
    //    Given
    when(productRepository.existsByNameAndBrand("iPhone 14", "Apple")).thenReturn(false);
    when(categoryRepository.findByName("Electronics")).thenReturn(null);
    when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
    when(productRepository.save(any(Product.class))).thenReturn(testProduct);

    //    When
    Product result = productService.addProduct(addProductRequest);

    //    Then
    assertNotNull(result);
    assertEquals("iPhone 14", result.getName());
    assertEquals("Electronics", result.getCategory().getName());

    verify(categoryRepository).save(any(Category.class));
    verify(productRepository).save(any(Product.class));
  }
}
