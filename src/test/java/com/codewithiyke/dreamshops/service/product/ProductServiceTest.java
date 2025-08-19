package com.codewithiyke.dreamshops.service.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.codewithiyke.dreamshops.exceptions.AlreadyExistsException;
import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Category;
import com.codewithiyke.dreamshops.model.Product;
import com.codewithiyke.dreamshops.repository.CategoryRepository;
import com.codewithiyke.dreamshops.repository.ProductRepository;
import com.codewithiyke.dreamshops.request.AddProductRequest;
import com.codewithiyke.dreamshops.request.ProductUpdateRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

  private Product testProduct1;
  private Product testProduct2;
  private Category testCategory;
  private AddProductRequest addProductRequest;
  private ProductUpdateRequest updateProductRequest;

  @BeforeEach
  void setup() {
    testCategory = new Category();
    testCategory.setId(1L);
    testCategory.setName("Electronics");

    testProduct1 = new Product();
    testProduct1.setId(1L);
    testProduct1.setName("iPhone 14");
    testProduct1.setBrand("Apple");
    testProduct1.setPrice(BigDecimal.valueOf(999.99));
    testProduct1.setInventory(10);
    testProduct1.setDescription("Latest iPhone");
    testProduct1.setCategory(testCategory);

    testProduct2 = new Product();
    testProduct2.setId(2L);
    testProduct2.setName("Samsung Galaxy");
    testProduct2.setBrand("Samsung");
    testProduct2.setPrice(BigDecimal.valueOf(1999.99));
    testProduct2.setInventory(10);
    testProduct2.setDescription("Latest Samsung");
    testProduct2.setCategory(testCategory);

    addProductRequest = new AddProductRequest();
    addProductRequest.setName("iPhone 14");
    addProductRequest.setBrand("Apple");
    addProductRequest.setPrice(BigDecimal.valueOf(999.99));
    addProductRequest.setInventory(10);
    addProductRequest.setDescription("Latest iPhone");
    addProductRequest.setCategory(testCategory);

    updateProductRequest = new ProductUpdateRequest();
    updateProductRequest.setName("Samsung S25");
    updateProductRequest.setBrand("Samsung");
    updateProductRequest.setPrice(BigDecimal.valueOf(1999.99));
    updateProductRequest.setInventory(20);
    updateProductRequest.setDescription("Latest Samsung");
    updateProductRequest.setCategory(testCategory);
  }

  @Test
  void addProduct_ShouldReturnProduct_WhenValidRequest() {
    //    Arrange
    when(productRepository.existsByNameAndBrand("iPhone 14", "Apple")).thenReturn(false);
    when(categoryRepository.findByName("Electronics")).thenReturn(testCategory);
    when(productRepository.save(any(Product.class))).thenReturn(testProduct1);

    //    Act
    Product result = productService.addProduct(addProductRequest);

    //    Assert
    assertNotNull(result);
    assertEquals("iPhone 14", result.getName());
    assertEquals("Apple", result.getBrand());
    assertEquals(BigDecimal.valueOf(999.99), result.getPrice());

    verify(productRepository).save(any(Product.class));
  }

  @Test
  void addProduct_ShouldThrowException_WhenProductAlreadyExists() {
    //    Arrange
    when(productRepository.existsByNameAndBrand("iPhone 14", "Apple")).thenReturn(true);

    //    Act & Assert
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
    //    Arrange
    when(productRepository.existsByNameAndBrand("iPhone 14", "Apple")).thenReturn(false);
    when(categoryRepository.findByName("Electronics")).thenReturn(null);
    when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
    when(productRepository.save(any(Product.class))).thenReturn(testProduct1);

    //    Act
    Product result = productService.addProduct(addProductRequest);

    //    Assert
    assertNotNull(result);
    assertEquals("iPhone 14", result.getName());
    assertEquals("Electronics", result.getCategory().getName());

    verify(categoryRepository).save(any(Category.class));
    verify(productRepository).save(any(Product.class));
  }

  @Test
  void getProductById_ShouldReturnProduct_WhenProductExists() {
    //    Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct1));

    //    Act
    Product result = productService.getProductById(1L);

    //    Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("iPhone 14", result.getName());
    verify(productRepository, times(1)).findById(1L);
  }

  @Test
  void getProductById_ShouldThrowException_WhenProductDoesNotExist() {
    //    Arrange
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    //    Act & Assert
    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));

    assertEquals("Product not found", ex.getMessage());
    verify(productRepository, times(1)).findById(99L);
  }

  @Test
  void deleteProductById_ShouldDelete_WhenProductExists() {
    // Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct1));

    // Act
    productService.deleteProductById(1L);

    // Assert
    verify(productRepository, times(1)).findById(1L);
    verify(productRepository, times(1)).delete(testProduct1);
  }

  @Test
  void deleteProductById_ShouldThrowException_WhenProductDoesNotExist() {
    // Arrange
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    // Act + Assert
    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProductById(99L));

    assertEquals("Product not found", exception.getMessage());
    verify(productRepository, times(1)).findById(99L);
    verify(productRepository, never()).delete(any());
  }

  @Test
  void updateProduct_ShouldUpdateAndSave_WhenProductExists() {
    // Arrange
    when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct1));
    when(categoryRepository.findByName("Electronics")).thenReturn(testCategory);
    when(productRepository.save(any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Product updatedProduct = productService.updateProduct(updateProductRequest, 1L);

    // Assert
    assertNotNull(updatedProduct);
    assertEquals("Samsung S25", updatedProduct.getName());
    assertEquals("Samsung", updatedProduct.getBrand());
    assertEquals(new BigDecimal("1999.99"), updatedProduct.getPrice());
    assertEquals(20, updatedProduct.getInventory());
    assertEquals("Latest Samsung", updatedProduct.getDescription());
    assertEquals(testCategory, updatedProduct.getCategory());

    verify(productRepository, times(1)).findById(1L);
    verify(categoryRepository, times(1)).findByName("Electronics");
    verify(productRepository, times(1)).save(testProduct1);
  }

  @Test
  void updateProduct_ShouldThrowException_WhenProductDoesNotExist() {
    // Arrange
    when(productRepository.findById(99L)).thenReturn(Optional.empty());

    // Act + Assert
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> productService.updateProduct(updateProductRequest, 99L));

    assertEquals("Product not found", exception.getMessage());
    verify(productRepository, times(1)).findById(99L);
    verify(productRepository, never()).save(any());
    verify(categoryRepository, never()).findByName(anyString());
  }

  @Test
  void testGetAllProducts() {
    when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct1, testProduct2));

    List<Product> result = productService.getAllProducts();

    assertEquals(2, result.size());
    assertTrue(result.contains(testProduct1));
    verify(productRepository, times(1)).findAll();
  }

  @Test
  void testGetAllProductsByCategory() {
    when(productRepository.findByCategoryName("Electronics"))
        .thenReturn(Arrays.asList(testProduct1, testProduct2));

    List<Product> result = productService.getAllProductsByCategory("Electronics");

    assertEquals(2, result.size());
    assertEquals("Electronics", result.get(0).getCategory().getName());
    verify(productRepository).findByCategoryName("Electronics");
  }

  @Test
  void testGetProductsByBrand() {
    when(productRepository.findByBrand("Samsung")).thenReturn(List.of(testProduct2));

    List<Product> result = productService.getProductsByBrand("Samsung");

    assertEquals(1, result.size());
    assertEquals("Samsung", result.get(0).getBrand());
    verify(productRepository).findByBrand("Samsung");
  }

  @Test
  void testGetProductsByCategoryAndBrand() {
    when(productRepository.findByCategoryNameAndBrand("Electronics", "Samsung"))
        .thenReturn(List.of(testProduct2));

    List<Product> result = productService.getProductsByCategoryAndBrand("Electronics", "Samsung");

    assertEquals(1, result.size());
    assertEquals("Samsung Galaxy", result.get(0).getName());
    verify(productRepository).findByCategoryNameAndBrand("Electronics", "Samsung");
  }

  @Test
  void testGetProductsByName() {
    when(productRepository.findByName("iPhone 14")).thenReturn(List.of(testProduct1));

    List<Product> result = productService.getProductsByName("iPhone 14");

    assertEquals(1, result.size());
    assertEquals("iPhone 14", result.get(0).getName());
    verify(productRepository).findByName("iPhone 14");
  }

  @Test
  void testGetProductsByBrandAndName() {
    when(productRepository.findByBrandAndName("Samsung", "Samsung Galaxy"))
        .thenReturn(List.of(testProduct2));

    List<Product> result = productService.getProductsByBrandAndName("Samsung", "Samsung Galaxy");

    assertEquals(1, result.size());
    assertEquals("Samsung Galaxy", result.get(0).getName());
    assertEquals("Samsung", result.get(0).getBrand());
    verify(productRepository).findByBrandAndName("Samsung", "Samsung Galaxy");
  }

  @Test
  void testCountProductsByBrandAndName() {
    when(productRepository.countByBrandAndName("Samsung", "Phone")).thenReturn(5L);

    Long count = productService.countProductsByBrandAndName("Samsung", "Phone");

    assertEquals(5L, count);
    verify(productRepository).countByBrandAndName("Samsung", "Phone");
  }
}
