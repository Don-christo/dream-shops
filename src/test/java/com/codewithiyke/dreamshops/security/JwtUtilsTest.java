package com.codewithiyke.dreamshops.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.codewithiyke.dreamshops.security.jwt.JwtUtils;
import com.codewithiyke.dreamshops.security.user.ShopUserDetails;
import io.jsonwebtoken.JwtException;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

  @InjectMocks private JwtUtils jwtUtils;
  @Mock private Authentication authentication;
  @Mock private ShopUserDetails userDetails;

  @BeforeEach
  void setUp() {
    String testSecret = "bXlTdXBlclNlY3JldEtleU15U3VwZXJTZWNyZXRLZXk=";
    ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testSecret);

    int testExpirationMs = 86400000; // 24 hours
    ReflectionTestUtils.setField(jwtUtils, "expirationTime", testExpirationMs);
  }

  @Test
  void generateTokenForUser_ShouldReturnValidToken() {
    // Arrange
    Collection<? extends GrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority("ROLE_USER"));

    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getEmail()).thenReturn("testuser@example.com");
    when(userDetails.getId()).thenReturn(1L);
    doReturn(authorities).when(userDetails).getAuthorities();

    // Act
    String token = jwtUtils.generateTokenForUser(authentication);

    // Assert
    assertNotNull(token);
    assertFalse(token.isEmpty());
    assertTrue(jwtUtils.validateToken(token));
  }

  @Test
  void getUsernameFromToken_ShouldReturnCorrectUsername() {
    // Arrange
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getEmail()).thenReturn("testuser@example.com");
    when(userDetails.getAuthorities()).thenReturn(List.of());
    when(userDetails.getId()).thenReturn(1L);

    String token = jwtUtils.generateTokenForUser(authentication);

    // Act
    String extractedUsername = jwtUtils.getUserNameFromToken(token);

    // Assert
    assertEquals("testuser@example.com", extractedUsername);
  }

  @Test
  void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
    // Arrange
    when(authentication.getPrincipal()).thenReturn(userDetails);
    when(userDetails.getEmail()).thenReturn("testuser@example.com");
    when(userDetails.getAuthorities()).thenReturn(List.of());
    when(userDetails.getId()).thenReturn(1L);

    String token = jwtUtils.generateTokenForUser(authentication);

    // Act
    boolean isValid = jwtUtils.validateToken(token);

    // Assert
    assertTrue(isValid);
  }

  @Test
  void validateToken_ShouldThrowException_WhenTokenIsInvalid() {
    // Arrange
    String invalidToken = "invalid.token.here";

    // Act & Assert
    assertThrows(JwtException.class, () -> jwtUtils.validateToken(invalidToken));
  }
}
