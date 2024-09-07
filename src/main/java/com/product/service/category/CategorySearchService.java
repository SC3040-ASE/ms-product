package com.product.service.category;

import com.product.dto.ResponseMessageDTO;
import com.product.dto.category.CategorySearchRequestDTO;
import com.product.dto.category.CategorySearchResponseDTO;
import com.product.mapper.CategoryMapper;
import com.product.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorySearchService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public ResponseMessageDTO searchCategory(String messageId, CategorySearchRequestDTO categorySearchRequestDTO) {
        List<Object[]> results = categoryRepository.searchCategories(
            categorySearchRequestDTO.getQuery(),
            categorySearchRequestDTO.getNumberOfResults()
        );
        List<CategorySearchResponseDTO> categorySearchResults = categoryMapper.mapToSearchResults(results);
        return new ResponseMessageDTO(messageId, 200, categorySearchResults);

    }
}
