package com.codewithiyke.dreamshops.controller;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.codewithiyke.dreamshops.dto.ProductDto;
import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Product;
import com.codewithiyke.dreamshops.request.AddProductRequest;
import com.codewithiyke.dreamshops.request.ProductUpdateRequest;
import com.codewithiyke.dreamshops.response.ApiResponse;
import com.codewithiyke.dreamshops.service.product.IProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
  private final IProductService productService;

  @GetMapping("/all")
  public ResponseEntity<ApiResponse> getAllProducts() {
    List<Product> products = productService.getAllProducts();
    List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
    return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
  }

  @GetMapping("/product/{productId}/product")
  public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
    try {
      Product product = productService.getProductById(productId);
      ProductDto productDto = productService.convertToDto(product);
      return ResponseEntity.ok(new ApiResponse("success", productDto));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    }
  }

  @PostMapping("/add")
  public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest product) {
    try {
      Product theProduct = productService.addProduct(product);
      return ResponseEntity.ok(new ApiResponse("Add product success", theProduct));
    } catch (Exception e) {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(e.getMessage(), null));
    }
  }

  @PutMapping("/product/{productId}/update")
  public ResponseEntity<ApiResponse> updateProduct(
      @RequestBody ProductUpdateRequest request, @PathVariable Long productId) {
    try {
      Product theProduct = productService.updateProduct(request, productId);
      return ResponseEntity.ok(new ApiResponse("Update product success", theProduct));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    }
  }

  @DeleteMapping("/product/{productId}/delete")
  public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
    try {
      productService.deleteProductById(productId);
      return ResponseEntity.ok(new ApiResponse("Delete product success", productId));
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
    }
  }

  @GetMapping("/product/by/brand-and-name")
  public ResponseEntity<ApiResponse> getProductByBrandAndName(
      @RequestParam String brandName, @RequestParam String productName) {
    try {
      List<Product> products = productService.getProductsByBrandAndName(brandName, productName);
      List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
      if (products.isEmpty()) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
      }
      return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    } catch (Exception e) {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(e.getMessage(), null));
    }
  }

  @GetMapping("/products/by/brand-and-name")
  public ResponseEntity<ApiResponse> getProductByCategoryAndBrand(
      @RequestParam String category, @RequestParam String brand) {
    try {
      List<Product> products = productService.getProductsByCategoryAndBrand(category, brand);
      List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
      if (products.isEmpty()) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
      }
      return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    } catch (Exception e) {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(e.getMessage(), null));
    }
  }

  @GetMapping("/products/{name}/products")
  public ResponseEntity<ApiResponse> getProductByName(@PathVariable String name) {
    try {
      List<Product> products = productService.getProductsByName(name);
      List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
      if (products.isEmpty()) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
      }
      return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    } catch (Exception e) {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(e.getMessage(), null));
    }
  }

  @GetMapping("/product/by-brand")
  public ResponseEntity<ApiResponse> findProductByBrand(@RequestParam String brand) {
    try {
      List<Product> products = productService.getProductsByBrand(brand);
      List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
      if (products.isEmpty()) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
      }
      return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    } catch (Exception e) {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(e.getMessage(), null));
    }
  }

  @GetMapping("/product/{category}/all/products")
  public ResponseEntity<ApiResponse> findProductByCategory(@PathVariable String category) {
    try {
      List<Product> products = productService.getAllProductsByCategory(category);
      List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
      if (products.isEmpty()) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
      }
      return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    } catch (Exception e) {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(e.getMessage(), null));
    }
  }

  @GetMapping("/product/count/by-brand/and-name")
  public ResponseEntity<ApiResponse> countProductByBrandAndName(
      @RequestParam String brand, @RequestParam String name) {
    try {
      var productCount = productService.countProductsByBrandAndName(brand, name);
      return ResponseEntity.ok(new ApiResponse("success", productCount));
    } catch (Exception e) {
      return ResponseEntity.ok(new ApiResponse(e.getMessage(), null));
    }
  }
}
