package com.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.Application;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.category.*;
import com.product.dto.tag.TagCreationRequestDTO;
import com.product.entity.Category;
import com.product.repository.CategoryRepository;
import com.product.service.category.CategoryCreationService;
import com.product.service.category.CategoryDeleteService;
import com.product.service.category.CategoryReadService;
import com.product.service.category.CategoryUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CategoryServiceTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryCreationService categoryCreationService;

    @Autowired
    private CategoryUpdateService categoryUpdateService;

    @Autowired
    private CategoryReadService categoryReadService;

    @Autowired
    private CategoryDeleteService categoryDeleteService;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeAll
    public void setup(){

    }

    @AfterAll
    public void tearDown(){

    }

    @Test
    @DisplayName("Test Create Category")
    void testCreateCategory() throws Exception {
        CategoryCreationRequestDTO categoryCreationRequestDTO = new CategoryCreationRequestDTO();
        categoryCreationRequestDTO.setCategoryName("testCategoryCreation");
        ResponseMessageDTO response = categoryCreationService.createCategory(
            "123", categoryCreationRequestDTO
        );
        Assertions.assertEquals(200, response.getStatus());
        Category category = objectMapper.readValue(response.getBody(), Category.class);
        Assertions.assertEquals("testCategoryCreation", category.getCategoryName());

        // Clean up
        categoryRepository.deleteById(category.getId());
    }

    @Test
    @DisplayName("Test Read Category")
    void testReadCategory() throws Exception {
        Category cat = new Category();
        cat.setCategoryName("testCategory");
        cat = categoryRepository.save(cat);
        CategoryReadRequestDTO categoryReadRequestDTO = new CategoryReadRequestDTO();
        categoryReadRequestDTO.setCategoryName(cat.getCategoryName());
        ResponseMessageDTO response = categoryReadService.readCategory("123", categoryReadRequestDTO);
        Assertions.assertEquals(200, response.getStatus());
        Category category = objectMapper.readValue(response.getBody(), Category.class);
        Assertions.assertEquals("testCategory", category.getCategoryName());
        categoryReadRequestDTO.setId(category.getId());

        CategoryReadResponseDTO categoryReadResponseDTO = categoryReadService.readCategory(categoryReadRequestDTO);
        Assertions.assertEquals("testCategory", categoryReadResponseDTO.getCategoryName());

        CategoryReadResponseDTO categoryReadResponseDTO1 = categoryReadService.readCategory(category.getId());
        Assertions.assertEquals(categoryReadResponseDTO.getCategoryName(), categoryReadResponseDTO1.getCategoryName());
        categoryRepository.deleteById(cat.getId());
    }

    @Test
    @DisplayName("Test Read All Categories")
    void testReadAllCategory() throws JsonProcessingException {
        List<CategoryReadResponseDTO> dtoList = categoryReadService.getAllCategories();
        Assertions.assertFalse(dtoList.isEmpty());

        ResponseMessageDTO response = categoryReadService.getAllCategories("123");
        Assertions.assertEquals(200, response.getStatus());
        List<CategoryReadResponseDTO> dtoList1 = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        Assertions.assertFalse(dtoList.isEmpty());
    }

    @Test
    @DisplayName("Test Update All Categories")
    void testUpdateCategory() throws Exception {
        Category cat1 = new Category();
        cat1.setCategoryName("testUpdateCategory");
        cat1 = categoryRepository.save(cat1);
        CategoryUpdateRequestDTO categoryUpdateRequestDTO = new CategoryUpdateRequestDTO();
        categoryUpdateRequestDTO.setId(cat1.getId());
        categoryUpdateRequestDTO.setCategoryName("testUpdateCatName");
        ResponseMessageDTO response = categoryUpdateService.updateCategory("123", categoryUpdateRequestDTO);
        Assertions.assertEquals(200, response.getStatus());
        Category c = objectMapper.readValue(response.getBody(), Category.class);
        Assertions.assertEquals("testUpdateCatName", c.getCategoryName());
        Assertions.assertEquals(cat1.getId(), c.getId());
        categoryRepository.deleteById(cat1.getId());
    }

    @Test
    @DisplayName("Test Delete Categories")
    void testDeleteCategory() {
        Category cat = new Category();
        cat.setCategoryName("testDeleteCategory");
        cat = categoryRepository.save(cat);
        CategoryDeleteRequestDTO categoryDeleteRequestDTO = new CategoryDeleteRequestDTO();
        categoryDeleteRequestDTO.setId(cat.getId());
        categoryDeleteRequestDTO.setCategoryName(cat.getCategoryName());
        ResponseMessageDTO response = categoryDeleteService.deleteCategory("123", categoryDeleteRequestDTO);
        Assertions.assertEquals(200, response.getStatus());


    }
}
