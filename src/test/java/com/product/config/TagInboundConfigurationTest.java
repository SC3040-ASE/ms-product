package com.product.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.Application;
import com.product.dto.RequestMessageDTO;
import com.product.dto.ResponseMessageDTO;
import com.product.entity.Category;
import com.product.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class TagInboundConfigurationTest {

    @Autowired
    private InboundConfiguration inboundConfiguration;

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
}
