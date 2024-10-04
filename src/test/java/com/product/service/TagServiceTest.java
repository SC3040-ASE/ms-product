package com.product.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.Application;
import com.product.dto.ResponseMessageDTO;
import com.product.entity.Category;
import com.product.repository.CategoryRepository;
import com.product.service.tag.TagGenerationService;
import lombok.extern.slf4j.Slf4j;
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
public class TagServiceTest {

    @Autowired
    private TagGenerationService tagGenerationService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Category category;


    @BeforeAll
    public void setup(){
        category = new Category();
        category.setCategoryName("testTagGeneration");
        category = categoryRepository.save(category);
    }

    @AfterAll
    public void tearDown(){
        categoryRepository.delete(category);
    }

    @Test
    @DisplayName("Test Generate Tag")
    void testGenerateTag() throws Exception {
        ResponseMessageDTO response = tagGenerationService.generateTag("123", "apple watch", "Lastest apple watch", category.getId());
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertNotNull(response.getBody());
        List<String> tags = objectMapper.readValue(response.getBody(), new TypeReference<>(){});
        Assertions.assertFalse(tags.isEmpty());
    }

}
