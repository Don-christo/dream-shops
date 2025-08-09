package com.codewithiyke.dreamshops.controller;

import static org.springframework.http.HttpStatus.*;

import com.codewithiyke.dreamshops.dto.ProductDto;
import com.codewithiyke.dreamshops.exceptions.AlreadyExistsException;
import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Product;
import com.codewithiyke.dreamshops.request.AddProductRequest;
import com.codewithiyke.dreamshops.request.ProductUpdateRequest;
import com.codewithiyke.dreamshops.response.ApiResponse;
import com.codewithiyke.dreamshops.service.product.IProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
  private final IProductService productService;

  @Transactional(readOnly = true)
  @GetMapping("/all")
  public ResponseEntity<ApiResponse> getAllProducts() {
    List<Product> products = productService.getAllProducts();
    List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
    return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
  }

  @Transactional(readOnly = true)
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
      ProductDto productDto = productService.convertToDto(theProduct);
      return ResponseEntity.ok(new ApiResponse("Add product success", productDto));
    } catch (AlreadyExistsException e) {
      return ResponseEntity.status(CONFLICT)
          .body(new ApiResponse(e.getMessage(), null));
    }
  }

  @PutMapping("/product/{productId}/update")
  public ResponseEntity<ApiResponse> updateProduct(
      @RequestBody ProductUpdateRequest request, @PathVariable Long productId) {
    try {
      Product theProduct = productService.updateProduct(request, productId);
      ProductDto productDto = productService.convertToDto(theProduct);
      return ResponseEntity.ok(new ApiResponse("Update product success", productDto));
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

  @Transactional(readOnly = true)
  @GetMapping("/product/by/brand-and-name")
  public ResponseEntity<ApiResponse> getProductByBrandAndName(
      @RequestParam String brandName, @RequestParam String productName) {
    try {
      List<Product> products = productService.getProductsByBrandAndName(brandName, productName);
      if (products.isEmpty()) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
      }
      List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
      return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    } catch (Exception e) {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(e.getMessage(), null));
    }
  }

  @Transactional(readOnly = true)
  @GetMapping("/products/by/brand-and-name")
  public ResponseEntity<ApiResponse> getProductByCategoryAndBrand(
      @RequestParam String category, @RequestParam String brand) {
    try {
      List<Product> products = productService.getProductsByCategoryAndBrand(category, brand);
      if (products.isEmpty()) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
      }
      List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
      return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    } catch (Exception e) {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(e.getMessage(), null));
    }
  }

  @Transactional(readOnly = true)
  @GetMapping("/products/{name}/products")
  public ResponseEntity<ApiResponse> getProductByName(@PathVariable String name) {
    try {
      List<Product> products = productService.getProductsByName(name);
      if (products.isEmpty()) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
      }
      List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
      return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    } catch (Exception e) {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(e.getMessage(), null));
    }
  }

  @Transactional(readOnly = true)
  @GetMapping("/product/by-brand")
  public ResponseEntity<ApiResponse> findProductByBrand(@RequestParam String brand) {
    try {
      List<Product> products = productService.getProductsByBrand(brand);
      if (products.isEmpty()) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
      }
      List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
      return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    } catch (Exception e) {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(e.getMessage(), null));
    }
  }

  @Transactional(readOnly = true)
  @GetMapping("/product/{category}/all/products")
  public ResponseEntity<ApiResponse> findProductByCategory(@PathVariable String category) {
    try {
      List<Product> products = productService.getAllProductsByCategory(category);
      if (products.isEmpty()) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No product found", null));
      }
      List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
      return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
    } catch (Exception e) {
      return ResponseEntity.status(INTERNAL_SERVER_ERROR)
          .body(new ApiResponse(e.getMessage(), null));
    }
  }

  @Transactional(readOnly = true)
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
