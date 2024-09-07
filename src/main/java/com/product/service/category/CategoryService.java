package com.product.service.category;

import com.product.dto.ResponseMessageDTO;
import com.product.dto.category.*;
import com.product.entity.Category;
import com.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryCreationService categoryCreationService;
    private final CategoryReadService categoryReadService;
    private final CategoryUpdateService categoryUpdateService;
    private final CategorySearchService categorySearchService;
    private final CategoryDeleteService categoryDeleteService;
    private final CategoryRepository categoryRepository;

    public ResponseMessageDTO handleCreateCategory(String messageId, CategoryCreationRequestDTO categoryCreationRequestDTO) {
        return categoryCreationService.createCategory(messageId, categoryCreationRequestDTO);
    }

    public ResponseMessageDTO handleReadCategory(String messageId, CategoryReadRequestDTO categoryReadRequestDTO) {
        return categoryReadService.readCategory(messageId, categoryReadRequestDTO);
    }

    public ResponseMessageDTO handleUpdateCategory(String messageId, CategoryUpdateRequestDTO categoryUpdateRequestDTO) {
        return categoryUpdateService.updateCategory(messageId, categoryUpdateRequestDTO);
    }

    public ResponseMessageDTO handleDeleteCategory(String messageId, CategoryDeleteRequestDTO categoryDeleteRequestDTO) {
        return categoryDeleteService.deleteCategory(messageId, categoryDeleteRequestDTO);
    }

    public ResponseMessageDTO handleSearchCategory(String messageId, CategorySearchRequestDTO categorySearchRequestDTO) {
        return categorySearchService.searchCategory(messageId, categorySearchRequestDTO);
    }

    public Category saveCategoryIfNotExists(String categoryName) {
        return categoryRepository.findByName(categoryName).orElseGet(() -> categoryRepository.save(new Category(categoryName)));
    }
}
