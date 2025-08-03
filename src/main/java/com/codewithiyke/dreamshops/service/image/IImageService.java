package com.codewithiyke.dreamshops.service.image;

import com.codewithiyke.dreamshops.dto.ImageDto;
import com.codewithiyke.dreamshops.model.Image;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
  Image getImageById(Long id);

  void deleteImageById(Long id);

  List<ImageDto> saveImages(List<MultipartFile> files, Long productId);

  void updateImage(MultipartFile file, Long productId);
}
