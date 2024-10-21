package com.product.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.Application;
import com.product.entity.Category;
import com.product.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class CategoryRepositoryTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private CategoryRepository categoryRepository;

    private Category cat1;

    public List<Product> createProducts(String productString) throws Exception {
        List<Product> catProducts = new ArrayList<>();
        List<Integer> productIds = objectMapper.readValue(productString, new TypeReference<>() {
        });
        productIds.forEach(
            productId -> {
                Product prod = new Product();
                prod.setId(productId);
                catProducts.add(prod);
            }
        );
        return catProducts;
    }

    @BeforeAll
    public void setup() {
        cat1 = new Category();
        cat1.setCategoryName("testCat1");

        categoryRepository.saveAndFlush(cat1);
    }

    @AfterAll
    public void tearDown() {
        categoryRepository.delete(cat1);
    }

    @Test
    @DisplayName("Test create category")
    @Rollback(value = false)
    void CreateCategoryTest() {
        Category createCat = new Category();
        createCat.setCategoryName("testCat2");
        categoryRepository.saveAndFlush(createCat);

        Optional<Category> findCreatedCategory = categoryRepository.findById(createCat.getId());

        Assertions.assertTrue(findCreatedCategory.isPresent());
        Category createdCategory = findCreatedCategory.get();
        Assertions.assertNotNull(createdCategory.getId());
        Assertions.assertEquals(createCat.getCategoryName(), createdCategory.getCategoryName());

        categoryRepository.delete(createdCategory);
    }


    @Test
    @DisplayName("Test read category")
    void ReadCategoryTest() {
        Optional<Category> optionalCategory1 = categoryRepository.findByName(cat1.getCategoryName());

        Assertions.assertTrue(optionalCategory1.isPresent());
        Assertions.assertNotNull(optionalCategory1.get().getId());
        Assertions.assertEquals(cat1.getCategoryName(), optionalCategory1.get().getCategoryName());
    }


    @Test
    @DisplayName("Test update category")
    void UpdateCategoryTest() {
        Optional<Category> optionalCategory1 = categoryRepository.findByName(cat1.getCategoryName());

        Assertions.assertTrue(optionalCategory1.isPresent());

        optionalCategory1.get().setCategoryName("updateCat");

        categoryRepository.save(optionalCategory1.get());
        Optional<Category> savedOptionalCategory1 = categoryRepository.findByName("updateCat");

        Assertions.assertTrue(savedOptionalCategory1.isPresent());

        Assertions.assertEquals("updateCat", savedOptionalCategory1.get().getCategoryName());
    }


    @Test
    @DisplayName("Test delete category")
    void DeleteCategoryTest() {
        Category deleteCat = new Category();
        deleteCat.setCategoryName("deleteCat");
        categoryRepository.saveAndFlush(deleteCat);

        Optional<Category> optionalCategory4 = categoryRepository.findByName(deleteCat.getCategoryName());
        Assertions.assertTrue(optionalCategory4.isPresent());

        categoryRepository.deleteById(optionalCategory4.get().getId());

        Optional<Category> deletedCategory4 = categoryRepository.findByName(deleteCat.getCategoryName());
        Assertions.assertTrue(deletedCategory4.isEmpty());
    }
}
