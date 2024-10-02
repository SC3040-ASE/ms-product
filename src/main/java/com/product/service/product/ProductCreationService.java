package com.product.service.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseMessageDTO createProduct(String messageId, ProductCreationRequestDTO productCreationRequestDTO) throws Exception{
        Category category = categoryService.saveCategoryIfNotExists(productCreationRequestDTO.getCategory());
        List<Tag> tags = tagService.saveTagsIfNotExists(productCreationRequestDTO.getTags(), category);

        Product product = productMapper.mapToEntity(productCreationRequestDTO, tags, category);
        Product savedProduct = productRepository.saveAndFlush(product);

        if(productCreationRequestDTO.getImageBase64List() != null)
            pictureBlobStorageService.saveImages(savedProduct.getId(), productCreationRequestDTO.getImageBase64List());

        ObjectNode response = objectMapper.createObjectNode();
        response.put("productId", savedProduct.getId());
        return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(response));
    }
}
