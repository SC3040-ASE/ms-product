package com.product.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.Application;
import com.product.dto.RequestMessageDTO;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.product.ProductCreationRequestDTO;
import com.product.dto.product.ProductReadResponseDTO;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.User;
import com.product.entity.Tag;
import com.product.repository.CategoryRepository;
import com.product.repository.ProductRepository;
import com.product.repository.TagRepository;
import com.product.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class InboundConfigurationTest {

    @Autowired
    private InboundConfiguration inboundConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private Tag tag1;
    private Tag tag2;
    private Product product;
    private User user;

    @BeforeAll
    public void setup(){
        user = new User();
        user.setIsAdmin(false);
        user.setUsername("testInbound");
        user.setEmail("testInbound@emai.com");
        user.setTelegramHandle("testInbound");
        user.setPassword("testInbound");
        userRepository.save(user);

        category = new Category();
        category.setCategoryName("Test Inbound Category");
        categoryRepository.save(category);

        tag1 = new Tag();
        tag1.setTagName("Test Inbound Tag 1");
        tag1.setCategory(category);
        tagRepository.save(tag1);


        tag2 = new Tag();
        tag2.setTagName("Test Inbound Tag 2");
        tag2.setCategory(category);
        tagRepository.save(tag2);

        product = new Product();
        product.setProductName("Test Inbound Product");
        product.setDescription("Test Inbound Product Description");
        product.setPrice(BigDecimal.valueOf(100));
        product.setTotalQuantity(2);
        product.setCurrentQuantity(2);
        product.setCondition("NEW");
        product.setOwnerId(user.getId());
        product.setCategory(category);
        product.setTags(new ArrayList<>());
        product.getTags().add(tag1);
        product.getTags().add(tag2);
        productRepository.save(product);


    }

    @AfterAll
    public void tearDown(){
        productRepository.delete(product);
        tagRepository.delete(tag1);
        tagRepository.delete(tag2);
        categoryRepository.delete(category);
        userRepository.delete(user);
    }



    @Test
    @Transactional
    @DisplayName("Test Product Creation")
    void testProductCreation() throws Exception{
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));

        ProductCreationRequestDTO productCreationRequestDTO = new ProductCreationRequestDTO();
        productCreationRequestDTO.setProductName("Test Product");
        productCreationRequestDTO.setDescription("Test Product Description");
        productCreationRequestDTO.setPrice(BigDecimal.valueOf(100));
        productCreationRequestDTO.setTotalQuantity(2);
        productCreationRequestDTO.setCondition("NEW");
        productCreationRequestDTO.setOwnerId(user.getId());
        productCreationRequestDTO.setImageBase64List(new ArrayList<>());
        productCreationRequestDTO.setTags(new ArrayList<>());
        productCreationRequestDTO.setCategory("Test Category");

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO("123", "POST", "/products", headers, objectMapper.writeValueAsString(productCreationRequestDTO));

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);

        Assertions.assertEquals(200,response.getStatus());

        Integer productId = objectMapper.readTree(response.getBody()).get("productId").asInt();

        Product productCreated = productRepository.findById(productId).orElse(null);

        Assertions.assertNotNull(productCreated);
        Assertions.assertEquals(productCreationRequestDTO.getProductName(), productCreated.getProductName());
        Assertions.assertEquals(productCreationRequestDTO.getDescription(), productCreated.getDescription());
        Assertions.assertTrue(productCreationRequestDTO.getPrice().compareTo(productCreated.getPrice())==0);
        Assertions.assertEquals(productCreationRequestDTO.getTotalQuantity(), productCreated.getTotalQuantity());
        Assertions.assertEquals(productCreationRequestDTO.getCondition(), productCreated.getCondition());
        Assertions.assertEquals(productCreationRequestDTO.getOwnerId(), productCreated.getOwnerId());
        Assertions.assertEquals(productCreationRequestDTO.getCategory(), productCreated.getCategory().getCategoryName());

        // clean up
        productRepository.delete(productCreated);
    }


    @Test
    @DisplayName("Test read invalid product by id")
    void testReadInvalidProduct() throws Exception {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));
        headers.put("X-id", "-1");

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO("123", "GET", "/products", headers, null);

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(404, response.getStatus());
        Assertions.assertEquals("Product not found", response.getBody());
    }

    @Test
    @DisplayName("Test read product without id")
    void testReadProductWithoutId() throws Exception {
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO("123", "GET", "/products", headers, null);

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(400, response.getStatus());
        Assertions.assertEquals("Bad request", response.getBody());
    }


    @Test
    @DisplayName("Test read valid product by id")
    void testReadValidProduct() throws Exception{
        Map<String,String> headers = new HashMap<>();
        headers.put("X-User-Id", Integer.toString(user.getId()));
        headers.put("X-Is-Admin", Boolean.toString(user.getIsAdmin()));
        headers.put("X-id", Integer.toString(product.getId()));

        RequestMessageDTO requestMessageDTO = new RequestMessageDTO("123", "GET", "/products", headers, null);

        ResponseMessageDTO response = inboundConfiguration.handler(requestMessageDTO);
        Assertions.assertEquals(200, response.getStatus());

        ProductReadResponseDTO productReadResponseDTO = objectMapper.readValue(response.getBody(), ProductReadResponseDTO.class);
        Assertions.assertEquals(product.getProductName(), productReadResponseDTO.getProductName());
        Assertions.assertEquals(product.getDescription(), productReadResponseDTO.getDescription());
        Assertions.assertTrue(product.getPrice().compareTo(productReadResponseDTO.getPrice())==0);
        Assertions.assertEquals(product.getTotalQuantity(), productReadResponseDTO.getTotalQuantity());
        Assertions.assertEquals(product.getCondition(), productReadResponseDTO.getCondition());
        Assertions.assertEquals(product.getOwnerId(), productReadResponseDTO.getOwnerId());
        Assertions.assertEquals(product.getCategory().getCategoryName(), productReadResponseDTO.getCategoryName());
        Assertions.assertEquals(2, productReadResponseDTO.getTags().size());
        Assertions.assertTrue(productReadResponseDTO.getTags().contains(tag1.getTagName()));
        Assertions.assertTrue(productReadResponseDTO.getTags().contains(tag2.getTagName()));
    }



}
