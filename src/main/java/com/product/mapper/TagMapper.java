package com.product.mapper;

import com.product.dto.tag.*;
import com.product.entity.Category;
import com.product.entity.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TagMapper {
    public Tag mapTagDTOToTag(TagCreationRequestDTO tagCreationRequestDTO) {
        Tag tag = new Tag();
        tag.setCategory(tagCreationRequestDTO.getCategory());
        tag.setTagName(tagCreationRequestDTO.getTagName());
        return tag;
    }

    public TagReadResponseDTO mapTagToTagDTO(Tag tag) {
        if (!tag.getProducts().isEmpty()) {
            log.info("product not empty");
            return TagReadResponseDTO
                .builder()
                .id(tag.getId())
                .tagName(tag.getTagName())
                .category(tag.getCategory())
                .products(tag.getProducts())
                .build();
        } else {
            log.info("product empty");
            return TagReadResponseDTO
                .builder()
                .id(tag.getId())
                .tagName(tag.getTagName())
                .category(tag.getCategory())
                .products(null)
                .build();
        }
    }

    public Tag mapUpdatedTagDTOToTag(Tag tag, TagUpdateRequestDTO tagUpdateRequestDTO) {
        tag.setTagName(tagUpdateRequestDTO.getTagName());
        tag.setCategory(tagUpdateRequestDTO.getCategory());
        return tag;
    }

    public MultipleTagCreationResponseDTO mapTagsToMultipleTagDTO(List<Tag> tags) {
        List<Integer> tagIds = new ArrayList<>();
        tags.forEach(tag -> {
            tagIds.add(tag.getId());
        });
        return MultipleTagCreationResponseDTO.builder()
            .tagIds(tagIds)
            .build();
    }
}
