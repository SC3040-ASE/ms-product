package com.product.repository;

import com.product.Application;
import com.product.dto.ProductSearchResponseDTO;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.Tag;
import com.product.entity.User;
import com.product.mapper.ProductMapper;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)  // Ensure context creation with the main application
@ActiveProfiles("test")
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
    private User user1;
    private User user2;


    @BeforeAll
    public void setup() {
        user1 = new User();
        user1.setUsername("user1");
        user1.setPassword("pass1");
        user1.setEmail("user1@example.com");
        user1.setIsAdmin(false);
        user1.setCreatedOn(LocalDateTime.now());
        user1.setUpdatedOn(LocalDateTime.now());
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("pass2");
        user2.setEmail("user2@example.com");
        user2.setIsAdmin(false);
        user2.setCreatedOn(LocalDateTime.now());
        user2.setUpdatedOn(LocalDateTime.now());
        user2 = userRepository.save(user2);

        testCategory = new Category();
        testCategory.setCategoryName("Electronics");
        categoryRepository.save(testCategory);

        testTag = new Tag();
        testTag.setTagName("New");
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
    }

    @AfterAll
    public void tearDown() {
        productRepository.deleteAll();
        tagRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
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
    public void testReadProduct() {
        Product readProduct = productRepository.findById(testProduct.getId()).orElse(null);
        Assertions.assertNotNull(readProduct);
        Assertions.assertEquals(testProduct.getProductName(), readProduct.getProductName());
        assertTrue(testProduct.getPrice().compareTo(readProduct.getPrice()) == 0,
                "Prices should be equal");
        Assertions.assertEquals(testProduct.getCondition(), readProduct.getCondition());
        Assertions.assertEquals(testProduct.getTotalQuantity(), readProduct.getTotalQuantity());
        Assertions.assertEquals(testProduct.getCurrentQuantity(), readProduct.getCurrentQuantity());
        Assertions.assertEquals(testProduct.getCategory().getCategoryName(), readProduct.getCategory().getCategoryName());
        Assertions.assertEquals(testProduct.getDescription(), readProduct.getDescription());
    }

    @Test
    @DisplayName("Test update product")
    public void testUpdateProduct() {
        Product productToUpdate = productRepository.findById(testProduct.getId()).orElse(null);
        Assertions.assertNotNull(productToUpdate);

        productToUpdate.setProductName("Updated Product");
        productToUpdate.setPrice(BigDecimal.valueOf(120.0));
        productRepository.save(productToUpdate);

        Product updatedProduct = productRepository.findById(testProduct.getId()).orElse(null);
        Assertions.assertNotNull(updatedProduct);
        Assertions.assertEquals("Updated Product", updatedProduct.getProductName());
        assertTrue(productToUpdate.getPrice().compareTo(updatedProduct.getPrice()) == 0,
                "Prices should be equal");
    }

    @Test
    @DisplayName("Test search products")
    public void testSearchProducts() {
        Product product = new Product();
        product.setOwnerId(user1.getId());
        product.setProductName("iPhone 19 Pro Max");
        product.setPrice(BigDecimal.valueOf(5999.99));
        product.setTags(List.of(testTag));
        product.setCondition("NEW");
        product.setTotalQuantity(5);
        product.setCurrentQuantity(5);
        product.setCategory(testCategory);
        product.setDescription("The latest iPhone");
        Product savedProduct = productRepository.save(product);

        List<Object[]> searchResults = productRepository.searchProducts("apple smartphone", 1);
        Assertions.assertFalse(searchResults.isEmpty());

        List<ProductSearchResponseDTO> searchResult = productMapper.mapToSearchResults(searchResults);
        Assertions.assertFalse(searchResult.isEmpty());
        Assertions.assertEquals(1, searchResult.size());
        Assertions.assertEquals(savedProduct.getId(), searchResult.get(0).getId());
        Assertions.assertEquals(savedProduct.getProductName(), searchResult.get(0).getProductName());
        Assertions.assertEquals(savedProduct.getPrice(), searchResult.get(0).getPrice());
        Assertions.assertEquals(savedProduct.getCondition(), searchResult.get(0).getCondition());
        Assertions.assertEquals(savedProduct.getTotalQuantity(), searchResult.get(0).getTotalQuantity());
        Assertions.assertEquals(savedProduct.getCurrentQuantity(), searchResult.get(0).getCurrentQuantity());
        Assertions.assertEquals(savedProduct.getCategory().getCategoryName(), searchResult.get(0).getCategoryName());
        Assertions.assertEquals(savedProduct.getDescription(), searchResult.get(0).getDescription());
    }

    @Test
    @DisplayName("Test delete product")
    public void testDeleteProduct() {
        productRepository.deleteById(testProduct.getId());
        Product deletedProduct = productRepository.findById(testProduct.getId()).orElse(null);
        Assertions.assertNull(deletedProduct);
    }

}
