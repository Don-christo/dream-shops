package com.codewithiyke.dreamshops.controller;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Cart;
import com.codewithiyke.dreamshops.model.User;
import com.codewithiyke.dreamshops.response.ApiResponse;
import com.codewithiyke.dreamshops.service.cart.ICartItemService;
import com.codewithiyke.dreamshops.service.cart.ICartService;
import com.codewithiyke.dreamshops.service.user.IUserService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/cartItems")
public class CartItemController {
  private final ICartItemService cartItemService;
  private final ICartService cartService;
  private final IUserService userService;

  @PostMapping("/item/add")
  public ResponseEntity<ApiResponse> addItemToCart(
      @RequestParam Long productId, @RequestParam Integer quantity) {
    try {
      User user = userService.getAuthenticatedUser();
      Cart cart = cartService.initializeNewCart(user);

      cartItemService.addItemToCart(cart.getId(), productId, quantity);
      return ResponseEntity.ok(new ApiResponse("Add Item Success", null));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    } catch (JwtException e) {
      return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
    }
  }

  @DeleteMapping("/cart/{cartId}/item/{itemId}/remove")
  public ResponseEntity<ApiResponse> removeItemFromCart(
      @PathVariable Long cartId, @PathVariable Long itemId) {
    try {
      cartItemService.removeItemFromCart(cartId, itemId);
      return ResponseEntity.ok(new ApiResponse("Remove Item Success", null));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    }
  }

  @PutMapping("/cart/{cartId}/item/{itemId}/update")
  public ResponseEntity<ApiResponse> updateItemQuantity(
      @PathVariable Long cartId, @PathVariable Long itemId, @RequestParam Integer quantity) {
    try {
      cartItemService.updateQuantity(cartId, itemId, quantity);
      return ResponseEntity.ok(new ApiResponse("Update Item Success", null));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    }
  }
}
