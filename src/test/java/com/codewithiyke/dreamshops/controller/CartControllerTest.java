package com.codewithiyke.dreamshops.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Cart;
import com.codewithiyke.dreamshops.security.jwt.JwtUtils;
import com.codewithiyke.dreamshops.security.user.ShopUserDetailsService;
import com.codewithiyke.dreamshops.service.cart.ICartService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ICartService cartService;
  @MockitoBean private JwtUtils jwtUtils;
  @MockitoBean private ShopUserDetailsService userDetailsService;

  private Cart testCart;

  @BeforeEach
  void setUp() {
    testCart = new Cart();
    testCart.setId(1L);
    testCart.setTotalAmount(BigDecimal.valueOf(1500));
  }

  @Test
  void getCart_ShouldReturnCart_WhenCartExists() throws Exception {
    when(cartService.getCart(1L)).thenReturn(testCart);

    mockMvc
        .perform(get("/api/v1/carts/{cartId}/my-cart", 1L))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Success"))
        .andExpect(jsonPath("$.data.cartId").value(1L));

    verify(cartService).getCart(1L);
  }

  @Test
  void getCart_ShouldReturnNotFound_WhenCartDoesNotExist() throws Exception {
    when(cartService.getCart(1L)).thenThrow(new ResourceNotFoundException("Cart not found"));

    mockMvc
        .perform(get("/api/v1/carts/{cartId}/my-cart", 1L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Cart not found"));

    verify(cartService).getCart(1L);
  }

  @Test
  void clearCart_ShouldReturnSuccessMessage_WhenCartExists() throws Exception {
    doNothing().when(cartService).clearCart(1L);

    mockMvc
        .perform(delete("/api/v1/carts/{cartId}/clear", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Clear cart success!"));

    verify(cartService).clearCart(1L);
  }

  @Test
  void clearCart_ShouldReturnNotFound_WhenCartDoesNotExist() throws Exception {
    doThrow(new ResourceNotFoundException("Cart not found")).when(cartService).clearCart(1L);

    mockMvc
        .perform(delete("/api/v1/carts/{cartId}/clear", 1L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Cart not found"));

    verify(cartService).clearCart(1L);
  }

  @Test
  void getTotalAmount_ShouldReturnTotalPrice_WhenCartExists() throws Exception {
    when(cartService.getTotalPrice(1L)).thenReturn(BigDecimal.valueOf(1500));

    mockMvc
        .perform(get("/api/v1/carts/{cartId}/cart/total-price", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Total Price"))
        .andExpect(jsonPath("$.data").value(1500));

    verify(cartService).getTotalPrice(1L);
  }

  @Test
  void getTotalAmount_ShouldReturnNotFound_WhenCartDoesNotExist() throws Exception {
    when(cartService.getTotalPrice(1L)).thenThrow(new ResourceNotFoundException("Cart not found"));

    mockMvc
        .perform(get("/api/v1/carts/{cartId}/cart/total-price", 1L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Cart not found"));

    verify(cartService).getTotalPrice(1L);
  }
}
