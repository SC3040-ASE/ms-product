package com.product.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.Application;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.tag.TagCreationRequestDTO;
import com.product.dto.tag.TagDeleteRequestDTO;
import com.product.entity.Category;
import com.product.entity.Tag;
import com.product.repository.CategoryRepository;
import com.product.service.tag.*;
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
    private TagCreationService tagCreationService;

    @Autowired
    private TagReadService tagReadService;

    @Autowired
    private TagUpdateService tagUpdateService;

    @Autowired
    private TagDeleteService tagDeleteService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Category category;

    private TagCreationRequestDTO tagCreationRequestDTO;


    @BeforeAll
    public void setup(){
        category = new Category();
        category.setCategoryName("testTagGeneration");
        category = categoryRepository.save(category);

        tagCreationRequestDTO = TagCreationRequestDTO
            .builder()
            .tagName("watch")
            .category(category)
            .build();
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

    @Test
    @DisplayName("Test Create Tag")
    void testCreateTag() throws Exception {

        ResponseMessageDTO response = tagCreationService.createTag("123", tagCreationRequestDTO);

        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertNotNull(response.getBody());
        System.out.println(response.getBody());
        Tag tag = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        TagDeleteRequestDTO tagDeleteRequestDTO = TagDeleteRequestDTO
            .builder()
            .category(category)
            .tagName("watch")
            .id(tag.getId())
            .build();

        // Clean up
        tagDeleteService.deleteTag("123", tagDeleteRequestDTO);
    }

    @Test
    @DisplayName("Test Delete Tag")
    void testDeleteTag() throws Exception {

        ResponseMessageDTO response = tagCreationService.createTag("123", tagCreationRequestDTO);
        Tag tag = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        System.out.println("Tag: " + tag.toString());
        TagDeleteRequestDTO tagDeleteRequestDTO = TagDeleteRequestDTO
            .builder()
            .category(category)
            .tagName("watch")
            .id(tag.getId())
            .build();

        // Clean up
        ResponseMessageDTO responseMessageDTO = tagDeleteService.deleteTag("123", tagDeleteRequestDTO);
        Assertions.assertEquals(200, responseMessageDTO.getStatus());
    }

}
