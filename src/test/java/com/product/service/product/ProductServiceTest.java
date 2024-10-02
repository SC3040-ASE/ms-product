package com.product.service.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.Application;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.product.ProductCreationRequestDTO;
import com.product.dto.product.ProductReadResponseDTO;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.Tag;
import com.product.entity.User;
import com.product.repository.CategoryRepository;
import com.product.repository.ProductRepository;
import com.product.repository.TagRepository;
import com.product.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class ProductServiceTest {

    @Autowired
    private ProductCreationService productCreationService;
    @Autowired
    private ProductDeleteService productDeleteService;
    @Autowired
    private ProductReadService productReadService;
    @Autowired
    private ProductSearchRangeService productSearchRangeService;
    @Autowired
    private ProductUpdateService productUpdateService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ObjectMapper objectMapper;


    private Category testCategory;
    private Tag testTag;
    private Product testProduct;
    private Product searchProduct;
    private User user1;
    private User user2;
    private List<String> base64Images;


    @BeforeAll
    public void setup() throws Exception{
        base64Images = new ArrayList<>();
        File image1 = ResourceUtils.getFile("classpath:image/test-image-1.jpg");
        byte[] encoded1 = Base64.encodeBase64(FileUtils.readFileToByteArray(image1));
        base64Images.add(new String(encoded1, StandardCharsets.US_ASCII));

        user1 = new User();
        user1.setUsername("productTestService1");
        user1.setPassword("productTestService1");
        user1.setEmail("productTestService1@example.com");
        user1.setTelegramHandle("productTestService1");
        user1.setIsAdmin(false);
        user1.setCreatedOn(LocalDateTime.now());
        user1.setUpdatedOn(LocalDateTime.now());
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setUsername("productTestService2");
        user2.setPassword("productTestService2");
        user2.setEmail("productTestService2@example.com");
        user2.setTelegramHandle("productTestService2");
        user2.setIsAdmin(false);
        user2.setCreatedOn(LocalDateTime.now());
        user2.setUpdatedOn(LocalDateTime.now());
        user2 = userRepository.save(user2);

        testCategory = new Category();
        testCategory.setCategoryName("Product Service Test Category");
        categoryRepository.save(testCategory);

        testTag = new Tag();
        testTag.setTagName("testProductServiceTag");
        testTag.setCategory(testCategory);
        tagRepository.save(testTag);

        testProduct = new Product();
        testProduct.setOwnerId(user1.getId());
        testProduct.setProductName("Test Product Service");
        testProduct.setPrice(BigDecimal.valueOf(100.0));
        testProduct.setTags(List.of(testTag));
        testProduct.setCondition("NEW");
        testProduct.setTotalQuantity(10);
        testProduct.setCurrentQuantity(10);
        testProduct.setCategory(testCategory);
        testProduct.setDescription("A test product Service");
        testProduct = productRepository.save(testProduct);

        searchProduct = new Product();
        searchProduct.setOwnerId(user1.getId());
        searchProduct.setProductName("Test Search Product");
        searchProduct.setPrice(BigDecimal.valueOf(5999.99));
        searchProduct.setTags(List.of(testTag));
        searchProduct.setCondition("NEW");
        searchProduct.setTotalQuantity(5);
        searchProduct.setCurrentQuantity(5);
        searchProduct.setCategory(testCategory);
        searchProduct.setDescription("The latest test product");
        searchProduct = productRepository.save(searchProduct);
    }

    @AfterAll
    public void tearDown(){
        productRepository.delete(searchProduct);
        productRepository.delete(testProduct);
        tagRepository.delete(testTag);
        categoryRepository.delete(testCategory);
        userRepository.delete(user1);
        userRepository.delete(user2);
    }

    @Test
    @DisplayName("Test Create Product")
    public void testCreateProduct() throws Exception{
        ProductCreationRequestDTO productCreationRequestDTO = new ProductCreationRequestDTO();
        productCreationRequestDTO.setOwnerId(user1.getId());
        productCreationRequestDTO.setProductName("testCreate");
        productCreationRequestDTO.setCategory("test");
        productCreationRequestDTO.setImageBase64List(new ArrayList<>());
        productCreationRequestDTO.setCondition("NEW");
        productCreationRequestDTO.setDescription("test");
        productCreationRequestDTO.setPrice(BigDecimal.valueOf(100));
        productCreationRequestDTO.setTags(new ArrayList<>());
        productCreationRequestDTO.setTotalQuantity(1);
        ResponseMessageDTO response = productCreationService.createProduct("123", productCreationRequestDTO);

        Assertions.assertEquals(200, response.getStatus());
        Integer productId = objectMapper.readTree(response.getBody()).get("productId").asInt();
        Product databaseProduct = productRepository.findById(productId).orElse(null);;

        Assertions.assertNotNull(databaseProduct);
        Assertions.assertEquals(productCreationRequestDTO.getProductName(), databaseProduct.getProductName());
        Assertions.assertEquals(productCreationRequestDTO.getOwnerId(), databaseProduct.getOwnerId());
        Assertions.assertEquals(productCreationRequestDTO.getCategory(), databaseProduct.getCategory().getCategoryName());
        Assertions.assertEquals(productCreationRequestDTO.getCondition(), databaseProduct.getCondition());
        Assertions.assertEquals(productCreationRequestDTO.getDescription(), databaseProduct.getDescription());
        Assertions.assertTrue(productCreationRequestDTO.getPrice().compareTo(databaseProduct.getPrice())==0);
        Assertions.assertEquals(productCreationRequestDTO.getTotalQuantity(), databaseProduct.getTotalQuantity());

        // clean up
        productRepository.delete(databaseProduct);
    }

    @Test
    @DisplayName("Test Read Product")
    public void testReadProduct() throws Exception{

        ResponseMessageDTO response = productReadService.readProduct("123", testProduct.getId());
        Assertions.assertEquals(200, response.getStatus());
        ProductReadResponseDTO foundProduct = objectMapper.readValue(response.getBody(), ProductReadResponseDTO.class);

        Assertions.assertNotNull(foundProduct);
        Assertions.assertEquals(testProduct.getId(), foundProduct.getProductId());
        Assertions.assertEquals(testProduct.getProductName(), foundProduct.getProductName());
        Assertions.assertEquals(testProduct.getOwnerId(), foundProduct.getOwnerId());
        Assertions.assertEquals(testProduct.getCategory().getCategoryName(), foundProduct.getCategoryName());
        Assertions.assertEquals(testProduct.getCondition(), foundProduct.getCondition());
        Assertions.assertEquals(testProduct.getDescription(), foundProduct.getDescription());
        Assertions.assertTrue(testProduct.getPrice().compareTo(foundProduct.getPrice())==0);
        Assertions.assertEquals(testProduct.getTotalQuantity(), foundProduct.getTotalQuantity());
    }

    @Test
    @DisplayName("Test Update Product")
    public void testUpdateProduct() throws Exception{

    }
}
