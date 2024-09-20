package com.product.repository;

import com.product.Application;
import com.product.entity.Category;
import com.product.entity.Tag;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TagRepositoryTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private Tag tag1;
    private Category mainCat;

    @BeforeAll
    public void setup() throws Exception {
        mainCat = new Category();
        mainCat.setCategoryName("testCat");
        Category savedCat = categoryRepository.saveAndFlush(mainCat);

        tag1 = new Tag();
        tag1.setTagName("testTag1");
        tag1.setCategory(mainCat);

        tag1 = tagRepository.saveAndFlush(tag1);
    }

    @AfterAll
    public void tearDown() {
        tagRepository.delete(tag1);
        categoryRepository.delete(mainCat);
    }

    @Test
    @DisplayName("Test create tag")
    @Rollback(value = false)
    public void CreateTagTest() {
        Tag newTag = new Tag();
        newTag.setTagName("newTag");
        newTag.setCategory(mainCat);

        Tag savedTag2 = tagRepository.saveAndFlush(newTag);

        Assertions.assertNotNull(savedTag2);
        Assertions.assertNotNull(savedTag2.getId());
        Assertions.assertEquals(savedTag2.getTagName(), newTag.getTagName());

        tagRepository.delete(newTag);
    }

    @Test
    @DisplayName("Test read tag")
    public void ReadTagTest() {
        Optional<Tag> optionalTag1 = tagRepository.findById(tag1.getId());

        Assertions.assertTrue(optionalTag1.isPresent());
        Assertions.assertNotNull(optionalTag1.get().getId());
        Assertions.assertEquals(optionalTag1.get().getTagName(), tag1.getTagName());

    }

    @Test
    @DisplayName("Test update tag")
    public void UpdateTagTest() {
        Tag storeTag1 = tag1;

        Optional<Tag> optionalTag1 = tagRepository.findById(tag1.getId());
        Assertions.assertTrue(optionalTag1.isPresent());

        optionalTag1.get().setTagName("updateTag");
        tagRepository.save(optionalTag1.get());

        Optional<Tag> savedOptionalTag1 = tagRepository.findByIdAndTagName(tag1.getId(), "updateTag");

        Assertions.assertTrue(savedOptionalTag1.isPresent());
        Assertions.assertEquals("updateTag", savedOptionalTag1.get().getTagName());

        tagRepository.save(storeTag1);
    }

    @Test
    @DisplayName("Test delete tag")
    public void DeleteTagTest() {
        Tag deleteTag = new Tag();
        deleteTag.setTagName("deleteTag");
        deleteTag.setCategory(mainCat);
        deleteTag = tagRepository.saveAndFlush(deleteTag);

        Optional<Tag> optionalTag3 = tagRepository.findById(deleteTag.getId());
        Assertions.assertTrue(optionalTag3.isPresent());

        tagRepository.deleteById(deleteTag.getId());
        Optional<Tag> deletedTag3 = tagRepository.findByIdAndTagName(deleteTag.getId(), deleteTag.getTagName());
        Assertions.assertTrue(deletedTag3.isEmpty());
    }
}
