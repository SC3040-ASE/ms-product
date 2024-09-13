package com.product.service.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.tag.TagCreationRequestDTO;
import com.product.entity.Category;
import com.product.entity.Tag;
import com.product.mapper.TagMapper;
import com.product.repository.CategoryRepository;
import com.product.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagCreationService {
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseMessageDTO createTag(String messageId, TagCreationRequestDTO tagCreationRequestDTO) {
        Optional<Category> optionalCategory = categoryRepository.findById(tagCreationRequestDTO.getCategory().getId());
        if (optionalCategory.isEmpty()) {
            return new ResponseMessageDTO(messageId, 401, "Category does not exist.");
        }
        Optional<Tag> optionalTag = tagRepository.findByTagNameAndCategoryName(
            tagCreationRequestDTO.getTagName(),
            tagCreationRequestDTO.getCategory().getId()
        );
        if (optionalTag.isPresent()) {
            return new ResponseMessageDTO(messageId, 401, "Tag already exist.");
        }
        Tag tag;
        Tag savedTag;
        try {
            tag = tagMapper.mapTagDTOToTag(tagCreationRequestDTO);
            log.info("Saving {} to repository.", tag.getTagName());
            savedTag = tagRepository.saveAndFlush(tag);
            log.info("New tag created: {}", savedTag);
            return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(savedTag));
        } catch (Exception e) {
            log.error("Failed to create tag: {}", String.valueOf(e));
            return new ResponseMessageDTO(messageId, 500, "Error creating tag.");
        }
    }
}
