package com.projects.dreamShops.service.image;

import com.projects.dreamShops.dto.ImageDto;
import com.projects.dreamShops.exceptions.ResourceNotFoundException;
import com.projects.dreamShops.model.Image;
import com.projects.dreamShops.model.Product;
import com.projects.dreamShops.repository.ImageRepository;
import com.projects.dreamShops.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService{


    private final ImageRepository imageRepository;

    private final ProductService productService;

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Image not found..!"));
    }

    @Override
    public void deleteImageById(Long id) {
            imageRepository.findById(id).ifPresentOrElse(imageRepository::delete,()->{
                throw new ResourceNotFoundException("Image not exits with id : "+ id);
            });
    }

    @Override
    public List<ImageDto> saveImage(List<MultipartFile> files, Long productId) {
        Product product = productService.getProductById(productId);
        List<ImageDto> savedImageDtos = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProducts(product);

                // Save first to generate ID
                Image savedImage = imageRepository.save(image);

                // Now build the download URL using the generated ID
                String downloadUrl = "/api/v1/images/image/download/" + savedImage.getId();
                savedImage.setDownloadUrl(downloadUrl);

                // Save again with the correct download URL
                savedImage = imageRepository.save(savedImage);

                // Convert to DTO
                ImageDto imageDto = new ImageDto();
                imageDto.setId(savedImage.getId());
                imageDto.setFileName(savedImage.getFileName());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());

                savedImageDtos.add(imageDto);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return savedImageDtos;
    }


    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        try {
            Image image = getImageById(imageId);
            image.setFileName(file.getOriginalFilename());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
