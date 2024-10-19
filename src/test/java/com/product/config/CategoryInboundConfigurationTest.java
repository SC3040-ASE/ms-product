package com.product.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.Application;
import com.product.dto.RequestMessageDTO;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.category.*;
import com.product.dto.tag.TagCreationRequestDTO;
import com.product.entity.Category;
import com.product.entity.Tag;
import com.product.entity.User;
import com.product.repository.CategoryRepository;
import com.product.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CategoryInboundConfigurationTest {
    @Autowired
    private InboundConfiguration inboundConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user;

    @BeforeAll
    public void setup(){
        user = new User();
        user.setIsAdmin(false);
        user.setUsername("testInbound");
        user.setEmail("testInbound@emai.com");
        user.setTelegramHandle("testInbound");
        user.setPassword("testInbound");
        user = userRepository.save(user);
    }

    @AfterAll
    public void tearDown(){
        userRepository.delete(user);
    }

    @Test
    @Transactional
    @DisplayName("Test Category Creation")
    void testTagCreation() throws Exception {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));

        CategoryCreationRequestDTO categoryCreationRequestDTO = CategoryCreationRequestDTO
            .builder()
            .categoryName("TestCreateCategory")
            .build();

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO(
            "123",
            "POST",
            "/products/category",
            headers,
            objectMapper.writeValueAsString(categoryCreationRequestDTO)
        );

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());
        Category category = objectMapper.readValue(response.getBody(), Category.class);
        Assertions.assertEquals("TestCreateCategory", category.getCategoryName());

        // Clean up
        categoryRepository.deleteById(category.getId());
    }

    @Test
    @Transactional
    @DisplayName("Test Category Update")
    void testTagUpdate() throws Exception {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));

        Category category = new Category();
        category.setCategoryName("TestCreateCategory");
        category = categoryRepository.save(category);

        CategoryUpdateRequestDTO categoryUpdateRequestDTO = CategoryUpdateRequestDTO
            .builder()
            .categoryName("TestCreateCategoryUpdate")
            .id(category.getId())
            .build();

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO(
            "123",
            "PUT",
            "/products/category",
            headers,
            objectMapper.writeValueAsString(categoryUpdateRequestDTO)
        );

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());
        Category category1 = objectMapper.readValue(response.getBody(), Category.class);
        Assertions.assertEquals("TestCreateCategoryUpdate", category1.getCategoryName());

        // Clean up
        categoryRepository.deleteById(category1.getId());
    }

    @Test
    @Transactional
    @DisplayName("Test Category DELETE")
    void testTagDelete() throws Exception {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));

        Category category = new Category();
        category.setCategoryName("TestCreateCategory");
        category = categoryRepository.save(category);

        CategoryDeleteRequestDTO categoryDeleteRequestDTO = CategoryDeleteRequestDTO
            .builder()
            .categoryName("TestCreateCategory")
            .id(category.getId())
            .build();

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO(
            "123",
            "DELETE",
            "/products/category",
            headers,
            objectMapper.writeValueAsString(categoryDeleteRequestDTO)
        );

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());
        Optional<Category> optionalCategory = categoryRepository.findById(category.getId());
        Assertions.assertTrue(optionalCategory.isEmpty());
    }

    @Test
    @Transactional
    @DisplayName("Test Category GET")
    void testTagGet() throws Exception {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));

        Category category = new Category();
        category.setCategoryName("TestCreateCategory");
        category = categoryRepository.save(category);

        CategoryReadRequestDTO categoryGetRequestDTO = CategoryReadRequestDTO
            .builder()
            .categoryName("TestCreateCategory")
            .id(category.getId())
            .build();

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO(
            "123",
            "GET",
            "/products/category",
            headers,
            objectMapper.writeValueAsString(categoryGetRequestDTO)
        );

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());
        CategoryReadResponseDTO categoryReadResponseDTO = objectMapper.readValue(response.getBody(), CategoryReadResponseDTO.class);
        Assertions.assertEquals("TestCreateCategory", categoryReadResponseDTO.getCategoryName());

        // Clean up
        categoryRepository.deleteById(categoryReadResponseDTO.getId());
    }

    @Test
    @Transactional
    @DisplayName("Test Category GET ALL")
    void testTagGetAll() throws Exception {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO(
            "123",
            "GET",
            "/products/category/all",
            headers,
            null
        );

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());
        List<Category> categories = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        Assertions.assertFalse(categories.isEmpty());
    }
}
