package com.codewithiyke.dreamshops.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.codewithiyke.dreamshops.model.Cart;
import com.codewithiyke.dreamshops.model.User;
import jakarta.transaction.Transactional;
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
}
