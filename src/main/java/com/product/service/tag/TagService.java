package com.product.service.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.tag.*;
import com.product.entity.Category;
import com.product.entity.Tag;
import com.product.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagCreationService tagCreationService;
    private final TagReadService tagReadService;
    private final TagUpdateService tagUpdateService;
    private final TagDeleteService tagDeleteService;
    private final TagRepository tagRepository;
    private final TagGenerationService tagGenerationService;

    public List<Tag> saveTagsIfNotExists(List<String> tagNames, Category category) {
        List<Tag> existingTags = tagRepository.findTagsByTagNamesAndCategory(tagNames, category.getId());

        List<String> existingTagNames = existingTags.stream().map(Tag::getTagName).toList();
        List<Tag> newTags = tagNames.stream()
                .filter(tagName -> !existingTagNames.contains(tagName))
                .map(tagName -> {
                    Tag tag = new Tag(tagName);
                    tag.setCategory(category);
                    return tag;
                })
                .collect(Collectors.toList());

        List<Tag> savedNewTags = tagRepository.saveAll(newTags);
        existingTags.addAll(savedNewTags);
        return existingTags;
    }

    public ResponseMessageDTO handleCreateTag(String messageId, TagCreationRequestDTO tagCreationRequestDTO) {
        return tagCreationService.createTag(messageId, tagCreationRequestDTO);
    }

    public MultipleTagCreationResponseDTO handleCreateMultipleTags(MultipleTagCreationRequestDTO requestDTO) {
        return tagCreationService.createMultipleTagsForCategory(requestDTO);
    }

    public ResponseMessageDTO handleReadTag(String messageId, TagReadRequestDTO tagReadRequestDTO) throws Exception {
        return tagReadService.readTag(messageId, tagReadRequestDTO);
    }

    public List<TagReadWithoutProductResponseDTO> handleGetAllTags() {
        return tagReadService.getAllTags();
    }
    public ResponseMessageDTO handleGetAllTags(String messageId) throws JsonProcessingException {
        return tagReadService.getAllTags(messageId);
    }

    public ResponseMessageDTO handleUpdateTag(String messageId, TagUpdateRequestDTO tagUpdateRequestDTO)
            throws Exception {
        return tagUpdateService.updateTag(messageId, tagUpdateRequestDTO);
    }

    public ResponseMessageDTO handleDeleteTag(String messageId, TagDeleteRequestDTO tagDeleteRequestDTO) {
        return tagDeleteService.deleteTag(messageId, tagDeleteRequestDTO);
    }

    public List<TagReadInternalResponseDTO> fetchTags(List<Integer> tagIDs) {
        return tagReadService.fetchTags(tagIDs).stream().map(tag -> TagReadInternalResponseDTO.builder().id(tag.getId())
                .tagName(tag.getTagName()).build()).collect(Collectors.toList());
    }

    public ResponseMessageDTO handleGenerateTag(String messageId, String productName, String productDescription, Integer categoryId) throws Exception {
        return tagGenerationService.generateTag(messageId, productName, productDescription, categoryId);
    }
}
