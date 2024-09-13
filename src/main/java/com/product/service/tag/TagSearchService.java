package com.product.service.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.tag.TagSearchRequestDTO;
import com.product.dto.tag.TagSearchResponseDTO;
import com.product.mapper.TagMapper;
import com.product.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagSearchService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseMessageDTO searchTag(String messageId, TagSearchRequestDTO tagSearchRequestDTO) throws Exception {
        List<Object[]> results = tagRepository.searchTag(
            tagSearchRequestDTO.getQuery(),
            tagSearchRequestDTO.getNumberOfResults()
        );
        List<TagSearchResponseDTO> tagSearchResults = tagMapper.mapToSearchResults(results);
        return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(tagSearchResults));
    }
}
