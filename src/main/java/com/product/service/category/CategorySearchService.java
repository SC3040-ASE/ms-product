package com.product.service.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.category.CategorySearchRequestDTO;
import com.product.dto.category.CategorySearchResponseDTO;
import com.product.mapper.CategoryMapper;
import com.product.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategorySearchService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    public final ObjectMapper objectMapper;

    @Transactional
    public ResponseMessageDTO searchCategory(String messageId, CategorySearchRequestDTO categorySearchRequestDTO) throws Exception {
        List<Object[]> results = categoryRepository.searchCategories(
            categorySearchRequestDTO.getQuery(),
            categorySearchRequestDTO.getNumberOfResults()
        );
        List<CategorySearchResponseDTO> categorySearchResults = categoryMapper.mapToSearchResults(results);
        return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(categorySearchResults));
    }
}
