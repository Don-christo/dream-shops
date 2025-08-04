package com.codewithiyke.dreamshops.service.image;

import com.codewithiyke.dreamshops.dto.ImageDto;
import com.codewithiyke.dreamshops.exceptions.ResourceNotFoundException;
import com.codewithiyke.dreamshops.model.Image;
import com.codewithiyke.dreamshops.model.Product;
import com.codewithiyke.dreamshops.repository.ImageRepository;
import com.codewithiyke.dreamshops.service.product.IProductService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {
  private final ImageRepository imageRepository;
  private final IProductService productService;

  @Override
  public Image getImageById(Long id) {
    return imageRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No image found with this id: " + id));
  }

  @Override
  public void deleteImageById(Long id) {
    imageRepository
        .findById(id)
        .ifPresentOrElse(
            imageRepository::delete,
            () -> {
              throw new ResourceNotFoundException("No image found with this id: " + id);
            });
  }

  @Override
  public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
    Product product = productService.getProductById(productId);
    List<ImageDto> savedImageDto = new ArrayList<>();
    for (MultipartFile file : files) {
      try {
        Image image = new Image();
        image.setFileName(file.getOriginalFilename());
        image.setFileType(file.getContentType());
        image.setImage(new SerialBlob(file.getBytes()));

        String buildDownloadUrl = "/api/v1/images/image/download/";
        String downloadUrl = buildDownloadUrl + image.getId();
        image.setDownloadUrl(downloadUrl);
        image.setProduct(product);
        Image savedImage = imageRepository.save(image);

        savedImage.setDownloadUrl(buildDownloadUrl + savedImage.getId());
        imageRepository.save(savedImage);

        ImageDto imageDto = new ImageDto();
        imageDto.setId(savedImage.getId());
        imageDto.setFileName(savedImage.getFileName());
        imageDto.setDownloadUrl(savedImage.getDownloadUrl());
        savedImageDto.add(imageDto);
      } catch (IOException | SQLException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return savedImageDto;
  }

  @Override
  public void updateImage(MultipartFile file, Long productId) {
    Image image = getImageById(productId);
    try {
      image.setFileName(file.getOriginalFilename());
      //      image.setFileName(file.getOriginalFilename());
      image.setImage(new SerialBlob(file.getBytes()));
      imageRepository.save(image);
    } catch (IOException | SQLException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
}
