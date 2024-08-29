package com.product.mapper;

import com.product.dto.ProductReceived;
import com.product.dto.ProductSearchResult;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.Tag;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product mapToEntity(ProductReceived productReceived, List<Tag> existingTags, Category category) {
        Product product = new Product();
        product.setOwnerId(productReceived.getOwnerId());
        product.setProductName(productReceived.getProductName());
        product.setPrice(BigDecimal.valueOf(productReceived.getPrice()));
        product.setTags(existingTags);
        product.setCondition(productReceived.getCondition());
        product.setTotalQuantity(productReceived.getTotalQuantity());
        product.setCurrentQuantity(productReceived.getTotalQuantity()); // Assuming current quantity is same as total initially
        product.setCategory(category);
        product.setDescription(productReceived.getDescription());

        return product;
    }

    public List<ProductSearchResult> mapToSearchResults(List<Object[]> results) {

        List<ProductSearchResult> searchResults = new ArrayList<>();
        for (Object[] row : results) {
            ProductSearchResult result = new ProductSearchResult();
            result.setProductId((Integer) row[0]);
            result.setProductName((String) row[1]);
            result.setPrice((BigDecimal) row[2]);
            result.setTags((String[]) row[3]);
            result.setCondition((String) row[4]);
            result.setProductImage((String) row[5]);
            result.setTotalQuantity((Integer) row[6]);
            result.setCurrentQuantity((Integer) row[7]);
            result.setCategoryName((String) row[8]);
            result.setDescription((String) row[9]);
            result.setScore((Float) row[10]);

            searchResults.add(result);
        }

        return searchResults;
    }

    public List<Tag> mapTags(List<String> tags) {
        return tags.stream().map(Tag::new).collect(Collectors.toList());
    }
}
