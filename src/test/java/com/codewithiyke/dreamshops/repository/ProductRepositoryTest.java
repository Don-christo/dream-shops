package com.codewithiyke.dreamshops.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.codewithiyke.dreamshops.model.Category;
import com.codewithiyke.dreamshops.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class ProductRepositoryTest {
  @Autowired private TestEntityManager testEntityManager;

  @Autowired private ProductRepository productRepository;

  private Category testCategory;
  private Product testProduct;

  @BeforeEach
  void setup() {
    testCategory = new Category();
    testCategory.setName("Electronics");
    testEntityManager.persistAndFlush(testCategory);

    testProduct = new Product();
    testProduct.setName("iPhone 14");
    testProduct.setBrand("Apple");
    testProduct.setPrice(BigDecimal.valueOf(999.99));
    testProduct.setInventory(10);
    testProduct.setDescription("Latest iPhone");
    testProduct.setCategory(testCategory);
  }

  @Test
  void findById_ShouldReturnProduct_WhenProductExists() {
    //      Arrange
    Product savedProduct = testEntityManager.persistAndFlush(testProduct);

    //    Act
    Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

    //      Assert
    assertTrue(foundProduct.isPresent());
    assertEquals(testProduct.getName(), foundProduct.get().getName());
    assertEquals(testProduct.getBrand(), foundProduct.get().getBrand());
  }

  @Test
  void findByCategoryName_ShouldReturnProducts_WhenCategoryExists() {
    testEntityManager.persistAndFlush(testProduct);

    List<Product> products = productRepository.findByCategoryName("Electronics");

    assertEquals(1, products.size());
    assertEquals("iPhone 14", products.get(0).getName());
  }

  @Test
  void findByBrand_ShouldReturnProducts_WhenBrandExists() {
    testEntityManager.persistAndFlush(testProduct);

    List<Product> products = productRepository.findByBrand("Apple");

    assertEquals(1, products.size());
    assertEquals("iPhone 14", products.get(0).getName());
  }

  @Test
  void findByCategoryNameAndBrand_ShouldReturnProducts_WhenBothMatch() {
    testEntityManager.persistAndFlush(testProduct);

    List<Product> products = productRepository.findByCategoryNameAndBrand("Electronics", "Apple");

    assertEquals(1, products.size());
    assertEquals("iPhone 14", products.get(0).getName());
  }

  @Test
  void findByName_ShouldReturnProducts_WhenNameExists() {
    testEntityManager.persistAndFlush(testProduct);

    List<Product> products = productRepository.findByName("iPhone 14");

    assertEquals(1, products.size());
    assertEquals("Apple", products.get(0).getBrand());
  }

  @Test
  void findByBrandAndName_ShouldReturnProducts_WhenBothMatch() {
    testEntityManager.persistAndFlush(testProduct);

    List<Product> products = productRepository.findByBrandAndName("Apple", "iPhone 14");

    assertEquals(1, products.size());
    assertEquals("Electronics", products.get(0).getCategory().getName());
  }

  @Test
  void countByBrandAndName_ShouldReturnCount_WhenProductsExist() {
    testEntityManager.persistAndFlush(testProduct);

    Long count = productRepository.countByBrandAndName("Apple", "iPhone 14");

    assertEquals(1L, count);
  }

  @Test
  void existsByNameAndBrand_ShouldReturnTrue_WhenProductExists() {
    testEntityManager.persistAndFlush(testProduct);

    boolean exists = productRepository.existsByNameAndBrand("iPhone 14", "Apple");

    assertTrue(exists);
  }

  @Test
  void existsByNameAndBrand_ShouldReturnFalse_WhenProductDoesNotExist() {
    testEntityManager.persistAndFlush(testProduct);

    boolean exists = productRepository.existsByNameAndBrand("Galaxy S22", "Samsung");

    assertFalse(exists);
  }
}
