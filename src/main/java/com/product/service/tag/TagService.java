package com.product.service.tag;

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
    private final TagSearchService tagSearchService;
    private final TagRepository tagRepository;

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

    public ResponseMessageDTO handleReadTag(String messageId, TagReadRequestDTO tagReadRequestDTO) {
        return tagReadService.readTag(messageId, tagReadRequestDTO);
    }

    public ResponseMessageDTO handleUpdateTag(String messageId, TagUpdateRequestDTO tagUpdateRequestDTO) {
        return tagUpdateService.updateTag(messageId, tagUpdateRequestDTO);
    }

    public ResponseMessageDTO handleDeleteTag(String messageId, TagDeleteRequestDTO tagDeleteRequestDTO) {
        return tagDeleteService.deleteTag(messageId, tagDeleteRequestDTO);
    }

    public ResponseMessageDTO handleSearchTag(String messageId, TagSearchRequestDTO tagSearchRequestDTO) {
        return tagSearchService.searchTag(messageId, tagSearchRequestDTO);
    }
}
