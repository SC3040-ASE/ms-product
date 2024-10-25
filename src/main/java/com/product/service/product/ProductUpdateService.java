package com.product.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.product.ProductOrderRequestDTO;
import com.product.dto.product.ProductUpdateRequestDTO;
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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

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
    private final ProductOrderTriggerService productOrderTriggerService;

    @Transactional
    public void updateProductQuantity(Integer productId, Integer quantityDiff) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            if(product.get().getCurrentQuantity() + quantityDiff < 0){
                log.error("Product quantity cannot be negative");
                return;
            }
            product.get().setCurrentQuantity(product.get().getCurrentQuantity()+quantityDiff);
            productRepository.save(product.get());
        }
    }

    public ResponseMessageDTO updateProduct(String messageId, ProductUpdateRequestDTO productUpdateRequestDTO) throws JsonProcessingException {

        Pair<ProductOrderRequestDTO,Integer> response = updateToDatabase(productUpdateRequestDTO);
        Integer status = response.getRight();

        if (status == 200) {
            productOrderTriggerService.triggerOrderRequest(response.getKey());
            return new ResponseMessageDTO(messageId, 200, "Product updated successfully");
        } else if (status == 403) {
            return new ResponseMessageDTO(messageId, 403, "Unauthorized to update product");
        } else {
            return new ResponseMessageDTO(messageId, 404, "Product not found");
        }

    }


    @Transactional
    public Pair<ProductOrderRequestDTO,Integer> updateToDatabase(ProductUpdateRequestDTO productUpdateRequestDTO) throws JsonProcessingException {
        Optional<Product> product = productRepository.findById(productUpdateRequestDTO.getProductId());




        if (product.isPresent()) {
            
            if (!product.get().getOwnerId().equals(productUpdateRequestDTO.getOwnerId()) && !productUpdateRequestDTO.getIsAdmin()) {
                return Pair.of(null, 403);
            }

            Category category = categoryService.saveCategoryIfNotExists(productUpdateRequestDTO.getCategory());
            List<Tag> tags = tagService.saveTagsIfNotExists(productUpdateRequestDTO.getTags(), category);

            Product updatedProduct = productMapper.updateProductFromDTO(product.get(), productUpdateRequestDTO, tags, category);
            Product savedProduct = productRepository.saveAndFlush(updatedProduct);

            pictureBlobStorageService.updateProductImages(savedProduct.getId(), productUpdateRequestDTO.getDeleteImageList(), productUpdateRequestDTO.getNewImageBase64List());

            ProductOrderRequestDTO productOrderRequestDTO = new ProductOrderRequestDTO();
            productOrderRequestDTO.setCategoryId(savedProduct.getCategory().getId());
            productOrderRequestDTO.setCurrentQuantity(savedProduct.getCurrentQuantity());
            productOrderRequestDTO.setProductId(savedProduct.getId());
            productOrderRequestDTO.setOwnerId(savedProduct.getOwnerId());
            productOrderRequestDTO.setPrice(savedProduct.getPrice());
            productOrderRequestDTO.setTags(tags.stream().map(Tag::getId).toList());


            return Pair.of(productOrderRequestDTO, 200);
        } else {
            return Pair.of(null, 404);
        }

    }
}
