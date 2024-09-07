package com.product.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.Application;
import com.product.entity.Category;
import com.product.entity.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryRepositoryTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private CategoryRepository categoryRepository;

    private Category cat1;
    private Category cat2;
    private Category cat3;
    private Category cat4;

    public List<Product> createProducts(String productString) throws JsonProcessingException {
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
    public void setup() throws JsonProcessingException {
        System.out.println("Its a fucking set up.");
        cat1 = new Category();
        cat1.setCategoryName("homework");

        cat2 = new Category();
        cat2.setCategoryName("electronics");

        cat3 = new Category();
        cat3.setCategoryName("clothes");

        cat4 = new Category();
        cat4.setCategoryName("animal");

        System.out.println("Before");
        System.out.println("cat1: " + cat1);
        System.out.println("cat2: " + cat2);
        System.out.println("cat3: " + cat3);
        System.out.println("cat4: " + cat4);

        Category savedCat1 = categoryRepository.saveAndFlush(cat1);
        Category savedCat4 = categoryRepository.saveAndFlush(cat4);
        System.out.println("After");
        System.out.println("cat1: " + savedCat1);
        System.out.println("cat4: " + savedCat4);
    }

    @AfterAll
    public void tearDown() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create category")
    @Rollback(value = false)
    public void CreateCategoryTest() {
        System.out.println("cat1: " + cat1);
        System.out.println("cat2: " + cat2);
        System.out.println("cat3: " + cat3);

        Category savedCat2 = categoryRepository.saveAndFlush(cat2);
        Category savedCat3 = categoryRepository.saveAndFlush(cat3);

        System.out.println("savedCat2: " + savedCat2);
        System.out.println("savedCat3: " + savedCat3);

        Assertions.assertNotNull(savedCat2);
        Assertions.assertNotNull(savedCat3);

        Assertions.assertNotNull(savedCat2.getId());
        Assertions.assertNotNull(savedCat3.getId());

        Assertions.assertEquals(cat2.getCategoryName(), savedCat2.getCategoryName());
        Assertions.assertEquals(cat3.getCategoryName(), savedCat3.getCategoryName());
        categoryRepository.delete(cat2);
        categoryRepository.delete(cat3);
    }


    @Test
    @DisplayName("Test read category")
    public void ReadCategoryTest() {
        Category savedCat2 = categoryRepository.saveAndFlush(cat2);
        Category savedCat3 = categoryRepository.saveAndFlush(cat3);
        Optional<Category> optionalCategory1 = categoryRepository.findByName(cat1.getCategoryName());
        Optional<Category> optionalCategory2 = categoryRepository.findByName(cat2.getCategoryName());
        Optional<Category> optionalCategory3 = categoryRepository.findByName(cat3.getCategoryName());

        Assertions.assertTrue(optionalCategory1.isPresent());
        Assertions.assertTrue(optionalCategory2.isPresent());
        Assertions.assertTrue(optionalCategory3.isPresent());

        Assertions.assertNotNull(optionalCategory1.get().getId());
        Assertions.assertNotNull(optionalCategory2.get().getId());
        Assertions.assertNotNull(optionalCategory3.get().getId());

        Assertions.assertEquals(cat1.getCategoryName(), optionalCategory1.get().getCategoryName());
        Assertions.assertEquals(cat2.getCategoryName(), optionalCategory2.get().getCategoryName());
        Assertions.assertEquals(cat3.getCategoryName(), optionalCategory3.get().getCategoryName());
        categoryRepository.delete(cat2);
        categoryRepository.delete(cat3);
    }


    @Test
    @DisplayName("Test update category")
    public void UpdateCategoryTest() throws JsonProcessingException {
        Optional<Category> optionalCategory1 = categoryRepository.findByName(cat1.getCategoryName());

        Assertions.assertTrue(optionalCategory1.isPresent());

        optionalCategory1.get().setCategoryName("classwork");

        categoryRepository.save(optionalCategory1.get());
        Optional<Category> savedOptionalCategory1 = categoryRepository.findByName("classwork");

        Assertions.assertTrue(savedOptionalCategory1.isPresent());

        Assertions.assertEquals("classwork", savedOptionalCategory1.get().getCategoryName());
    }


    @Test
    @DisplayName("Test delete category")
    public void DeleteCategoryTest() {
        Optional<Category> optionalCategory4 = categoryRepository.findByName(cat4.getCategoryName());

        Assertions.assertTrue(optionalCategory4.isPresent());

        categoryRepository.deleteById(optionalCategory4.get().getId());

        Optional<Category> deletedCategory4 = categoryRepository.findByName(cat4.getCategoryName());

        Assertions.assertTrue(deletedCategory4.isEmpty());
    }
}
