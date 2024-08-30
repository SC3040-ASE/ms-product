package com.product.mapper;

import com.product.dto.ProductCreationRequestDTO;
import com.product.dto.ProductReadResponseDTO;
import com.product.dto.ProductSearchResponseDTO;
import com.product.dto.ProductUpdateRequestDTO;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.Tag;
import org.springframework.stereotype.Component;

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
        product.setId(productUpdateRequestDTO.getId());
        product.setOwnerId(productUpdateRequestDTO.getOwnerId());
        product.setProductName(productUpdateRequestDTO.getProductName());
        product.setPrice(productUpdateRequestDTO.getPrice());
        product.setTags(existingTags);
        product.setCondition(productUpdateRequestDTO.getCondition());
        product.setTotalQuantity(productUpdateRequestDTO.getTotalQuantity());
        product.setCurrentQuantity(productUpdateRequestDTO.getTotalQuantity()); // Assuming current quantity is same as total initially
        product.setCategory(category);
        product.setDescription(productUpdateRequestDTO.getDescription());

        return product;
    }

    public List<ProductSearchResponseDTO> mapToSearchResults(List<Object[]> results) {

        List<ProductSearchResponseDTO> searchResults = new ArrayList<>();
        for (Object[] row : results) {
            ProductSearchResponseDTO result = new ProductSearchResponseDTO();
            result.setId((Integer) row[0]);
            result.setOwnerId((Integer) row[1]);
            result.setProductName((String) row[2]);
            result.setPrice((BigDecimal) row[3]);
            result.setTags((String[]) row[4]);
            result.setCondition((String) row[5]);
            result.setProductImage((String) row[6]);
            result.setTotalQuantity((Integer) row[7]);
            result.setCurrentQuantity((Integer) row[8]);
            result.setCategoryName((String) row[9]);
            result.setDescription((String) row[10]);
            result.setScore((Float) row[11]);

            searchResults.add(result);
        }

        return searchResults;
    }

    public ProductReadResponseDTO mapToProductReadResponse(Product product, String base64Image) {
        ProductReadResponseDTO productReadResponseDTO = new ProductReadResponseDTO();
        productReadResponseDTO.setId(product.getId());
        productReadResponseDTO.setOwnerId(product.getOwnerId());
        productReadResponseDTO.setProductName(product.getProductName());
        productReadResponseDTO.setPrice(product.getPrice());
        List<String> tags = new ArrayList<>();
        for (Tag tag : product.getTags()) {
            tags.add(tag.getTagName());
        }
        productReadResponseDTO.setTags(tags);
        productReadResponseDTO.setCondition(product.getCondition());
        productReadResponseDTO.setImageBase64(base64Image);
        productReadResponseDTO.setTotalQuantity(product.getTotalQuantity());
        productReadResponseDTO.setCategoryName(product.getCategory().getCategoryName());
        productReadResponseDTO.setDescription(product.getDescription());

        return productReadResponseDTO;
    }

}
