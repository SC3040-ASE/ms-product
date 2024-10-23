package com.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.Application;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.image.ImageDTO;
import com.product.dto.product.*;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.Tag;
import com.product.entity.User;
import com.product.repository.CategoryRepository;
import com.product.repository.ProductRepository;
import com.product.repository.TagRepository;
import com.product.repository.UserRepository;
import com.product.service.blob.PictureBlobStorageService;
import com.product.service.product.ProductCreationService;
import com.product.service.product.ProductDeleteService;
import com.product.service.product.ProductReadService;
import com.product.service.product.ProductUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ProductServiceTest {

    @Autowired
    private ProductCreationService productCreationService;
    @Autowired
    private ProductDeleteService productDeleteService;
    @Autowired
    private ProductReadService productReadService;
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
    @Autowired
    private PictureBlobStorageService pictureBlobStorageService;
    private Category testCategory;
    private Tag testTag;
    private Product testProduct1;
    private Product testProduct2;
    private User user1;
    private List<String> base64Images;
    protected ClientAndServer mockServer;


    @BeforeAll
    public void setup() throws Exception{
        mockServer = startClientAndServer(8003);
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

        testCategory = new Category();
        testCategory.setCategoryName("Product Service Test Category");
        categoryRepository.save(testCategory);

        testTag = new Tag();
        testTag.setTagName("testProductServiceTag".toLowerCase());
        testTag.setCategory(testCategory);
        tagRepository.save(testTag);

        testProduct1 = new Product();
        testProduct1.setOwnerId(user1.getId());
        testProduct1.setProductName("Test Product Service 1");
        testProduct1.setPrice(BigDecimal.valueOf(100.0));
        testProduct1.setTags(List.of(testTag));
        testProduct1.setCondition("NEW");
        testProduct1.setTotalQuantity(10);
        testProduct1.setCurrentQuantity(10);
        testProduct1.setCategory(testCategory);
        testProduct1.setDescription("A test product Service 1");

        testProduct2 = new Product();
        testProduct2.setOwnerId(user1.getId());
        testProduct2.setProductName("Test Product Service 2");
        testProduct2.setPrice(BigDecimal.valueOf(100.0));
        testProduct2.setTags(List.of(testTag));
        testProduct2.setCondition("NEW");
        testProduct2.setTotalQuantity(10);
        testProduct2.setCurrentQuantity(10);
        testProduct2.setCategory(testCategory);
        testProduct2.setDescription("A test product Service 2");

    }

    @AfterAll
    public void tearDown(){
        tagRepository.delete(testTag);
        categoryRepository.delete(testCategory);
        userRepository.delete(user1);

        if (mockServer != null) {
            mockServer.stop();
        }
    }

    @Test
    @DisplayName("Test Create Product")
    void testCreateProduct() throws Exception {
        mockServer.when(request()
                        .withMethod("POST")
                        .withPath("/order-requests/products"))
                        .respond(
                        response().withStatusCode(200)
                                .withBody("Mock response"));



        ProductCreationRequestDTO productCreationRequestDTO = new ProductCreationRequestDTO();
        productCreationRequestDTO.setOwnerId(user1.getId());
        productCreationRequestDTO.setProductName("testCreate");
        productCreationRequestDTO.setCategory(testCategory.getCategoryName());
        productCreationRequestDTO.setImageBase64List(base64Images);
        productCreationRequestDTO.setCondition("NEW");
        productCreationRequestDTO.setDescription("test");
        productCreationRequestDTO.setPrice(BigDecimal.valueOf(100));
        productCreationRequestDTO.setTags(new ArrayList<>());
        productCreationRequestDTO.setTotalQuantity(1);
        ResponseMessageDTO response = productCreationService.createProduct("123", productCreationRequestDTO);

        Assertions.assertEquals(200, response.getStatus());
        int productId = objectMapper.readTree(response.getBody()).get("productId").asInt();
        Product databaseProduct = productRepository.findById(productId).orElse(null);

        Assertions.assertNotNull(databaseProduct);
        Assertions.assertEquals(productCreationRequestDTO.getProductName(), databaseProduct.getProductName());
        Assertions.assertEquals(productCreationRequestDTO.getOwnerId(), databaseProduct.getOwnerId());
        Assertions.assertEquals(productCreationRequestDTO.getCategory(), databaseProduct.getCategory().getCategoryName());
        Assertions.assertEquals(productCreationRequestDTO.getCondition(), databaseProduct.getCondition());
        Assertions.assertEquals(productCreationRequestDTO.getDescription(), databaseProduct.getDescription());
        Assertions.assertEquals(0, productCreationRequestDTO.getPrice().compareTo(databaseProduct.getPrice()));
        Assertions.assertEquals(productCreationRequestDTO.getTotalQuantity(), databaseProduct.getTotalQuantity());

        List<String> savedImages = pictureBlobStorageService.retrieveProductImages(productId).stream().map(ImageDTO::getImageBase64).toList();
        Assertions.assertEquals(productCreationRequestDTO.getImageBase64List().size(), savedImages.size());
        Assertions.assertTrue(savedImages.containsAll(productCreationRequestDTO.getImageBase64List()));

        // clean up
        productRepository.delete(databaseProduct);
        pictureBlobStorageService.deleteDirectory(productId);
    }

    @Test
    @DisplayName("Test Read Product Range")
    void testReadProductsByOwnerId() throws Exception{
        testProduct1 = productRepository.save(testProduct1);
        testProduct2 = productRepository.save(testProduct2);

        ResponseMessageDTO response = productReadService.readProductsByOwnerId("123", user1.getId(), 1, 10);

        ProductReadPreviewResponseDTO productReadPreviewResponseDTO = objectMapper.readValue(response.getBody(), ProductReadPreviewResponseDTO.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(2, productReadPreviewResponseDTO.getTotalProducts());
        Assertions.assertEquals(2, productReadPreviewResponseDTO.getProducts().size());

        List<ProductReadPreviewDTO> products = productReadPreviewResponseDTO.getProducts();
        Assertions.assertTrue(products.stream().anyMatch(product -> product.getProductId().equals(testProduct1.getId())));
        Assertions.assertTrue(products.stream().anyMatch(product -> product.getProductId().equals(testProduct2.getId())));

        // clean up
        productRepository.delete(testProduct1);
        productRepository.delete(testProduct2);
    }


    @Test
    @DisplayName("Test Update Product Quantity")
    void testUpdateProductQuantity(){

        mockServer.when(request()
                        .withMethod("POST")
                        .withPath("/order-requests/products"))
                .respond(
                        response().withStatusCode(200)
                                .withBody("Mock response"));
        testProduct1 = productRepository.save(testProduct1);
        productUpdateService.updateProductQuantity(testProduct1.getId(), -1);
        Product updatedProduct = productRepository.findById(testProduct1.getId()).orElse(null);

        Assertions.assertNotNull(updatedProduct);
        Assertions.assertEquals(testProduct1.getCurrentQuantity()-1, updatedProduct.getCurrentQuantity());

        // clean up
        productRepository.delete(testProduct1);
    }

    @Test
    @DisplayName("Test Update Product")
    void testUpdateProduct() throws JsonProcessingException {
        testProduct1 = productRepository.save(testProduct1);

        ProductUpdateRequestDTO testProduct1Updated = new ProductUpdateRequestDTO();
        testProduct1Updated.setProductId(testProduct1.getId());
        testProduct1Updated.setOwnerId(user1.getId());
        testProduct1Updated.setProductName("Test Product Service 1 Updated");
        testProduct1Updated.setPrice(BigDecimal.valueOf(200.0));
        testProduct1Updated.setTags(Stream.of(testTag).map(Tag::getTagName).toList());
        testProduct1Updated.setCondition("USED");
        testProduct1Updated.setTotalQuantity(20);
        testProduct1Updated.setCurrentQuantity(20);
        testProduct1Updated.setCategory(testCategory.getCategoryName());
        testProduct1Updated.setDescription("A test product Service 1 Updated");
        testProduct1Updated.setNewImageBase64List(base64Images);
        testProduct1Updated.setDeleteImageList(new ArrayList<>());

        ResponseMessageDTO response = productUpdateService.updateProduct("123", testProduct1Updated);
        Assertions.assertEquals(200, response.getStatus());

        Product updatedProduct = productRepository.findById(testProduct1.getId()).orElse(null);
        Assertions.assertNotNull(updatedProduct);
        Assertions.assertEquals(testProduct1Updated.getProductName(), updatedProduct.getProductName());
        Assertions.assertEquals(testProduct1Updated.getOwnerId(), updatedProduct.getOwnerId());
        Assertions.assertEquals(testProduct1Updated.getCategory(), updatedProduct.getCategory().getCategoryName());
        Assertions.assertEquals(testProduct1Updated.getCondition(), updatedProduct.getCondition());
        Assertions.assertEquals(testProduct1Updated.getDescription(), updatedProduct.getDescription());
        Assertions.assertEquals(0,testProduct1Updated.getPrice().compareTo(updatedProduct.getPrice()));
        Assertions.assertEquals(testProduct1Updated.getTotalQuantity(), updatedProduct.getTotalQuantity());
        Assertions.assertEquals(testProduct1Updated.getCurrentQuantity(), updatedProduct.getCurrentQuantity());

        List<ImageDTO> savedImages = pictureBlobStorageService.retrieveProductImages(testProduct1.getId());
        Assertions.assertEquals(testProduct1Updated.getNewImageBase64List().size(), savedImages.size());
        Assertions.assertTrue(savedImages.stream().map(ImageDTO::getImageBase64).toList().containsAll(testProduct1Updated.getNewImageBase64List()));

        // clean up
        productRepository.delete(testProduct1);
        pictureBlobStorageService.deleteDirectory(testProduct1.getId());
    }


    @Test
    @DisplayName("Test Delete Product")
    void testDeleteProduct() {
        testProduct1 = productRepository.save(testProduct1);
        pictureBlobStorageService.saveImages(testProduct1.getId(), base64Images);

        ResponseMessageDTO response = productDeleteService.deleteProduct("123", testProduct1.getId(), user1.getId(), false);
        Assertions.assertEquals(200, response.getStatus());

        Product deletedProduct = productRepository.findById(testProduct1.getId()).orElse(null);
        Assertions.assertNull(deletedProduct);

        List<ImageDTO> images = pictureBlobStorageService.retrieveProductImages(testProduct1.getId());
        Assertions.assertEquals(0, images.size());

    }
}
