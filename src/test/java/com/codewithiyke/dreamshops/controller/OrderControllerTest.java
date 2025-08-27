package com.codewithiyke.dreamshops.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.codewithiyke.dreamshops.dto.OrderDto;
import com.codewithiyke.dreamshops.model.Order;
import com.codewithiyke.dreamshops.security.jwt.JwtUtils;
import com.codewithiyke.dreamshops.security.user.ShopUserDetailsService;
import com.codewithiyke.dreamshops.service.order.IOrderService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private IOrderService orderService;
  @MockitoBean private JwtUtils jwtUtils;
  @MockitoBean private ShopUserDetailsService userDetailsService;

  private Order testOrder;
  private OrderDto testOrderDto;

  @BeforeEach
  void setUp() {
    testOrder = new Order();
    testOrder.setOrderId(1L);

    testOrderDto = new OrderDto();
    testOrderDto.setOrderId(1L);
  }

  @Test
  void createOrder_ShouldReturnSuccess_WhenOrderPlaced() throws Exception {
    when(orderService.placeOrder(1L)).thenReturn(testOrder);
    when(orderService.convertToDto(testOrder)).thenReturn(testOrderDto);

    mockMvc
        .perform(post("/api/v1/orders/order").param("userId", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Item Order Success!"))
        .andExpect(jsonPath("$.data.orderId").value(1L));

    verify(orderService).placeOrder(1L);
    verify(orderService).convertToDto(testOrder);
  }

  @Test
  void createOrder_ShouldReturnInternalServerError_WhenExceptionThrown() throws Exception {
    when(orderService.placeOrder(anyLong())).thenThrow(new RuntimeException("Database error"));

    mockMvc
        .perform(post("/api/v1/orders/order").param("userId", "1"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Error Occurred!"))
        .andExpect(jsonPath("$.data").value("Database error"));

    verify(orderService).placeOrder(1L);
  }

  @Test
  void getOrderById_ShouldReturnSuccess_WhenOrderExists() throws Exception {
    when(orderService.getOrder(1L)).thenReturn(testOrderDto);

    mockMvc
        .perform(get("/api/v1/orders/{orderId}/order", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Item Order Success!"))
        .andExpect(jsonPath("$.data.orderId").value(1L));

    verify(orderService).getOrder(1L);
  }

  @Test
  void getOrderById_ShouldReturnNotFound_WhenOrderDoesNotExist() throws Exception {
    when(orderService.getOrder(1L)).thenThrow(new RuntimeException("Order not found"));

    mockMvc
        .perform(get("/api/v1/orders/{orderId}/order", 1L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Oops!"))
        .andExpect(jsonPath("$.data").value("Order not found"));

    verify(orderService).getOrder(1L);
  }

  @Test
  void getUserOrders_ShouldReturnSuccess_WhenOrdersExist() throws Exception {
    when(orderService.getUserOrders(1L)).thenReturn(List.of(testOrderDto));

    mockMvc
        .perform(get("/api/v1/orders/{userId}/orders", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Item Order Success!"))
        .andExpect(jsonPath("$.data[0].orderId").value(1L));

    verify(orderService).getUserOrders(1L);
  }

  @Test
  void getUserOrders_ShouldReturnNotFound_WhenOrdersDoNotExist() throws Exception {
    when(orderService.getUserOrders(1L)).thenThrow(new RuntimeException("No orders found"));

    mockMvc
        .perform(get("/api/v1/orders/{userId}/orders", 1L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Oops!"))
        .andExpect(jsonPath("$.data").value("No orders found"));

    verify(orderService).getUserOrders(1L);
  }
}
