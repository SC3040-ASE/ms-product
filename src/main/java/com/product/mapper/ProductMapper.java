package com.product.mapper;

import com.product.dto.image.ImageDTO;
import com.product.dto.product.*;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.Tag;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductMapper {

    public Product mapToEntity(ProductCreationRequestDTO productCreationRequestDTO, List<Tag> existingTags, Category category) {
        Product product = new Product();
        product.setOwnerId(productCreationRequestDTO.getOwnerId());
        product.setProductName(productCreationRequestDTO.getProductName());
        product.setPrice(productCreationRequestDTO.getPrice());
        product.setTags(existingTags);
        product.setCondition(productCreationRequestDTO.getCondition());
        product.setTotalQuantity(productCreationRequestDTO.getTotalQuantity());
        product.setCurrentQuantity(productCreationRequestDTO.getTotalQuantity()); // Assuming current quantity is same as total initially
        product.setCategory(category);
        product.setDescription(productCreationRequestDTO.getDescription());

        return product;
    }

    public Product updateProductFromDTO(Product product, ProductUpdateRequestDTO productUpdateRequestDTO, List<Tag> existingTags, Category category) {
        product.setId(productUpdateRequestDTO.getProductId());
        product.setOwnerId(productUpdateRequestDTO.getOwnerId());
        product.setProductName(productUpdateRequestDTO.getProductName());
        product.setPrice(productUpdateRequestDTO.getPrice());
        product.setTags(existingTags);
        product.setCondition(productUpdateRequestDTO.getCondition());
        product.setTotalQuantity(productUpdateRequestDTO.getTotalQuantity());
        product.setCurrentQuantity(productUpdateRequestDTO.getCurrentQuantity());
        product.setCategory(category);
        product.setDescription(productUpdateRequestDTO.getDescription());

        return product;
    }

    public Pair<List<ProductSearchResultDTO>,Integer> mapToSearchResults(List<Object[]> results) {
        List<ProductSearchResultDTO> searchResults = new ArrayList<>();
        int totalResults = 0;
        if(results.size()>0){
            totalResults = (Integer) results.get(0)[9];
        }

        for (Object[] row : results) {
            ProductSearchResultDTO result = new ProductSearchResultDTO();
            result.setProductId((Integer) row[0]);
            result.setOwnerId((Integer) row[1]);
            result.setOwnerUsername((String) row[2]);
            result.setProductName((String) row[3]);
            result.setPrice((BigDecimal) row[4]);
            result.setCondition((String) row[5]);
            result.setCurrentQuantity((Integer) row[6]);
            result.setCreatedOn((java.sql.Timestamp) row[7]);
            result.setScore((Float) row[8]);
            searchResults.add(result);
        }


        return Pair.of(searchResults, totalResults);
    }

    public ProductReadResponseDTO mapToProductReadResponse(Product product, List<ImageDTO> images) {
        ProductReadResponseDTO productReadResponseDTO = new ProductReadResponseDTO();
        productReadResponseDTO.setProductId(product.getId());
        productReadResponseDTO.setOwnerId(product.getOwnerId());
        productReadResponseDTO.setProductName(product.getProductName());
        productReadResponseDTO.setPrice(product.getPrice());
        List<String> tags = new ArrayList<>();
        for (Tag tag : product.getTags()) {
            tags.add(tag.getTagName());
        }
        productReadResponseDTO.setTags(tags);
        productReadResponseDTO.setCondition(product.getCondition());
        productReadResponseDTO.setImages(images);
        productReadResponseDTO.setTotalQuantity(product.getTotalQuantity());
        productReadResponseDTO.setCreatedOn(product.getCreatedOn());
        productReadResponseDTO.setCategoryName(product.getCategory().getCategoryName());
        productReadResponseDTO.setDescription(product.getDescription());

        return productReadResponseDTO;
    }


    public List<ProductReadPreviewDTO> mapToReadPreviewResults(List<Product> products){
        List<ProductReadPreviewDTO> productReadPreviewResponseDTOs = new ArrayList<>();
        for (Product product : products) {
            ProductReadPreviewDTO productReadPreviewDTO = new ProductReadPreviewDTO();
            productReadPreviewDTO.setProductId(product.getId());
            productReadPreviewDTO.setOwnerId(product.getOwnerId());
            productReadPreviewDTO.setProductName(product.getProductName());
            productReadPreviewDTO.setPrice(product.getPrice());
            productReadPreviewDTO.setCondition(product.getCondition());
            productReadPreviewDTO.setCurrentQuantity(product.getCurrentQuantity());
            productReadPreviewDTO.setCreatedOn(product.getCreatedOn());
            productReadPreviewResponseDTOs.add(productReadPreviewDTO);
        }
        return productReadPreviewResponseDTOs;
    }
}
