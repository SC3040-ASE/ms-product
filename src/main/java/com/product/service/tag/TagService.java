package com.product.service.tag;

import com.product.entity.Tag;
import com.product.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> saveTagsIfNotExists(List<String> tagNames) {
        List<Tag> existingTags = tagRepository.findTagsByTagNames(tagNames);

        List<String> existingTagNames = existingTags.stream().map(Tag::getTagName).toList();
        List<Tag> newTags = tagNames.stream()
                .filter(tagName -> !existingTagNames.contains(tagName))
                .map(Tag::new)
                .collect(Collectors.toList());

        List<Tag> savedNewTags = tagRepository.saveAll(newTags);
        existingTags.addAll(savedNewTags);
        return existingTags;
    }
}
