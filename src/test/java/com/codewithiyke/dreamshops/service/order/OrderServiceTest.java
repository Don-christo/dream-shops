package com.codewithiyke.dreamshops.service.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.codewithiyke.dreamshops.dto.OrderDto;
import com.codewithiyke.dreamshops.enums.OrderStatus;
import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.*;
import com.codewithiyke.dreamshops.repository.OrderRepository;
import com.codewithiyke.dreamshops.repository.ProductRepository;
import com.codewithiyke.dreamshops.service.cart.CartService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock private OrderRepository orderRepository;
  @Mock private ProductRepository productRepository;
  @Mock private CartService cartService;
  @Mock private ModelMapper modelMapper;

  @InjectMocks private OrderService orderService;

  private Product testProduct;
  private Cart testCart;
  private CartItem testCartItem;
  private Order testOrder;

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

    testCartItem = new CartItem();
    testCartItem.setId(1L);
    testCartItem.setProduct(testProduct);
    testCartItem.setQuantity(2);
    testCartItem.setUnitPrice(testProduct.getPrice());
    testCartItem.setTotalPrice(testProduct.getPrice().multiply(BigDecimal.valueOf(2)));

    testCart = new Cart();
    testCart.setId(1L);
    testCart.setUser(testUser);
    testCart.setItems(new HashSet<>(Collections.singletonList(testCartItem)));
    testCart.setTotalAmount(BigDecimal.valueOf(1999.98));

    testOrder = new Order();
    testOrder.setOrderId(1L);
    testOrder.setUser(testUser);
    testOrder.setOrderDate(LocalDate.now());
    testOrder.setTotalAmount(BigDecimal.valueOf(1999.98));
    testOrder.setOrderStatus(OrderStatus.PENDING);
  }

  @Test
  void placeOrder_ShouldCreateOrder_WhenCartHasItems() {
    // Arrange
    when(cartService.getCartByUserId(1L)).thenReturn(testCart);
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(
            inv -> {
              Order o = inv.getArgument(0);
              o.setOrderId(1L);
              return o;
            });

    // Act
    Order result = orderService.placeOrder(1L);

    // Assert
    assertNotNull(result);
    assertEquals(OrderStatus.PENDING, result.getOrderStatus());
    assertEquals(0, result.getTotalAmount().compareTo(new BigDecimal("1999.98")));
    verify(orderRepository).save(any(Order.class));
    verify(cartService).clearCart(1L);

    // Verify inventory was reduced
    verify(productRepository).save(testProduct);
    assertEquals(8, testProduct.getInventory()); // 10 - 2
  }

  @Test
  void placeOrder_ShouldThrowException_WhenInsufficientInventory() {
    // Arrange
    testProduct.setInventory(1); // Less than required quantity
    testCartItem.setQuantity(2); // More than available inventory

    when(cartService.getCartByUserId(1L)).thenReturn(testCart);

    // Act & Assert
    assertThrows(IllegalStateException.class, () -> orderService.placeOrder(1L));

    verify(orderRepository, never()).save(any(Order.class));
    verify(cartService, never()).clearCart(1L);
    verify(productRepository, never()).save(any(Product.class));
  }

  @Test
  void getOrder_ShouldReturnOrder_WhenOrderExists() {
    // Arrange
    when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

    OrderDto expectedDto = new OrderDto();
    expectedDto.setId(testOrder.getOrderId());
    expectedDto.setTotalAmount(testOrder.getTotalAmount());

    when(modelMapper.map(testOrder, OrderDto.class)).thenReturn(expectedDto);

    // Act
    OrderDto result = orderService.getOrder(1L);

    // Assert
    assertNotNull(result);
    assertEquals(expectedDto.getId(), result.getId());
    assertEquals(0, expectedDto.getTotalAmount().compareTo(result.getTotalAmount()));
    verify(orderRepository).findById(1L);
    verify(modelMapper).map(testOrder, OrderDto.class);
  }

  @Test
  void getOrder_ShouldThrowException_WhenOrderNotFound() {
    // Arrange
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> orderService.getOrder(1L));
  }

  @Test
  void getUserOrders_ShouldReturnOrderList() {
    // Arrange
    List<Order> orders = Collections.singletonList(testOrder);
    when(orderRepository.findByUserId(1L)).thenReturn(orders);

    OrderDto expectedDto = new OrderDto();
    expectedDto.setId(testOrder.getOrderId());
    expectedDto.setTotalAmount(testOrder.getTotalAmount());

    when(modelMapper.map(testOrder, OrderDto.class)).thenReturn(expectedDto);

    // Act
    List<OrderDto> result = orderService.getUserOrders(1L);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(expectedDto.getId(), result.get(0).getId());
    assertEquals(0, expectedDto.getTotalAmount().compareTo(result.get(0).getTotalAmount()));

    verify(orderRepository).findByUserId(1L);
    verify(modelMapper).map(testOrder, OrderDto.class);
  }

  @Test
  void cancelOrder_ShouldCancelOrderAndRestoreInventory() {
    // Arrange
    testOrder.setOrderStatus(OrderStatus.PENDING);

    OrderItem orderItem = new OrderItem();
    orderItem.setProduct(testProduct);
    orderItem.setQuantity(2);
    orderItem.setPrice(testProduct.getPrice());

    testOrder.setOrderItems(new HashSet<>(List.of(orderItem)));
    testProduct.setInventory(10); // initial inventory

    when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(productRepository.save(any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Order result = orderService.cancelOrder(1L);

    // Assert
    assertNotNull(result);
    assertEquals(OrderStatus.CANCELLED, result.getOrderStatus());
    verify(productRepository).save(testProduct);
    assertEquals(12, testProduct.getInventory()); // 10 + 2 restored
  }

  @Test
  void cancelOrder_ShouldThrowException_WhenOrderAlreadyShipped() {
    // Arrange
    testOrder.setOrderStatus(OrderStatus.DELIVERED);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

    // Act & Assert
    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(1L));

    assertEquals("Order cannot be cancelled as it is already processed.", exception.getMessage());
    verify(orderRepository, never()).save(any(Order.class));
    verify(productRepository, never()).save(any(Product.class));
  }
}
