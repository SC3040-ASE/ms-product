package com.product.service.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.product.dto.product.ProductCreationRequestDTO;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.product.ProductOrderDTO;
import com.product.dto.product.ProductOrderRequestDTO;
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
    private final ProductOrderTriggerService productOrderTriggerService;

    public ResponseMessageDTO createProduct(String messageId, ProductCreationRequestDTO productCreationRequestDTO) throws Exception{

        ProductOrderRequestDTO productOrderRequestDTO = saveProductToDatabase(productCreationRequestDTO);

        productOrderTriggerService.triggerOrderRequest(productOrderRequestDTO);

        ObjectNode response = objectMapper.createObjectNode();
        response.put("productId", productOrderRequestDTO.getProductId());

        return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(response));
    }


    @Transactional
    public ProductOrderRequestDTO saveProductToDatabase(ProductCreationRequestDTO productCreationRequestDTO) throws Exception{
        Category category = categoryService.saveCategoryIfNotExists(productCreationRequestDTO.getCategory());
        List<Tag> tags = tagService.saveTagsIfNotExists(productCreationRequestDTO.getTags(), category);

        Product product = productMapper.mapToEntity(productCreationRequestDTO, tags, category);
        Product savedProduct = productRepository.saveAndFlush(product);

        if(productCreationRequestDTO.getImageBase64List() != null)
            pictureBlobStorageService.saveImages(savedProduct.getId(), productCreationRequestDTO.getImageBase64List());

        ProductOrderRequestDTO productOrderRequestDTO = new ProductOrderRequestDTO();
        productOrderRequestDTO.setCategoryId(savedProduct.getCategory().getId());
        productOrderRequestDTO.setCurrentQuantity(savedProduct.getCurrentQuantity());
        productOrderRequestDTO.setProductId(savedProduct.getId());
        productOrderRequestDTO.setOwnerId(savedProduct.getOwnerId());
        productOrderRequestDTO.setPrice(savedProduct.getPrice());
        productOrderRequestDTO.setTags(tags.stream().map(Tag::getId).toList());

        return productOrderRequestDTO;

    }
}
