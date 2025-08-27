package com.codewithiyke.dreamshops.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.codewithiyke.dreamshops.model.Cart;
import com.codewithiyke.dreamshops.model.CartItem;
import com.codewithiyke.dreamshops.model.Product;
import com.codewithiyke.dreamshops.model.User;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@Transactional
public class CartRepositoryTest {

  @Autowired private TestEntityManager testEntityManager;
  @Autowired private CartRepository cartRepository;

  private User testUser;
  private Cart testCart;
  private Product testProduct;

  @BeforeEach
  void setup() {
    testUser = new User();
    testUser.setFirstName("John");
    testUser.setLastName("Doe");
    testUser.setEmail("john@example.com");
    testEntityManager.persistAndFlush(testUser);

    testCart = new Cart();
    testCart.setUser(testUser);
    testEntityManager.persistAndFlush(testCart);

    testProduct = new Product();
    testProduct.setName("Test Product");
    testProduct.setPrice(BigDecimal.valueOf(100));
    testEntityManager.persistAndFlush(testProduct);

    CartItem item = new CartItem();
    item.setCart(testCart);
    item.setProduct(testProduct);
    item.setQuantity(2);
    testEntityManager.persistAndFlush(item);

    testEntityManager.refresh(testCart);
  }

  @Test
  void findByUserId_ShouldReturnCart_WhenCartExists() {
    Cart foundCart = cartRepository.findByUserId(testUser.getId());

    assertNotNull(foundCart);
    assertEquals(testCart.getId(), foundCart.getId());
    assertEquals(testUser.getId(), foundCart.getUser().getId());
  }

  @Test
  void findByUserId_ShouldReturnNull_WhenCartDoesNotExist() {
    Cart foundCart = cartRepository.findByUserId(999L);
    assertNull(foundCart);
  }

    @Test
    void findWithItemsById_ShouldReturnCartWithItems_WhenCartExists() {
        Optional<Cart> optionalCart = cartRepository.findWithItemsById(testCart.getId());

        assertTrue(optionalCart.isPresent());
        Cart foundCart = optionalCart.get();

        assertEquals(testCart.getId(), foundCart.getId());
        assertNotNull(foundCart.getItems());
        assertFalse(foundCart.getItems().isEmpty(), "Cart should contain items");
        assertEquals(1, foundCart.getItems().size());
        assertEquals(testProduct.getId(), foundCart.getItems().iterator().next().getProduct().getId());
    }

    @Test
    void findWithItemsById_ShouldReturnEmpty_WhenCartDoesNotExist() {
        Optional<Cart> optionalCart = cartRepository.findWithItemsById(999L);
        assertTrue(optionalCart.isEmpty(), "Cart should not exist");
    }
}
