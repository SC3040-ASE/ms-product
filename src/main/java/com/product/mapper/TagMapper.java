package com.product.mapper;

import com.product.dto.tag.TagCreationRequestDTO;
import com.product.dto.tag.TagReadResponseDTO;
import com.product.dto.tag.TagSearchResponseDTO;
import com.product.dto.tag.TagUpdateRequestDTO;
import com.product.entity.Category;
import com.product.entity.Tag;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TagMapper {
    public Tag mapTagDTOToTag(TagCreationRequestDTO tagCreationRequestDTO) {
        Tag tag = new Tag();
        tag.setCategory(tagCreationRequestDTO.getCategory());
        tag.setTagName(tagCreationRequestDTO.getTagName());
        return tag;
    }

    public TagReadResponseDTO mapTagToTagDTO(Tag tag) {
        if (!tag.getProducts().isEmpty()) {
            System.out.println("product not empty");
            return TagReadResponseDTO
                .builder()
                .id(tag.getId())
                .tagName(tag.getTagName())
                .category(tag.getCategory())
                .products(tag.getProducts())
                .build();
        } else {
            System.out.println("product empty");
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

    public List<TagSearchResponseDTO> mapToSearchResults(List<Object[]> results) {
        List<TagSearchResponseDTO> searchResults = new ArrayList<>();
        for (Object[] row : results) {
            try {
                TagSearchResponseDTO result = TagSearchResponseDTO
                    .builder()
                    .id((Integer) row[0])
                    .category((Category) row[1])
                    .tagName((String) row[2])
                    // TODO: Add the embedding pointed to list of products? Need to check if im supposed to do that for both category and tag first.
                    .build();
                searchResults.add(result);
            } catch (Exception e) {
                System.out.println("Error converting tag search results to DTO.");
            }
        }
        return searchResults;
    }
}
