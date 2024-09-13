package com.product.service.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.category.CategoryUpdateRequestDTO;
import com.product.entity.Category;
import com.product.mapper.CategoryMapper;
import com.product.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryUpdateService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseMessageDTO updateCategory(String messageId, CategoryUpdateRequestDTO categoryUpdateRequestDTO) throws Exception {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryUpdateRequestDTO.getId());

        if (optionalCategory.isPresent()) {
            if (optionalCategory.get().getId().equals(categoryUpdateRequestDTO.getId())) {
                Category updatedCategory = categoryMapper.mapCategoryDTOToCategory(optionalCategory.get(), categoryUpdateRequestDTO);
                Category savedCategory = categoryRepository.saveAndFlush(updatedCategory);
                categoryRepository.save(savedCategory);
                log.info("Successfully updated category.");
                return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(savedCategory));
            } else {
                return new ResponseMessageDTO(messageId, 403, "Forbidden Update Request.");
            }
        } else {
            return new ResponseMessageDTO(messageId, 404, "Category Not Found.");
        }
    }
}
