package com.product.mapper;

import com.product.Application;
import com.product.dto.category.CategoryCreationRequestDTO;
import com.product.dto.category.CategoryReadResponseDTO;
import com.product.dto.category.CategoryUpdateRequestDTO;
import com.product.entity.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryMapperTest {
    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("Test Map to Entity")
    void testMapToEntity() {
        CategoryCreationRequestDTO categoryCreationRequestDTO = new CategoryCreationRequestDTO();
        categoryCreationRequestDTO.setCategoryName("catName");
        Category c = categoryMapper.mapToEntity(categoryCreationRequestDTO);
        Assertions.assertEquals("catName", c.getCategoryName());
    }

    @Test
    @DisplayName("Test Map to ReadResponse")
    void testMapToReadResponse() {
        Category c = new Category();
        c.setId(1);
        c.setCategoryName("category");

        CategoryReadResponseDTO categoryCreationRequestDTO = categoryMapper.mapToCategoryReadResponse(c);
        Assertions.assertEquals("category", categoryCreationRequestDTO.getCategoryName());
        Assertions.assertEquals(1, categoryCreationRequestDTO.getId());
    }

    @Test
    @DisplayName("Test Map DTO to Category")
    void testMapDTOToCategory() {
        Category c = new Category();
        c.setId(1);
        c.setCategoryName("category");
        CategoryUpdateRequestDTO categoryUpdateRequestDTO = new CategoryUpdateRequestDTO();
        categoryUpdateRequestDTO.setId(1);
        categoryUpdateRequestDTO.setCategoryName("category1");

        Category c1 = categoryMapper.mapCategoryDTOToCategory(c, categoryUpdateRequestDTO);
        Assertions.assertEquals("category1", c1.getCategoryName());
        Assertions.assertEquals(1, c1.getId());
    }
}
