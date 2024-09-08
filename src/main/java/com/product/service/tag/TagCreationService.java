package com.product.service.tag;

import com.product.dto.ResponseMessageDTO;
import com.product.dto.tag.TagCreationRequestDTO;
import com.product.entity.Category;
import com.product.entity.Tag;
import com.product.mapper.TagMapper;
import com.product.repository.CategoryRepository;
import com.product.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagCreationService {
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

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
            System.out.println("Saving " + tag.getTagName() + " to repository.");
            savedTag = tagRepository.saveAndFlush(tag);
            System.out.println("New tag created: " + savedTag);
            return new ResponseMessageDTO(messageId, 200, savedTag);
        } catch (Exception e) {
            System.out.println("Failed to create tag: " + e);
            return new ResponseMessageDTO(messageId, 500, "Error creating tag.");
        }
    }
}
