package com.product.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.Application;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.tag.*;
import com.product.entity.Category;
import com.product.entity.Tag;
import com.product.repository.CategoryRepository;
import com.product.repository.TagRepository;
import com.product.service.tag.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private TagRepository tagRepository;

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
    @DisplayName("Test Create Multiple Tag")
    void testCreateMultipleTags() {
        List<String> tags = new ArrayList<>();
        tags.add("a");
        tags.add("b");
        tags.add("c");
        tags.add("d");
        MultipleTagCreationRequestDTO multipleTags = MultipleTagCreationRequestDTO
            .builder()
            .categoryId(category.getId())
            .tags(tags)
            .build();
        MultipleTagCreationResponseDTO response = tagCreationService.createMultipleTagsForCategory(multipleTags);
        Assertions.assertEquals(4, response.getTagIds().size());

        // Clean up
        response.getTagIds().forEach(tag -> {
            Optional<Tag> optionalTag = tagRepository.findById(tag);
            if (optionalTag.isPresent()) {
                TagDeleteRequestDTO tagDeleteRequestDTO = TagDeleteRequestDTO
                    .builder()
                    .category(category)
                    .id(optionalTag.get().getId())
                    .tagName(optionalTag.get().getTagName())
                    .build();
                tagDeleteService.deleteTag("123", tagDeleteRequestDTO);
            }

        });
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

    @Test
    @DisplayName("Test Update Tag")
    void testUpdateTag() throws Exception {
        tagCreationService.createTag("123", tagCreationRequestDTO);
        Optional<Tag> optionalTag = tagRepository.findByTagNameAndCategoryName(
            tagCreationRequestDTO.getTagName(), tagCreationRequestDTO.getCategory().getId()
        );
        if (optionalTag.isPresent()) {
            TagUpdateRequestDTO newTag = TagUpdateRequestDTO
                .builder()
                .id(optionalTag.get().getId())
                .category(optionalTag.get().getCategory())
                .tagName("watcher")
                .build();
            ResponseMessageDTO response = tagUpdateService.updateTag("123", newTag);
            Assertions.assertEquals(200, response.getStatus());
            TagReadResponseDTO responseBody = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
            Assertions.assertEquals("watcher", responseBody.getTagName());
            tagRepository.deleteById(optionalTag.get().getId());
        }
    }

    @Test
    @DisplayName("Test Fetch Multiple Tag")
    void testFetchTags() {
        List<String> tags = new ArrayList<>();
        tags.add("a");
        tags.add("b");
        tags.add("c");
        tags.add("d");
        MultipleTagCreationRequestDTO multipleTags = MultipleTagCreationRequestDTO
            .builder()
            .categoryId(category.getId())
            .tags(tags)
            .build();
        MultipleTagCreationResponseDTO response = tagCreationService.createMultipleTagsForCategory(multipleTags);
        Assertions.assertEquals(4, response.getTagIds().size());

        List<Tag> savedTags = tagReadService.fetchTags(response.getTagIds());
        Assertions.assertEquals(4, savedTags.size());
    }

    @Test
    @DisplayName("Test Read Tag")
    void testReadTags() throws Exception {
        tagCreationService.createTag("123", tagCreationRequestDTO);
        Optional<Tag> optionalTag = tagRepository.findByTagNameAndCategoryName(
            tagCreationRequestDTO.getTagName(), tagCreationRequestDTO.getCategory().getId()
        );
        if (optionalTag.isPresent()) {
            TagReadRequestDTO tagReadRequestDTO = TagReadRequestDTO
                .builder()
                .category(optionalTag.get().getCategory())
                .id(optionalTag.get().getId())
                .tagName(optionalTag.get().getTagName())
                .build();
            ResponseMessageDTO response = tagReadService.readTag("123", tagReadRequestDTO);
            TagReadResponseDTO responseBody = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
            Assertions.assertEquals("watch", responseBody.getTagName());

            tagRepository.deleteById(optionalTag.get().getId());
        }
    }
}
