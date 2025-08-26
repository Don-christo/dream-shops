package com.codewithiyke.dreamshops.service.cart;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Cart;
import com.codewithiyke.dreamshops.model.CartItem;
import com.codewithiyke.dreamshops.model.Product;
import com.codewithiyke.dreamshops.repository.CartItemRepository;
import com.codewithiyke.dreamshops.repository.CartRepository;
import com.codewithiyke.dreamshops.service.product.IProductService;
import java.math.BigDecimal;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

  @InjectMocks private CartItemService cartItemService;

  @Mock private ICartService cartService;
  @Mock private IProductService productService;
  @Mock private CartItemRepository cartItemRepository;
  @Mock private CartRepository cartRepository;

  private Cart testCart;
  private Product testProduct;
  private CartItem testCartItem;

  @BeforeEach
  void setUp() {
    testProduct = new Product();
    testProduct.setId(1L);
    testProduct.setName("iPhone 14");
    testProduct.setPrice(BigDecimal.valueOf(999.99));

    testCart = new Cart();
    testCart.setId(1L);
    testCart.setTotalAmount(BigDecimal.valueOf(1999.98)); // for two items
    testCart.setItems(new HashSet<>());

    testCartItem = new CartItem();
    testCartItem.setId(1L);
    testCartItem.setProduct(testProduct);
    testCartItem.setQuantity(2);
    testCartItem.setUnitPrice(testProduct.getPrice());
    testCartItem.setTotalPrice(); // calculates price internally
  }

  @Test
  void addItemToCart_ShouldAddNewItem_WhenItemNotInCart() {
    // Arrange
    when(cartService.getCart(1L)).thenReturn(testCart);
    when(productService.getProductById(1L)).thenReturn(testProduct);
    when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));
    when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

    //    Act
    cartItemService.addItemToCart(1L, 1L, 2);

    //    Assert
    assertEquals(1, testCart.getItems().size());
    CartItem addedItem = testCart.getItems().iterator().next();
    assertEquals(2, addedItem.getQuantity());
    assertEquals(testProduct.getPrice(), addedItem.getUnitPrice());
    assertEquals(testProduct.getPrice().multiply(BigDecimal.valueOf(2)), addedItem.getTotalPrice());

    verify(cartItemRepository).save(any(CartItem.class));
    verify(cartRepository).save(testCart);
  }

  @Test
  void addItemToCart_ShouldUpdateQuantity_WhenItemAlreadyInCart() {
    // Arrange
    testCart.addItem(testCartItem);

    when(cartService.getCart(1L)).thenReturn(testCart);
    when(productService.getProductById(1L)).thenReturn(testProduct);
    when(cartItemRepository.save(any(CartItem.class)))
        .thenAnswer(AdditionalAnswers.returnsFirstArg());
    when(cartRepository.save(any(Cart.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

    // Act
    cartItemService.addItemToCart(1L, 1L, 2); // adding 2 more items

    // Assert
    assertEquals(4, testCartItem.getQuantity(), "Quantity should increase from 2 to 4");
    assertEquals(
        testProduct.getPrice().multiply(BigDecimal.valueOf(4)),
        testCartItem.getTotalPrice(),
        "Total price should match updated quantity");

    verify(cartItemRepository).save(testCartItem);
    verify(cartRepository).save(testCart);
  }

  @Test
  void addItemToCart_ShouldThrowException_WhenCartNotFound() {
    // Arrange
    when(cartService.getCart(1L)).thenThrow(new ResourceNotFoundException("Cart not found"));

    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          cartItemService.addItemToCart(1L, 1L, 2);
        });

    verify(cartRepository, never()).save(any());
    verify(cartItemRepository, never()).save(any());
  }

  @Test
  void removeItemFromCart_ShouldRemoveItem_WhenItemExists() {
    // Arrange
    testCart.addItem(testCartItem);

    when(cartService.getCart(1L)).thenReturn(testCart);

    // Act
    cartItemService.removeItemFromCart(1L, 1L);

    // Assert
    assertFalse(testCart.getItems().contains(testCartItem), "Item should be removed from the cart");
    assertEquals(
        BigDecimal.ZERO, testCart.getTotalAmount(), "Total amount should be zero after removal");
    verify(cartRepository).save(testCart);
  }

  @Test
  void removeItemFromCart_ShouldThrowException_WhenItemNotFound() {
    // Arrange
    Cart emptyCart = new Cart();
    emptyCart.setId(1L);
    emptyCart.setItems(new HashSet<>());

    when(cartService.getCart(1L)).thenReturn(emptyCart);

    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          cartItemService.removeItemFromCart(1L, 1L);
        });

    verify(cartRepository, never()).save(any());
  }

  @Test
  void removeItemFromCart_ShouldThrowException_WhenCartNotFound() {
    // Arrange
    when(cartService.getCart(1L)).thenThrow(new ResourceNotFoundException("Cart not found"));

    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> cartItemService.removeItemFromCart(1L, 1L));

    verify(cartRepository, never()).save(any());
  }

  @Test
  void updateQuantity_ShouldUpdateQuantity_WhenItemExists() {
    // Arrange
    testCartItem.setQuantity(2);
    testCartItem.setUnitPrice(BigDecimal.valueOf(100));
    testCartItem.setTotalPrice(); // initial total price = 200
    testCart.getItems().add(testCartItem);

    when(cartService.getCart(1L)).thenReturn(testCart);

    // Act
    cartItemService.updateQuantity(1L, 1L, 5);

    // Assert
    assertEquals(5, testCartItem.getQuantity()); // quantity updated
    assertEquals(
        testCartItem.getProduct().getPrice(), testCartItem.getUnitPrice()); // unit price updated
    assertEquals(
        BigDecimal.valueOf(5).multiply(testCartItem.getUnitPrice()),
        testCartItem.getTotalPrice()); // total price updated
    assertEquals(testCartItem.getTotalPrice(), testCart.getTotalAmount()); // cart total updated
    verify(cartRepository).save(testCart); // cart was saved
  }
}
