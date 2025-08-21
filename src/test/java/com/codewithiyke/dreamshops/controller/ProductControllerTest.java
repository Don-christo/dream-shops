package com.codewithiyke.dreamshops.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.codewithiyke.dreamshops.dto.ProductDto;
import com.codewithiyke.dreamshops.model.Category;
import com.codewithiyke.dreamshops.model.Product;
import com.codewithiyke.dreamshops.request.AddProductRequest;
import com.codewithiyke.dreamshops.security.jwt.JwtUtils;
import com.codewithiyke.dreamshops.security.user.ShopUserDetailsService;
import com.codewithiyke.dreamshops.service.product.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {
  @Autowired private MockMvc mockMvc;
  @MockitoBean private ProductService productService;
  @MockitoBean private JwtUtils jwtUtils;
  @MockitoBean private ShopUserDetailsService userDetailsService;
  @Autowired private ObjectMapper objectMapper;

  private Product testProduct;
  private ProductDto testProductDto;

  @BeforeEach
  void setup() {
    Category testCategory = new Category();
    testCategory.setId(1L);
    testCategory.setName("Electronics");

    testProduct = new Product();
    testProduct.setId(1L);
    testProduct.setName("iPhone 14");
    testProduct.setBrand("Apple");
    testProduct.setPrice(BigDecimal.valueOf(999.99));
    testProduct.setInventory(10);
    testProduct.setDescription("Latest iPhone");
    testProduct.setCategory(testCategory);

    AddProductRequest addProductRequest = new AddProductRequest();
    addProductRequest.setName("iPhone 14");
    addProductRequest.setBrand("Apple");
    addProductRequest.setPrice(BigDecimal.valueOf(999.99));
    addProductRequest.setInventory(10);
    addProductRequest.setDescription("Latest iPhone");
    addProductRequest.setCategory(testCategory);

    testProductDto = new ProductDto();
    testProductDto.setId(1L);
    testProductDto.setName("iPhone 14");
    testProductDto.setBrand("Apple");
    testProductDto.setPrice(BigDecimal.valueOf(999.99));
    testProductDto.setInventory(10);
    testProductDto.setDescription("Latest iPhone");
    testProductDto.setCategory(testCategory);
  }

  @Test
  void getAllProducts_ShouldReturnProductList() throws Exception {
    //      Arrange
    List<Product> products = Collections.singletonList(testProduct);
    List<ProductDto> productDtos = Collections.singletonList(testProductDto);

    when(productService.getAllProducts()).thenReturn(products);
    when(productService.getConvertedProducts(products)).thenReturn(productDtos);

    //      Act & Assert
    mockMvc
        .perform(get("/api/v1/products/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("success"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].name").value("iPhone 14"))
        .andExpect(jsonPath("$.data[0].brand").value("Apple"));

    verify(productService).getAllProducts();
    verify(productService).getConvertedProducts(products);
  }

  @Test
  void getProductById_ShouldReturnProduct_WhenProductExists() throws Exception {
    // Arrange
    when(productService.getProductById(1L)).thenReturn(testProduct);
    when(productService.convertToDto(testProduct)).thenReturn(testProductDto);

    // Act & Assert

    mockMvc
        .perform(get("/api/v1/products/product/{productId}/product", 1L))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("success"))
        .andExpect(jsonPath("$.data.name").value("iPhone 14"))
        .andExpect(jsonPath("$.data.brand").value("Apple"));
  }
}
