package com.product.service.tag;

import com.product.dto.ResponseMessageDTO;
import com.product.dto.tag.TagDeleteRequestDTO;
import com.product.entity.Tag;
import com.product.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagDeleteService {
    private final TagRepository tagRepository;

    @Transactional
    public ResponseMessageDTO deleteTag(String messageId, TagDeleteRequestDTO tagDeleteRequestDTO) {
        Optional<Tag> optionalTag;
        if (
            tagDeleteRequestDTO.getId() == null
                || tagDeleteRequestDTO.getTagName() == null
                || tagDeleteRequestDTO.getTagName().isEmpty()
                || tagDeleteRequestDTO.getCategory().getId() == null
        ) {
            return new ResponseMessageDTO(messageId, 401, "Bad Request.");
        }
        optionalTag = tagRepository.findById(tagDeleteRequestDTO.getId());
        if (optionalTag.isEmpty()) {
            return new ResponseMessageDTO(messageId, 404, "Tag Not Found.");
        }
        if (!optionalTag.get().getTagName().equalsIgnoreCase(tagDeleteRequestDTO.getTagName())) {
            return new ResponseMessageDTO(messageId, 403, "Forbidden Delete Request.");
        } else {
            if (!optionalTag.get().getProducts().isEmpty()) {
                log.info("There are products tied to this tag.");
                return new ResponseMessageDTO(messageId, 403, "Forbidden Delete Request.");
            }
            try {
                tagRepository.deleteById(tagDeleteRequestDTO.getId());
                log.info("Successfully deleted tag.");
                return new ResponseMessageDTO(messageId, 200, "Successfully deleted tag.");
            } catch (Exception e) {
                return new ResponseMessageDTO(messageId, 500, "Internal Server Error.");
            }
        }
    }
}
