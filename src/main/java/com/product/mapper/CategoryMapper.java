package com.product.mapper;

import com.product.dto.category.CategoryCreationRequestDTO;
import com.product.dto.category.CategoryReadResponseDTO;
import com.product.dto.category.CategorySearchResponseDTO;
import com.product.dto.category.CategoryUpdateRequestDTO;
import com.product.entity.Category;
import com.product.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class CategoryMapper {
    public Category mapToEntity(CategoryCreationRequestDTO categoryCreationRequestDTO) {
        log.info("Mapping to entity: " + categoryCreationRequestDTO);
        Category category = new Category();
        category.setCategoryName(categoryCreationRequestDTO.getCategoryName());
        log.info("Map category name success");
        category.setProducts(categoryCreationRequestDTO.getProducts());
        log.info("Map products success");
        return category;
    }

    public CategoryReadResponseDTO mapToCategoryReadResponse(Category category) {
        return CategoryReadResponseDTO
            .builder()
            .Id(category.getId())
            .categoryName(category.getCategoryName())
            .build();
    }

    public Category mapCategoryDTOToCategory(Category category, CategoryUpdateRequestDTO categoryUpdateRequestDTO) {
        category.setCategoryName(categoryUpdateRequestDTO.getCategoryName());
        return category;
    }

    public List<CategorySearchResponseDTO> mapToSearchResults(List<Object[]> results) {
        List<CategorySearchResponseDTO> searchResults = new ArrayList<>();
        for (Object[] row: results) {
            try {
                CategorySearchResponseDTO result = CategorySearchResponseDTO
                    .builder()
                    .id((Integer) row[0])
                    .categoryName((String) row[1])
                    // TODO: Add the embedding pointed to list of products? Need to check if im supposed to do that for both category and tag first.
                    .build();
                searchResults.add(result);
            } catch (Exception e) {
                log.error("Failed to convert category search results to DTO.");
            }
        }
        return searchResults;
    }
}
