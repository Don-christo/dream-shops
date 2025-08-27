package com.codewithiyke.dreamshops.mapper;

import com.codewithiyke.dreamshops.dto.CartDto;
import com.codewithiyke.dreamshops.dto.CartItemDto;
import com.codewithiyke.dreamshops.dto.ImageDto;
import com.codewithiyke.dreamshops.dto.ProductDto;
import com.codewithiyke.dreamshops.model.Cart;
import com.codewithiyke.dreamshops.model.CartItem;
import com.codewithiyke.dreamshops.model.Image;
import com.codewithiyke.dreamshops.model.Product;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CartMapper {

  public static CartDto toDto(Cart cart) {
    if (cart == null) {
      return null;
    }

    CartDto dto = new CartDto();
    dto.setCartId(cart.getId());
    dto.setTotalAmount(cart.getTotalAmount());

    Set<CartItemDto> cartItemDtos =
        cart.getItems().stream().map(CartMapper::toCartItemDto).collect(Collectors.toSet());
    dto.setItems(cartItemDtos);

    return dto;
  }

  private static CartItemDto toCartItemDto(CartItem cartItem) {
    CartItemDto dto = new CartItemDto();
    dto.setId(cartItem.getId());
    dto.setQuantity(cartItem.getQuantity());
    dto.setUnitPrice(cartItem.getUnitPrice());
    dto.setProduct(toProductDto(cartItem.getProduct()));
    return dto;
  }

  private static ProductDto toProductDto(Product product) {
    if (product == null) {
      return null;
    }

    ProductDto dto = new ProductDto();
    dto.setId(product.getId());
    dto.setName(product.getName());
    dto.setBrand(product.getBrand());
    dto.setPrice(product.getPrice());
    dto.setInventory(product.getInventory());
    dto.setDescription(product.getDescription());
    dto.setCategory(product.getCategory());

    List<ImageDto> imageDtos =
        product.getImages().stream().map(CartMapper::toImageDto).collect(Collectors.toList());
    dto.setImages(imageDtos);

    return dto;
  }

  private static ImageDto toImageDto(Image image) {
    ImageDto dto = new ImageDto();
    dto.setId(image.getId());
    dto.setFileName(image.getFileName());
    dto.setDownloadUrl(image.getDownloadUrl());
    return dto;
  }
}
