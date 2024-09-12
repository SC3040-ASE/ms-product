package com.product.service.product;

import com.product.dto.product.ProductCreationRequestDTO;
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
        Category category = categoryService.saveCategoryIfNotExists(productCreationRequestDTO.getCategory());
        List<Tag> tags = tagService.saveTagsIfNotExists(productCreationRequestDTO.getTags(), category);

        Product product = productMapper.mapToEntity(productCreationRequestDTO, tags, category);
        Product savedProduct = productRepository.saveAndFlush(product);

        pictureBlobStorageService.saveImages(savedProduct.getId(), productCreationRequestDTO.getImageBase64List());

        return new ResponseMessageDTO(messageId, 200, "Product created successfully");
    }
}
