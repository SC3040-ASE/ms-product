package com.product.service.category;

import com.product.dto.ResponseMessageDTO;
import com.product.dto.category.CategoryCreationRequestDTO;
import com.product.entity.Category;
import com.product.mapper.CategoryMapper;
import com.product.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryCreationService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public ResponseMessageDTO createCategory(String messageId, CategoryCreationRequestDTO categoryCreationRequestDTO) {
        Category category;
        Category savedCategory;
        try {
            category = categoryMapper.mapToEntity(categoryCreationRequestDTO);
            System.out.println("Saving " + category.getCategoryName() + " to repository");
            savedCategory = categoryRepository.saveAndFlush(category);
        } catch (Exception e) {
            return new ResponseMessageDTO(messageId, 500, "Error creating category.");
        }
        return new ResponseMessageDTO(messageId, 200, savedCategory);
    }
}
