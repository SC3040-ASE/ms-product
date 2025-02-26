package com.product.service.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.tag.TagReadRequestDTO;
import com.product.dto.tag.TagReadResponseDTO;
import com.product.dto.tag.TagReadWithoutProductResponseDTO;
import com.product.entity.Tag;
import com.product.mapper.TagMapper;
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
public class TagReadService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public List<Tag> fetchTags(List<Integer> tagIDs) {
        List<Tag> tags = new ArrayList<>();
        for (Integer id : tagIDs) {
            tags.add(tagRepository.findById(id).orElse(null));
        }

        for (Tag tag : tags) {
            log.info(tag.getTagName());
        }
        return tags;
    }

    @Transactional
    public ResponseMessageDTO readTag(String messageId, TagReadRequestDTO tagReadRequestDTO) throws Exception {
        Optional<Tag> optionalTag;
        if (tagReadRequestDTO.getId() != null
                && tagReadRequestDTO.getTagName() != null
                && !tagReadRequestDTO.getTagName().isEmpty()
                && tagReadRequestDTO.getCategory().getId() != null) {
            optionalTag = tagRepository.findTagByAllParams(
                    tagReadRequestDTO.getId(),
                    tagReadRequestDTO.getTagName(),
                    tagReadRequestDTO.getCategory().getId());
        } else if (tagReadRequestDTO.getId() != null
                && tagReadRequestDTO.getTagName() != null
                && !tagReadRequestDTO.getTagName().isEmpty()) {
            optionalTag = tagRepository.findByIdAndTagName(
                    tagReadRequestDTO.getId(),
                    tagReadRequestDTO.getTagName());
        } else if (tagReadRequestDTO.getId() != null) {
            optionalTag = tagRepository.findById(
                    tagReadRequestDTO.getId());
        } else {
            return new ResponseMessageDTO(messageId, 401, "Bad Request. Missing tag ID.");
        }

        if (optionalTag.isPresent()) {
            log.info("Mapping tag to DTO");
            TagReadResponseDTO tagReadResponseDTO = tagMapper.mapTagToTagDTO(optionalTag.get());
            return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(tagReadResponseDTO));
        } else {
            return new ResponseMessageDTO(messageId, 404, "Tag not found.");
        }
    }

    public List<TagReadWithoutProductResponseDTO> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        List<TagReadWithoutProductResponseDTO> tagReadResponseDTOS = new ArrayList<>();
        tags.forEach(tag -> {
            tagReadResponseDTOS.add(tagMapper.mapTagToTagWithoutProductDTO(tag));
        });
        return tagReadResponseDTOS;
    }

    public ResponseMessageDTO getAllTags(String messageId) throws JsonProcessingException {
        List<Tag> tags = tagRepository.findAll();
        List<TagReadWithoutProductResponseDTO> tagReadResponseDTOS = new ArrayList<>();
        tags.forEach(tag -> {
            tagReadResponseDTOS.add(tagMapper.mapTagToTagWithoutProductDTO(tag));
        });
        return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(tagReadResponseDTOS));
    }
}
