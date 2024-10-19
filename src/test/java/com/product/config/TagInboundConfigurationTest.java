package com.product.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.Application;
import com.product.dto.RequestMessageDTO;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.tag.TagCreationRequestDTO;
import com.product.dto.tag.TagDeleteRequestDTO;
import com.product.dto.tag.TagReadRequestDTO;
import com.product.dto.tag.TagUpdateRequestDTO;
import com.product.entity.Category;
import com.product.entity.Tag;
import com.product.entity.User;
import com.product.repository.CategoryRepository;
import com.product.repository.TagRepository;
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
public class TagInboundConfigurationTest {

    @Autowired
    private InboundConfiguration inboundConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    private Category category;

    private Tag taggy;

    private User user;

    @BeforeAll
    public void setup(){
        category = new Category();
        category.setCategoryName("testTagGeneration");
        category = categoryRepository.save(category);

        taggy = new Tag();
        taggy.setCategory(category);
        taggy.setTagName("tempTag");
        taggy = tagRepository.save(taggy);

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
        tagRepository.delete(taggy);
        categoryRepository.delete(category);
        userRepository.delete(user);
    }

    @Test
    @DisplayName("Test Tag Generation")
    void testTagGeneration() throws Exception{
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", "-1");
        headers.put("X-Is-Admin", "false");
        headers.put("X-productName", "apple watch");
        headers.put("X-productDescription", "Lastest apple watch");
        headers.put("X-categoryId", category.getId().toString());


        RequestMessageDTO requestMessageDTO = new RequestMessageDTO("123", "GET", "/products/tag/generate",headers,null);
        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertNotNull(response.getBody());
        List<String> tags = objectMapper.readValue(response.getBody(), new TypeReference<>(){});
        Assertions.assertFalse(tags.isEmpty());
    }

    @Test
    @Transactional
    @DisplayName("Test Tag Creation")
    void testTagCreation() throws Exception {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));

        TagCreationRequestDTO tagCreationRequestDTO = TagCreationRequestDTO
            .builder()
            .category(category)
            .tagName("testTag")
            .build();

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO(
            "123",
            "POST",
            "/products/tag",
            headers,
            objectMapper.writeValueAsString(tagCreationRequestDTO)
        );

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());
        Tag tag = objectMapper.readValue(response.getBody(), Tag.class);
        Optional<Tag> t = tagRepository.findById(tag.getId());
        Assertions.assertTrue(t.isPresent());
        Assertions.assertEquals("testTag", t.get().getTagName());

        // Clean up
        tagRepository.deleteById(t.get().getId());
    }

    @Test
    @Transactional
    @DisplayName("Test Tag Update")
    void testTagUpdate() throws Exception {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));

        TagUpdateRequestDTO tagUpdateRequestDTO = TagUpdateRequestDTO
            .builder()
            .id(taggy.getId())
            .tagName("tempTag2")
            .category(taggy.getCategory())
            .build();

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO(
            "123",
            "PUT",
            "/products/tag",
            headers,
            objectMapper.writeValueAsString(tagUpdateRequestDTO)
        );

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());
        Tag tag = objectMapper.readValue(response.getBody(), Tag.class);
        Optional<Tag> t = tagRepository.findById(tag.getId());
        Assertions.assertTrue(t.isPresent());
        Assertions.assertEquals("tempTag2", t.get().getTagName());

        // Clean up
        tagUpdateRequestDTO.setTagName("tempTag");
        requestMessageDTO = new RequestMessageDTO(
            "123",
            "PUT",
            "/products/tag",
            headers,
            objectMapper.writeValueAsString(tagUpdateRequestDTO)
        );
        response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @Transactional
    @DisplayName("Test Tag Delete")
    void testTagDelete() throws Exception {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));
        Tag tag = new Tag();
        tag.setTagName("TestDeleteTag");
        tag.setCategory(category);
        tag.setProducts(new ArrayList<>());
        tag = tagRepository.save(tag);

        TagDeleteRequestDTO tagDeleteRequestDTO = TagDeleteRequestDTO
            .builder()
            .id(tag.getId())
            .tagName("TestDeleteTag")
            .category(tag.getCategory())
            .build();

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO(
            "123",
            "DELETE",
            "/products/tag",
            headers,
            objectMapper.writeValueAsString(tagDeleteRequestDTO)
        );

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());
        Optional<Tag> t = tagRepository.findById(tag.getId());
        Assertions.assertFalse(t.isPresent());

        // Clean up

    }

    @Test
    @Transactional
    @DisplayName("Test Tag Get")
    void testTagGet() throws Exception {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));

        TagReadRequestDTO tagGetRequestDTO = TagReadRequestDTO
            .builder()
            .id(taggy.getId())
            .tagName(taggy.getTagName())
            .category(taggy.getCategory())
            .build();

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO(
            "123",
            "GET",
            "/products/tag",
            headers,
            objectMapper.writeValueAsString(tagGetRequestDTO)
        );

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());
        Optional<Tag> t = tagRepository.findById(taggy.getId());
        Assertions.assertTrue(t.isPresent());

        requestMessageDTO = new RequestMessageDTO(
            "123",
            "GET",
            "/products/tag/all",
            headers,
            objectMapper.writeValueAsString(tagGetRequestDTO)
        );
        response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());
        List<Tag> tags = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        Assertions.assertFalse(tags.isEmpty());
    }
}
