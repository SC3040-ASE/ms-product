package com.product.mapper;

import com.product.Application;
import com.product.dto.image.ImageDTO;
import com.product.dto.product.*;
import com.product.entity.Category;
import com.product.entity.Product;
import com.product.entity.Tag;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    @DisplayName("Test map to entity")
    void testMapToEntity() {
        // Arrange
        ProductCreationRequestDTO dto = new ProductCreationRequestDTO();
        dto.setOwnerId(1);
        dto.setProductName("Test Product");
        dto.setPrice(new BigDecimal("100.00"));
        dto.setTags(Arrays.asList("Tag1", "Tag2"));
        dto.setCondition("New");
        dto.setTotalQuantity(10);
        dto.setDescription("A test product");

        Tag tag1 = new Tag("Tag1");
        Tag tag2 = new Tag("Tag2");
        List<Tag> existingTags = Arrays.asList(tag1, tag2);

        Category category = new Category("TestCategory");

        // Act
        Product product = productMapper.mapToEntity(dto, existingTags, category);

        // Assert
        Assertions.assertThat(product.getOwnerId()).isEqualTo(1);
        Assertions.assertThat(product.getProductName()).isEqualTo("Test Product");
        Assertions.assertThat(product.getPrice()).isEqualTo(new BigDecimal("100.00"));
        Assertions.assertThat(product.getTags()).contains(tag1, tag2);
        Assertions.assertThat(product.getCondition()).isEqualTo("New");
        Assertions.assertThat(product.getTotalQuantity()).isEqualTo(10);
        Assertions.assertThat(product.getCurrentQuantity()).isEqualTo(10);
        Assertions.assertThat(product.getCategory()).isEqualTo(category);
        Assertions.assertThat(product.getDescription()).isEqualTo("A test product");
    }

    @Test
    @DisplayName("Test update product from DTO")
    void testUpdateProductFromDTO() {
        // Arrange
        Product product = new Product();
        product.setId(100);
        product.setOwnerId(1);
        product.setProductName("Existing Product");
        product.setPrice(new BigDecimal("50.00"));
        product.setCondition("Old");
        product.setTotalQuantity(20);
        product.setCurrentQuantity(20);
        Category initialCategory = new Category("ExistingCategory");
        product.setCategory(initialCategory);
        product.setDescription("An existing product");

        ProductUpdateRequestDTO dto = new ProductUpdateRequestDTO();
        dto.setProductId(100);
        dto.setOwnerId(2);
        dto.setProductName("Updated Product");
        dto.setPrice(new BigDecimal("150.00"));
        dto.setTags(Arrays.asList("Tag1", "Tag2"));
        dto.setCondition("New");
        dto.setTotalQuantity(30);
        dto.setCurrentQuantity(25);
        dto.setCategory("UpdatedCategory");
        dto.setDescription("An updated product");

        Tag tag1 = new Tag("Tag1");
        Tag tag2 = new Tag("Tag2");
        List<Tag> existingTags = Arrays.asList(tag1, tag2);

        Category updatedCategory = new Category("UpdatedCategory");

        ProductMapper productMapper = new ProductMapper();

        // Act
        Product updatedProduct = productMapper.updateProductFromDTO(product, dto, existingTags, updatedCategory);

        // Assert
        Assertions.assertThat(updatedProduct.getId()).isEqualTo(100);
        Assertions.assertThat(updatedProduct.getOwnerId()).isEqualTo(2);
        Assertions.assertThat(updatedProduct.getProductName()).isEqualTo("Updated Product");
        Assertions.assertThat(updatedProduct.getPrice()).isEqualTo(new BigDecimal("150.00"));
        Assertions.assertThat(updatedProduct.getTags()).contains(tag1, tag2);
        Assertions.assertThat(updatedProduct.getCondition()).isEqualTo("New");
        Assertions.assertThat(updatedProduct.getTotalQuantity()).isEqualTo(30);
        Assertions.assertThat(updatedProduct.getCurrentQuantity()).isEqualTo(25);
        Assertions.assertThat(updatedProduct.getCategory()).isEqualTo(updatedCategory);
        Assertions.assertThat(updatedProduct.getDescription()).isEqualTo("An updated product");
    }


    @Test
    @DisplayName("Test map to search results")
    void testMapToSearchResults() {
        // Arrange
        List<Object[]> results = new ArrayList<>();
        results.add(new Object[] {
                1, // productId
                10, // ownerId
                "ownerUsername", // ownerUsername
                "Test Product", // productName
                new BigDecimal("100.00"), // price
                "New", // condition
                5, // currentQuantity
                Timestamp.valueOf("2021-01-01 10:00:00"), // createdOn
                0.95f, // score
                100 // totalResults (only first row element used)
        });

        results.add(new Object[] {
                2, // productId
                20, // ownerId
                "anotherUsername", // ownerUsername
                "Another Product", // productName
                new BigDecimal("50.00"), // price
                "Used", // condition
                2, // currentQuantity
                Timestamp.valueOf("2021-01-02 10:00:00"), // createdOn
                0.85f, // score
                100 // totalResults (ignored for other rows)
        });

        ProductMapper productMapper = new ProductMapper();

        // Act
        Pair<List<ProductSearchResultDTO>, Integer> result = productMapper.mapToSearchResults(results);

        List<ProductSearchResultDTO> searchResults = result.getLeft();
        int totalResults = result.getRight();

        // Assert
        Assertions.assertThat(searchResults).hasSize(2);

        ProductSearchResultDTO firstResult = searchResults.get(0);
        Assertions.assertThat(firstResult.getProductId()).isEqualTo(1);
        Assertions.assertThat(firstResult.getOwnerId()).isEqualTo(10);
        Assertions.assertThat(firstResult.getOwnerUsername()).isEqualTo("ownerUsername");
        Assertions.assertThat(firstResult.getProductName()).isEqualTo("Test Product");
        Assertions.assertThat(firstResult.getPrice()).isEqualTo(new BigDecimal("100.00"));
        Assertions.assertThat(firstResult.getCondition()).isEqualTo("New");
        Assertions.assertThat(firstResult.getCurrentQuantity()).isEqualTo(5);
        Assertions.assertThat(firstResult.getCreatedOn()).isEqualTo(Timestamp.valueOf("2021-01-01 10:00:00"));
        Assertions.assertThat(firstResult.getScore()).isEqualTo(0.95f);

        ProductSearchResultDTO secondResult = searchResults.get(1);
        Assertions.assertThat(secondResult.getProductId()).isEqualTo(2);
        Assertions.assertThat(secondResult.getOwnerId()).isEqualTo(20);
        Assertions.assertThat(secondResult.getOwnerUsername()).isEqualTo("anotherUsername");
        Assertions.assertThat(secondResult.getProductName()).isEqualTo("Another Product");
        Assertions.assertThat(secondResult.getPrice()).isEqualTo(new BigDecimal("50.00"));
        Assertions.assertThat(secondResult.getCondition()).isEqualTo("Used");
        Assertions.assertThat(secondResult.getCurrentQuantity()).isEqualTo(2);
        Assertions.assertThat(secondResult.getCreatedOn()).isEqualTo(Timestamp.valueOf("2021-01-02 10:00:00"));
        Assertions.assertThat(secondResult.getScore()).isEqualTo(0.85f);

        Assertions.assertThat(totalResults).isEqualTo(100);
    }


    @Test
    @DisplayName("Test map to product read response")
    void testMapToProductReadResponse() {
        // Arrange
        List<Tag> tags = Arrays.asList(new Tag("Tag1"), new Tag("Tag2"));
        Category category = new Category("CategoryName");
        Product product = new Product();
        product.setId(1);
        product.setOwnerId(10);
        product.setProductName("Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setTags(tags);
        product.setCondition("New");
        product.setTotalQuantity(50);
        product.setCurrentQuantity(50);
        product.setCreatedOn(Timestamp.valueOf("2022-01-01 10:00:00"));
        product.setCategory(category);
        product.setDescription("Test description");

        ImageDTO image1 = new ImageDTO();
        image1.setImageName("imageUrl1");
        image1.setImageBase64("base64Image1");
        ImageDTO image2 = new ImageDTO();
        image2.setImageName("imageUrl2");
        image2.setImageBase64("base64Image2");
        List<ImageDTO> images = Arrays.asList(image1, image2);

        ProductMapper productMapper = new ProductMapper();

        // Act
        ProductReadResponseDTO response = productMapper.mapToProductReadResponse(product, images, "ownerUsername");

        // Assert
        Assertions.assertThat(response.getProductId()).isEqualTo(1);
        Assertions.assertThat(response.getOwnerId()).isEqualTo(10);
        Assertions.assertThat(response.getProductName()).isEqualTo("Test Product");
        Assertions.assertThat(response.getPrice()).isEqualTo(new BigDecimal("99.99"));

        List<String> expectedTags = Arrays.asList("Tag1", "Tag2");
        Assertions.assertThat(response.getTags()).isEqualTo(expectedTags);

        Assertions.assertThat(response.getCondition()).isEqualTo("New");
        Assertions.assertThat(response.getImages()).isEqualTo(images);
        Assertions.assertThat(response.getTotalQuantity()).isEqualTo(50);
        Assertions.assertThat(response.getCurrentQuantity()).isEqualTo(50);

        Assertions.assertThat(response.getCreatedOn()).isEqualTo(Timestamp.valueOf("2022-01-01 10:00:00"));
        Assertions.assertThat(response.getCategoryName()).isEqualTo("CategoryName");
        Assertions.assertThat(response.getDescription()).isEqualTo("Test description");
    }

    @Test
    @DisplayName("Test map to product read preview results")
    void testMapToReadPreviewResults() {
        // Arrange
        List<Product> products = new ArrayList<>();

        Product product1 = new Product();
        product1.setId(1);
        product1.setOwnerId(10);
        product1.setProductName("Product 1");
        product1.setPrice(new BigDecimal("10.00"));
        product1.setCondition("New");
        product1.setCurrentQuantity(100);
        product1.setCreatedOn(Timestamp.valueOf("2022-01-01 10:00:00"));

        Product product2 = new Product();
        product2.setId(2);
        product2.setOwnerId(20);
        product2.setProductName("Product 2");
        product2.setPrice(new BigDecimal("20.00"));
        product2.setCondition("Used");
        product2.setCurrentQuantity(50);
        product2.setCreatedOn(Timestamp.valueOf("2022-02-01 10:00:00"));

        products.add(product1);
        products.add(product2);

        ProductMapper productMapper = new ProductMapper();

        // Act
        List<ProductReadPreviewDTO> result = productMapper.mapToReadPreviewResults(products);

        // Assert
        Assertions.assertThat(result).hasSize(2);

        ProductReadPreviewDTO dto1 = result.get(0);
        Assertions.assertThat(dto1.getProductId()).isEqualTo(1);
        Assertions.assertThat(dto1.getOwnerId()).isEqualTo(10);
        Assertions.assertThat(dto1.getProductName()).isEqualTo("Product 1");
        Assertions.assertThat(dto1.getPrice()).isEqualTo(new BigDecimal("10.00"));
        Assertions.assertThat(dto1.getCondition()).isEqualTo("New");
        Assertions.assertThat(dto1.getCurrentQuantity()).isEqualTo(100);
        Assertions.assertThat(dto1.getCreatedOn()).isEqualTo(Timestamp.valueOf("2022-01-01 10:00:00"));

        ProductReadPreviewDTO dto2 = result.get(1);
        Assertions.assertThat(dto2.getProductId()).isEqualTo(2);
        Assertions.assertThat(dto2.getOwnerId()).isEqualTo(20);
        Assertions.assertThat(dto2.getProductName()).isEqualTo("Product 2");
        Assertions.assertThat(dto2.getPrice()).isEqualTo(new BigDecimal("20.00"));
        Assertions.assertThat(dto2.getCondition()).isEqualTo("Used");
        Assertions.assertThat(dto2.getCurrentQuantity()).isEqualTo(50);
        Assertions.assertThat(dto2.getCreatedOn()).isEqualTo(Timestamp.valueOf("2022-02-01 10:00:00"));
    }

    @Test
    @DisplayName("Test map to products reserved")
    void testMapToProductsReserved() {
        // Arrange
        List<Product> products = new ArrayList<>();
        List<ProductOrderDTO> productOrderDTOS = new ArrayList<>();
        List<String> usersTelegram = Arrays.asList("telegramUser1", "telegramUser2");

        Product product1 = new Product();
        product1.setId(1);
        product1.setProductName("Product 1");
        product1.setPrice(new BigDecimal("10.00"));

        ProductOrderDTO order1 = new ProductOrderDTO();
        order1.setBuyerId(101);

        Product product2 = new Product();
        product2.setId(2);
        product2.setProductName("Product 2");
        product2.setPrice(new BigDecimal("20.00"));

        ProductOrderDTO order2 = new ProductOrderDTO();
        order2.setBuyerId(102);

        products.add(product1);
        productOrderDTOS.add(order1);
        products.add(product2);
        productOrderDTOS.add(order2);

        ProductMapper productMapper = new ProductMapper();

        Map<Integer,String> usersTelegramMap = new HashMap<>();
        usersTelegramMap.put(101, "telegramUser1");
        usersTelegramMap.put(102, "telegramUser2");

        // Act
        List<ProductReservedDTO> result = productMapper.mapToProductsReserved(products, productOrderDTOS, usersTelegramMap);

        // Assert
        Assertions.assertThat(result).hasSize(2);

        ProductReservedDTO dto1 = result.get(0);
        Assertions.assertThat(dto1.getProductId()).isEqualTo(1);
        Assertions.assertThat(dto1.getProductName()).isEqualTo("Product 1");
        Assertions.assertThat(dto1.getPrice()).isEqualTo(new BigDecimal("10.00"));
        Assertions.assertThat(dto1.getBuyerId()).isEqualTo(101);
        Assertions.assertThat(dto1.getBuyerTelegramHandle()).isEqualTo("telegramUser1");

        ProductReservedDTO dto2 = result.get(1);
        Assertions.assertThat(dto2.getProductId()).isEqualTo(2);
        Assertions.assertThat(dto2.getProductName()).isEqualTo("Product 2");
        Assertions.assertThat(dto2.getPrice()).isEqualTo(new BigDecimal("20.00"));
        Assertions.assertThat(dto2.getBuyerId()).isEqualTo(102);
        Assertions.assertThat(dto2.getBuyerTelegramHandle()).isEqualTo("telegramUser2");
    }
}
