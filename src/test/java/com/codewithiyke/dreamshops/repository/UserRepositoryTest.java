package com.codewithiyke.dreamshops.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codewithiyke.dreamshops.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Transactional
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("john@example.com");
    user.setPassword("password123");
    userRepository.save(user);
  }

  @Test
  void testExistsByEmail_ShouldReturnTrue_WhenUserExists() {
    boolean exists = userRepository.existsByEmail("john@example.com");

    assertThat(exists).isTrue();
  }

  @Test
  void testExistsByEmail_ShouldReturnFalse_WhenUserDoesNotExist() {
    boolean exists = userRepository.existsByEmail("jane@example.com");

    assertThat(exists).isFalse();
  }

  @Test
  void testFindByEmail_ShouldReturnUser_WhenUserExists() {
    User foundUser = userRepository.findByEmail("john@example.com");

    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getEmail()).isEqualTo("john@example.com");
    assertThat(foundUser.getFirstName()).isEqualTo("John");
  }

  @Test
  void testFindByEmail_ShouldReturnNull_WhenUserDoesNotExist() {
    User foundUser = userRepository.findByEmail("jane@example.com");

    assertThat(foundUser).isNull();
  }
}
