package com.product.service.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.tag.TagReadResponseDTO;
import com.product.dto.tag.TagUpdateRequestDTO;
import com.product.entity.Tag;
import com.product.mapper.TagMapper;
import com.product.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagUpdateService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseMessageDTO updateTag(String messageId, TagUpdateRequestDTO tagUpdateRequestDTO) throws Exception {
        if (tagUpdateRequestDTO.getId() == null) {
            return new ResponseMessageDTO(messageId, 401, "Bad Request. Missing tag information.");
        }
        Optional<Tag> optionalTag = tagRepository.findById(tagUpdateRequestDTO.getId());
        if (optionalTag.isEmpty()) {
            return new ResponseMessageDTO(messageId, 404, "Tag not found.");
        }
        if (tagUpdateRequestDTO.getId().equals(optionalTag.get().getId())) {
            Tag updatedTag = tagMapper.mapUpdatedTagDTOToTag(optionalTag.get(), tagUpdateRequestDTO);
            Tag savedTag = tagRepository.saveAndFlush(updatedTag);
            TagReadResponseDTO newTag = tagMapper.mapTagToTagDTO(savedTag);
            return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(newTag));
        } else {
            return new ResponseMessageDTO(messageId, 403, "Forbidden Update Request.");
        }
    }
}
