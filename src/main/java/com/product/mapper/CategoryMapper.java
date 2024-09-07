package com.product.mapper;

import com.product.dto.category.CategoryCreationRequestDTO;
import com.product.dto.category.CategoryReadResponseDTO;
import com.product.dto.category.CategorySearchResponseDTO;
import com.product.dto.category.CategoryUpdateRequestDTO;
import com.product.entity.Category;
import com.product.entity.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CategoryMapper {
    public Category mapToEntity(CategoryCreationRequestDTO categoryCreationRequestDTO) {
        System.out.println("Mapping to entity: " + categoryCreationRequestDTO);
        Category category = new Category();
        category.setCategoryName(categoryCreationRequestDTO.getCategoryName());
        System.out.println("Map category name success");
        category.setProducts(categoryCreationRequestDTO.getProducts());
        System.out.println("Map products success");
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
                Product[] productArray = (Product[]) row[2];
                CategorySearchResponseDTO result = CategorySearchResponseDTO
                    .builder()
                    .id((Integer) row[0])
                    .categoryName((String) row[1])
                    .products(Arrays.asList(productArray))
                    .build();
                searchResults.add(result);
            } catch (Exception e) {
                System.out.println("Failed to convert category search results to DTO.");
            }
        }
        return searchResults;
    }
}
