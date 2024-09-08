package com.product.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.Application;
import com.product.entity.Category;
import com.product.entity.Tag;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("dev")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TagRepositoryTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private Tag tag1;
    private Tag tag2;
    private Tag tag3;

    @BeforeAll
    public void setup() throws JsonProcessingException {
        Category mainCat = new Category();
        mainCat.setCategoryName("FakeAnimal");
        Category savedCat = categoryRepository.save(mainCat);

        tag1 = new Tag();
        tag1.setTagName("fakedoggie");
        tag1.setCategory(mainCat);

        tag2 = new Tag();
        tag2.setTagName("fakecat");
        tag2.setCategory(mainCat);

        tag3 = new Tag();
        tag3.setTagName("rabbit");
        tag3.setCategory(mainCat);

        System.out.println("Before");
        System.out.println("Category: " + savedCat);
        System.out.println("tag1: " + tag1);
        System.out.println("tag2: " + tag2);
        System.out.println("tag3: " + tag3);

        tag1 = tagRepository.saveAndFlush(tag1);
        tag3 = tagRepository.saveAndFlush(tag3);
        System.out.println("After");
        System.out.println("tag1: " + tag1);
        System.out.println("tag3: " + tag3);
    }

    @AfterAll
    public void tearDown() {
        tagRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create tag")
    @Rollback(value = false)
    public void CreateTagTest() {
        Tag savedTag2 = tagRepository.saveAndFlush(tag2);

        Assertions.assertNotNull(savedTag2);
        Assertions.assertNotNull(savedTag2.getId());
        Assertions.assertEquals(savedTag2.getTagName(), tag2.getTagName());
        tagRepository.delete(tag2);
    }

    @Test
    @DisplayName("Test read tag")
    public void ReadTagTest() {
        Tag savedTag2 = tagRepository.saveAndFlush(tag2);
        Optional<Tag> optionalTag1 = tagRepository.findById(tag1.getId());
        Optional<Tag> optionalTag2 = tagRepository.findById(tag2.getId());

        Assertions.assertTrue(optionalTag1.isPresent());
        Assertions.assertTrue(optionalTag2.isPresent());

        Assertions.assertNotNull(optionalTag1.get().getId());
        Assertions.assertNotNull(optionalTag2.get().getId());

        Assertions.assertEquals(optionalTag1.get().getTagName(), tag1.getTagName());
        Assertions.assertEquals(optionalTag2.get().getTagName(), tag2.getTagName());

        tagRepository.delete(tag2);
    }

    @Test
    @DisplayName("Test update tag")
    public void UpdateTagTest() {
        Tag storeTag1 = tag1;

        Optional<Tag> optionalTag1 = tagRepository.findById(tag1.getId());
        Assertions.assertTrue(optionalTag1.isPresent());

        optionalTag1.get().setTagName("doggo");
        tagRepository.save(optionalTag1.get());

        Optional<Tag> savedOptionalTag1 = tagRepository.findByIdAndTagName(tag1.getId(), "doggo");

        Assertions.assertTrue(savedOptionalTag1.isPresent());
        Assertions.assertEquals("doggo", savedOptionalTag1.get().getTagName());

        tagRepository.save(storeTag1);
    }

    @Test
    @DisplayName("Test delete tag")
    public void DeleteTagTest() {
        Optional<Tag> optionalTag3 = tagRepository.findById(tag3.getId());
        Assertions.assertTrue(optionalTag3.isPresent());

        tagRepository.deleteById(tag3.getId());
        Optional<Tag> deletedTag3 = tagRepository.findByIdAndTagName(tag3.getId(), tag3.getTagName());
        Assertions.assertTrue(deletedTag3.isEmpty());
    }
}
