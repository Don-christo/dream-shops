package com.codewithiyke.dreamshops.service.cart;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Cart;
import com.codewithiyke.dreamshops.model.CartItem;
import com.codewithiyke.dreamshops.model.Product;
import com.codewithiyke.dreamshops.model.User;
import com.codewithiyke.dreamshops.repository.CartItemRepository;
import com.codewithiyke.dreamshops.repository.CartRepository;
import com.codewithiyke.dreamshops.service.product.IProductService;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
  @InjectMocks private CartService cartService;
  @Mock private CartRepository cartRepository;
  @Mock private CartItemRepository cartItemRepository;
  @Mock private IProductService productService;
  private Product testProduct;
  private Cart testCart;

  @BeforeEach
  void setUp() {
    User testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@example.com");

    testProduct = new Product();
    testProduct.setId(1L);
    testProduct.setName("iPhone 14");
    testProduct.setPrice(BigDecimal.valueOf(999.99));
    testProduct.setInventory(10);

    CartItem testCartItem = new CartItem();
    testCartItem.setId(1L);
    testCartItem.setProduct(testProduct);
    testCartItem.setQuantity(2);
    testCartItem.setUnitPrice(testProduct.getPrice());
    testCartItem.setTotalPrice(testProduct.getPrice().multiply(BigDecimal.valueOf(2)));

    testCart = new Cart();
    testCart.setId(1L);
    testCart.setUser(testUser);
    testCart.setTotalAmount(BigDecimal.ZERO);
    testCart.setItems(new HashSet<>());
    testCart.addItem(testCartItem);
  }

  @Test
  void getCart_ShouldReturnExistingCart_WhenCartExists() {
    // Arrange
    when(cartRepository.findById(1L)).thenReturn(Optional.of(testCart));
    when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

    // Act
    Cart result = cartService.getCart(1L);

    // Assert
    assertNotNull(result);
    assertEquals(testCart.getId(), result.getId());
    assertEquals(testCart.getTotalAmount(), result.getTotalAmount());
    verify(cartRepository).findById(1L);
    verify(cartRepository).save(testCart);
  }

  @Test
  void getCart_ShouldThrowException_WhenCartDoesNotExist() {
    // Arrange
    when(cartRepository.findById(1L)).thenReturn(Optional.empty());

    // Act / Assert
    ResourceNotFoundException exception =
        assertThrows(ResourceNotFoundException.class, () -> cartService.getCart(1L));

    assertEquals("Cart not found", exception.getMessage());
    verify(cartRepository).findById(1L);
    verify(cartRepository, never()).save(any());
  }

  @Test
  void getCart_ShouldUpdateTotalAmount_WhenCartHasItems() {
    // Simulate expected calculation: total amount from cart items
    BigDecimal expectedTotal = testProduct.getPrice().multiply(BigDecimal.valueOf(2)); // 2 x 999.99

    // Arrange
    when(cartRepository.findById(1L)).thenReturn(Optional.of(testCart));
    when(cartRepository.save(any(Cart.class)))
        .thenAnswer(invocation -> invocation.<Cart>getArgument(0));

    // Act
    Cart result = cartService.getCart(1L);

    // Assert
    assertNotNull(result);
    assertEquals(expectedTotal, result.getTotalAmount(), "Total amount should equal sum of items");
    verify(cartRepository).findById(1L);
    verify(cartRepository).save(result);
  }

  @Test
  void clearCart_ShouldRemoveAllItems() {
    // Arrange
    testCart.getItems().add(new CartItem());
    when(cartRepository.findById(1L)).thenReturn(Optional.of(testCart));
    when(cartRepository.save(any(Cart.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

    // Act
    cartService.clearCart(1L);

    // Assert
    verify(cartItemRepository).deleteAllByCartId(1L);
    verify(cartRepository, times(2)).save(testCart);
    assertEquals(BigDecimal.ZERO, testCart.getTotalAmount());
    assertTrue(testCart.getItems().isEmpty());
  }
}
