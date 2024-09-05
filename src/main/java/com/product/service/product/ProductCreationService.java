package com.product.service.product;

import com.product.dto.ProductCreationRequestDTO;
import com.product.dto.ResponseMessageDTO;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.Tag;
import com.product.mapper.ProductMapper;
import com.product.repository.ProductRepository;
import com.product.service.category.CategoryService;
import com.product.service.blob.PictureBlobStorageService;
import com.product.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCreationService {

    private final ProductRepository productRepository;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;
    private final PictureBlobStorageService pictureBlobStorageService;

    @Transactional
    public ResponseMessageDTO createProduct(String messageId, ProductCreationRequestDTO productCreationRequestDTO) {
        try {
            Category category = categoryService.saveCategoryIfNotExists(productCreationRequestDTO.getCategory());
            List<Tag> tags = tagService.saveTagsIfNotExists(productCreationRequestDTO.getTags(), category);

            Product product = productMapper.mapToEntity(productCreationRequestDTO, tags, category);
            Product savedProduct = productRepository.saveAndFlush(product);

            String imageFilename = "product_" + savedProduct.getId() + ".jpg";
            byte[] imageBytes = Base64.getDecoder().decode(productCreationRequestDTO.getImageBase64());
            pictureBlobStorageService.saveImage(imageBytes, imageFilename);

            savedProduct.setProductImage(imageFilename);
            productRepository.save(savedProduct);

            return new ResponseMessageDTO(messageId, 200, savedProduct);
        } catch (Exception e) {
            return new ResponseMessageDTO(messageId, 500, "Error creating product");
        }
    }
}
