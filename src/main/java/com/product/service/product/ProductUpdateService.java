package com.product.service.product;

import com.product.dto.ResponseMessageDTO;
import com.product.dto.ProductUpdateRequestDTO;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.Tag;
import com.product.mapper.ProductMapper;
import com.product.repository.ProductRepository;
import com.product.service.category.CategoryService;
import com.product.service.blob.PictureBlobStorageService;
import com.product.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductUpdateService {

    private final ProductRepository productRepository;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;
    private final PictureBlobStorageService pictureBlobStorageService;

    @Transactional
    public ResponseMessageDTO updateProduct(String messageId, ProductUpdateRequestDTO productUpdateRequestDTO) {
        Optional<Product> product = productRepository.findById(productUpdateRequestDTO.getId());

        if (product.isPresent()) {
            if (!product.get().getOwnerId().equals(productUpdateRequestDTO.getOwnerId())) {
                return new ResponseMessageDTO(messageId, 403, "Unauthorized");
            }

            Category category = categoryService.saveCategoryIfNotExists(productUpdateRequestDTO.getCategory());
            List<Tag> tags = tagService.saveTagsIfNotExists(productUpdateRequestDTO.getTags(), category);

            Product updatedProduct = productMapper.updateProductFromDTO(product.get(), productUpdateRequestDTO, tags, category);
            Product savedProduct = productRepository.saveAndFlush(updatedProduct);

            pictureBlobStorageService.updateProductImages(savedProduct.getId(), productUpdateRequestDTO.getDeleteImageList(), productUpdateRequestDTO.getNewImageBase64List());

            return new ResponseMessageDTO(messageId, 200, "Product updated successfully");
        } else {
            return new ResponseMessageDTO(messageId, 404, "Product not found");
        }

    }
}
