package com.codewithiyke.dreamshops.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Cart;
import com.codewithiyke.dreamshops.model.User;
import com.codewithiyke.dreamshops.security.jwt.JwtUtils;
import com.codewithiyke.dreamshops.security.user.ShopUserDetailsService;
import com.codewithiyke.dreamshops.service.cart.ICartItemService;
import com.codewithiyke.dreamshops.service.cart.ICartService;
import com.codewithiyke.dreamshops.service.user.IUserService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartItemController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CartItemControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private ICartItemService cartItemService;
    @MockitoBean private ICartService cartService;
    @MockitoBean private IUserService userService;
    @MockitoBean private JwtUtils jwtUtils;
    @MockitoBean private ShopUserDetailsService userDetailsService;
    
    private User testUser;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testCart = new Cart();
        testCart.setId(1L);
    }

    @Test
    void addItemToCart_ShouldReturnSuccess_WhenUserAuthenticatedAndProductExists() throws Exception {
        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(cartService.initializeNewCart(testUser)).thenReturn(testCart);
        doNothing().when(cartItemService).addItemToCart(eq(1L), eq(100L), eq(2));

        mockMvc.perform(post("/api/v1/cartItems/item/add")
                        .param("productId", "100")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Add Item Success"));

        verify(userService).getAuthenticatedUser();
        verify(cartService).initializeNewCart(testUser);
        verify(cartItemService).addItemToCart(1L, 100L, 2);
    }

    @Test
    void addItemToCart_ShouldReturnUnauthorized_WhenJwtExceptionOccurs() throws Exception {
        when(userService.getAuthenticatedUser()).thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(post("/api/v1/cartItems/item/add")
                        .param("productId", "100")
                        .param("quantity", "2"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid token"));

        verify(userService).getAuthenticatedUser();
    }

    @Test
    void addItemToCart_ShouldReturnNotFound_WhenProductNotFound() throws Exception {
        when(userService.getAuthenticatedUser()).thenReturn(testUser);
        when(cartService.initializeNewCart(testUser)).thenReturn(testCart);
        doThrow(new ResourceNotFoundException("Product not found"))
                .when(cartItemService).addItemToCart(eq(1L), eq(100L), eq(2));

        mockMvc.perform(post("/api/v1/cartItems/item/add")
                        .param("productId", "100")
                        .param("quantity", "2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));

        verify(cartItemService).addItemToCart(1L, 100L, 2);
    }

    @Test
    void removeItemFromCart_ShouldReturnSuccess_WhenItemExists() throws Exception {
        doNothing().when(cartItemService).removeItemFromCart(1L, 200L);

        mockMvc.perform(delete("/api/v1/cartItems/cart/{cartId}/item/{itemId}/remove", 1L, 200L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Remove Item Success"));

        verify(cartItemService).removeItemFromCart(1L, 200L);
    }

    @Test
    void removeItemFromCart_ShouldReturnNotFound_WhenItemDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Cart item not found"))
                .when(cartItemService).removeItemFromCart(1L, 200L);

        mockMvc.perform(delete("/api/v1/cartItems/cart/{cartId}/item/{itemId}/remove", 1L, 200L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart item not found"));
    }

    @Test
    void updateItemQuantity_ShouldReturnSuccess_WhenItemExists() throws Exception {
        doNothing().when(cartItemService).updateQuantity(1L, 200L, 3);

        mockMvc.perform(put("/api/v1/cartItems/cart/{cartId}/item/{itemId}/update", 1L, 200L)
                        .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Update Item Success"));

        verify(cartItemService).updateQuantity(1L, 200L, 3);
    }

    @Test
    void updateItemQuantity_ShouldReturnNotFound_WhenItemDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Cart item not found"))
                .when(cartItemService).updateQuantity(1L, 200L, 3);

        mockMvc.perform(put("/api/v1/cartItems/cart/{cartId}/item/{itemId}/update", 1L, 200L)
                        .param("quantity", "3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart item not found"));
    }
}

