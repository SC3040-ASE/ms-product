package com.product.service.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.category.CategoryCreationRequestDTO;
import com.product.entity.Category;
import com.product.mapper.CategoryMapper;
import com.product.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryCreationService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseMessageDTO createCategory(String messageId, CategoryCreationRequestDTO categoryCreationRequestDTO) throws Exception {
        Category category;
        Category savedCategory;
        try {
            category = categoryMapper.mapToEntity(categoryCreationRequestDTO);
            log.info("Saving {} to repository", category.getCategoryName());
            savedCategory = categoryRepository.saveAndFlush(category);
        } catch (Exception e) {
            return new ResponseMessageDTO(messageId, 500, "Error creating category.");
        }
        return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(savedCategory));
    }
}
