package com.product.service.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.tag.MultipleTagCreationRequestDTO;
import com.product.dto.tag.MultipleTagCreationResponseDTO;
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

import java.util.ArrayList;
import java.util.List;
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
        Optional<Category> optionalCategory = categoryRepository.findById(
            tagCreationRequestDTO.getCategory().getId());
        if (optionalCategory.isEmpty()) {
            return new ResponseMessageDTO(messageId, 401, "Category does not exist.");
        }
        Optional<Tag> optionalTag = tagRepository.findByTagNameAndCategoryName(
            tagCreationRequestDTO.getTagName(),
            tagCreationRequestDTO.getCategory().getId());
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

    @Transactional
    public MultipleTagCreationResponseDTO createMultipleTagsForCategory(
        MultipleTagCreationRequestDTO requestDTO) {
        log.info("request: {}", requestDTO);
        Optional<Category> optionalCategory = categoryRepository.findById(requestDTO.getCategoryId());
        if (optionalCategory.isEmpty()) {
            log.error("Category does not exist.");
            return new MultipleTagCreationResponseDTO();
        }
        List<String> tagsToCreate = new ArrayList<>(requestDTO.getTags());
        List<String> tagsThatExist = new ArrayList<>();
        List<Tag> tagEntitiesThatExist = tagRepository.findTagsByTagNamesAndCategory(
            tagsToCreate,
            requestDTO.getCategoryId());
        tagEntitiesThatExist.forEach(
            tag -> tagsThatExist.add(tag.getTagName()));
        tagsToCreate.removeAll(tagsThatExist);
        List<Tag> tagsToSave = tagsToCreate.stream()
            .map(tagName -> {
                Category c = new Category();
                c.setId(requestDTO.getCategoryId());
                Tag t = new Tag();
                t.setTagName(tagName);
                t.setCategory(c);
                return t;
            }).toList();
        try {
            tagRepository.saveAll(tagsToSave);
            List<Tag> newTags = tagRepository.findTagsByTagNamesAndCategory(
                requestDTO.getTags(),
                requestDTO.getCategoryId());
            MultipleTagCreationResponseDTO responseDTO = tagMapper.mapTagsToMultipleTagDTO(newTags);

            log.info("{} new tags saved.", newTags.size());
            return responseDTO;
        } catch (Exception e) {
            log.error("Failed to create tags: {}", String.valueOf(e));
            return new MultipleTagCreationResponseDTO();
        }
    }
}
