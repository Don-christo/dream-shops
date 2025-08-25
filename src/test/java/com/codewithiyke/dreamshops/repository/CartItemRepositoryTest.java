package com.codewithiyke.dreamshops.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.codewithiyke.dreamshops.model.*;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@Transactional
public class CartItemRepositoryTest {

  @Autowired private TestEntityManager testEntityManager;
  @Autowired private CartItemRepository cartItemRepository;

  private Cart testCart;
  private CartItem testCartItem;
  private Product testProduct;

  @BeforeEach
  void setup() {
    User testUser = new User();
    testUser.setFirstName("John");
    testUser.setLastName("Doe");
    testUser.setEmail("john@example.com");
    testEntityManager.persistAndFlush(testUser);

    testCart = new Cart();
    testCart.setUser(testUser);
    testEntityManager.persistAndFlush(testCart);

    Category category = new Category();
    category.setName("Electronics");
    testEntityManager.persistAndFlush(category);

    testProduct = new Product();
    testProduct.setName("iPhone 14");
    testProduct.setBrand("Apple");
    testProduct.setPrice(BigDecimal.valueOf(999.99));
    testProduct.setInventory(10);
    testProduct.setDescription("Latest iPhone");
    testProduct.setCategory(category);
    testEntityManager.persistAndFlush(testProduct);

    testCartItem = new CartItem();
    testCartItem.setCart(testCart);
    testCartItem.setProduct(testProduct);
    testCartItem.setQuantity(2);
    testCartItem.setUnitPrice(BigDecimal.valueOf(999.99));
    testEntityManager.persistAndFlush(testCartItem);
  }

  @Test
  void deleteAllByCartId_ShouldRemoveItems_WhenCartExists() {
    // Verify item exists before deletion
    List<CartItem> itemsBefore = cartItemRepository.findAll();
    assertEquals(1, itemsBefore.size());

    // Delete
    cartItemRepository.deleteAllByCartId(testCart.getId());

    List<CartItem> itemsAfter = cartItemRepository.findAll();
    assertTrue(itemsAfter.isEmpty());
  }

  @Test
  void save_ShouldPersistCartItem_WhenValid() {
    CartItem savedItem = cartItemRepository.save(testCartItem);
    assertNotNull(savedItem.getId());
    assertEquals(testCart.getId(), savedItem.getCart().getId());
    assertEquals(testProduct.getId(), savedItem.getProduct().getId());
  }
}
