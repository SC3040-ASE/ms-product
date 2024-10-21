package com.product.mapper;

import com.product.Application;
import com.product.dto.tag.*;
import com.product.entity.Category;
import com.product.entity.Tag;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TagMapperTest {
    @Autowired
    private TagMapper tagMapper;

    private Category category;

    @BeforeAll
    public void setup() {
        category = new Category();
        category.setCategoryName("testTagGeneration");
    }

    @Test
    @DisplayName("Test Map Tag DTO to Tag")
    void testMapDTOToTag() {
        TagCreationRequestDTO tagCreationRequestDTO = TagCreationRequestDTO
            .builder()
            .tagName("taggy")
            .category(category)
            .build();

        Tag tag = tagMapper.mapTagDTOToTag(tagCreationRequestDTO);
        Assertions.assertEquals("taggy", tag.getTagName());
    }

    @Test
    @DisplayName("Test Map Tag To Tag DTO")
    void testMapTagToDTO() {
        Tag tag = new Tag();
        tag.setCategory(category);
        tag.setTagName("Taggy");
        tag.setId(1);
        tag.setProducts(new ArrayList<>());

        TagReadResponseDTO tagReadResponseDTO = tagMapper.mapTagToTagDTO(tag);
        Assertions.assertEquals("Taggy", tagReadResponseDTO.getTagName());
        Assertions.assertEquals(1, tagReadResponseDTO.getId());
    }

    @Test
    @DisplayName("Test Map Tag without Product DTO")
    void testMapTagWOProduct() {
        Tag tag = new Tag();
        tag.setCategory(category);
        tag.setTagName("Taggy");
        tag.setId(1);

        TagReadWithoutProductResponseDTO tagRead = tagMapper.mapTagToTagWithoutProductDTO(tag);
        Assertions.assertEquals("Taggy", tagRead.getTagName());
        Assertions.assertEquals(1, tagRead.getId());
    }

    @Test
    @DisplayName("Test Map Update Tag DTO to Tag")
    void testMapUpdateToTag() {
        Category category1 = new Category();
        category1.setId(2);
        category1.setCategoryName("cat2");

        Tag tag = new Tag();
        tag.setId(2);
        tag.setCategory(category1);
        tag.setTagName("oldTag");

        TagUpdateRequestDTO tagUpdateRequestDTO = TagUpdateRequestDTO
            .builder()
            .id(2)
            .category(category)
            .tagName("newTag")
            .build();
        Tag t = tagMapper.mapUpdatedTagDTOToTag(tag, tagUpdateRequestDTO);
        Assertions.assertEquals("newTag", t.getTagName());
        Assertions.assertNotEquals("oldTag", t.getTagName());
        Assertions.assertEquals("testTagGeneration", t.getCategory().getCategoryName());
        Assertions.assertNotEquals("cat2", t.getCategory().getCategoryName());
    }

    @Test
    @DisplayName("Test Map Multiple Tags")
    void mapMultipleTags() {
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Tag t = new Tag();
            t.setTagName("tag" + i);
            t.setCategory(category);
            t.setId(i);
            tags.add(t);
        }
        MultipleTagCreationResponseDTO multipleTags = tagMapper.mapTagsToMultipleTagDTO(tags);
        Assertions.assertEquals(4, multipleTags.getTagIds().size());
    }
}
