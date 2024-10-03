package com.product.repository;

import com.product.Application;
import com.product.dto.product.ProductSearchResultDTO;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.Tag;
import com.product.entity.User;
import com.product.mapper.ProductMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductMapper productMapper;

    private Category testCategory;
    private Tag testTag;
    private Product testProduct;
    private Product searchProduct;
    private User user1;
    private User user2;


    @BeforeAll
    public void setup() {
        user1 = new User();
        user1.setUsername("productTest1");
        user1.setPassword("productTest1Pass");
        user1.setEmail("productTest1@example.com");
        user1.setTelegramHandle("productTest1Telegram");
        user1.setIsAdmin(false);
        user1.setCreatedOn(LocalDateTime.now());
        user1.setUpdatedOn(LocalDateTime.now());
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setUsername("productTest2");
        user2.setPassword("productTest2Pass");
        user2.setEmail("productTest2@example.com");
        user2.setTelegramHandle("productTest2Telegram");
        user2.setIsAdmin(false);
        user2.setCreatedOn(LocalDateTime.now());
        user2.setUpdatedOn(LocalDateTime.now());
        user2 = userRepository.save(user2);

        testCategory = new Category();
        testCategory.setCategoryName("Product Test Category");
        categoryRepository.save(testCategory);

        testTag = new Tag();
        testTag.setTagName("testProductTag");
        testTag.setCategory(testCategory);
        tagRepository.save(testTag);

        testProduct = new Product();
        testProduct.setOwnerId(user1.getId());
        testProduct.setProductName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.0));
        testProduct.setTags(List.of(testTag));
        testProduct.setCondition("NEW");
        testProduct.setTotalQuantity(10);
        testProduct.setCurrentQuantity(10);
        testProduct.setCategory(testCategory);
        testProduct.setDescription("A test product");
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
    public void tearDown() {
        productRepository.delete(searchProduct);
        productRepository.delete(testProduct);
        tagRepository.delete(testTag);
        categoryRepository.delete(testCategory);
        userRepository.delete(user1);
        userRepository.delete(user2);
    }

    @Test
    @DisplayName("Test create product")
    public void testCreateProduct() {
        Product product = new Product();
        product.setOwnerId(user2.getId());
        product.setProductName("Another Test Product");
        product.setPrice(BigDecimal.valueOf(200.0));
        product.setTags(List.of(testTag));
        product.setCondition("USED");
        product.setTotalQuantity(5);
        product.setCurrentQuantity(5);
        product.setCategory(testCategory);
        product.setDescription("Another test product");
        Product savedProduct = productRepository.save(product);

        Assertions.assertNotNull(savedProduct);
        Assertions.assertNotNull(savedProduct.getId());
        Assertions.assertEquals(product.getProductName(), savedProduct.getProductName());

        productRepository.delete(savedProduct);
    }

    @Test
    @DisplayName("Test read product")
    void testReadProduct() {
        Product readProduct = productRepository.findById(testProduct.getId()).orElse(null);
        Assertions.assertNotNull(readProduct);
        Assertions.assertEquals(testProduct.getProductName(), readProduct.getProductName());
        Assertions.assertTrue(testProduct.getPrice().compareTo(readProduct.getPrice()) == 0,
                "Prices should be equal");
        Assertions.assertEquals(testProduct.getCondition(), readProduct.getCondition());
        Assertions.assertEquals(testProduct.getTotalQuantity(), readProduct.getTotalQuantity());
        Assertions.assertEquals(testProduct.getCurrentQuantity(), readProduct.getCurrentQuantity());
        Assertions.assertEquals(testProduct.getCategory().getCategoryName(), readProduct.getCategory().getCategoryName());
        Assertions.assertEquals(testProduct.getDescription(), readProduct.getDescription());
    }

    @Test
    @DisplayName("Test update product")
    void testUpdateProduct() {
        Product productToUpdate = productRepository.findById(testProduct.getId()).orElse(null);
        Assertions.assertNotNull(productToUpdate);

        productToUpdate.setProductName("Updated Product");
        productToUpdate.setPrice(BigDecimal.valueOf(120.0));
        productRepository.save(productToUpdate);

        Product updatedProduct = productRepository.findById(testProduct.getId()).orElse(null);
        Assertions.assertNotNull(updatedProduct);
        Assertions.assertEquals("Updated Product", updatedProduct.getProductName());
        Assertions.assertTrue(productToUpdate.getPrice().compareTo(updatedProduct.getPrice()) == 0,
                "Prices should be equal");
    }

    @Test
    @DisplayName("Test search products")
    void testSearchProducts() {
        List<Object[]> searchResults = productRepository.searchProductsRange("Test Search Product", 1,1);
        Assertions.assertFalse(searchResults.isEmpty());

        Pair<List<ProductSearchResultDTO>,Integer> result = productMapper.mapToSearchResults(searchResults);
        List<ProductSearchResultDTO> searchResult = result.getKey();
        Assertions.assertFalse(searchResult.isEmpty());
        Assertions.assertEquals(searchProduct.getId(), searchResult.get(0).getProductId());
        Assertions.assertEquals(searchProduct.getProductName(), searchResult.get(0).getProductName());
        Assertions.assertEquals(searchProduct.getPrice(), searchResult.get(0).getPrice());
        Assertions.assertEquals(searchProduct.getCondition(), searchResult.get(0).getCondition());
        Assertions.assertEquals(searchProduct.getCurrentQuantity(), searchResult.get(0).getCurrentQuantity());
    }

    @Test
    @DisplayName("Test delete product")
    void testDeleteProduct() {
        Product deleteProduct = new Product();
        deleteProduct.setOwnerId(user1.getId());
        deleteProduct.setProductName("Delete Product");
        deleteProduct.setPrice(BigDecimal.valueOf(90.0));
        deleteProduct.setTags(List.of(testTag));
        deleteProduct.setCondition("NEW");
        deleteProduct.setTotalQuantity(10);
        deleteProduct.setCurrentQuantity(10);
        deleteProduct.setCategory(testCategory);
        deleteProduct.setDescription("A delete product");
        deleteProduct = productRepository.save(deleteProduct);

        productRepository.deleteById(deleteProduct.getId());
        Product deletedProduct = productRepository.findById(deleteProduct.getId()).orElse(null);
        Assertions.assertNull(deletedProduct);
    }

}
