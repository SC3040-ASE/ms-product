package com.product.service.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.category.*;
import com.product.entity.Category;
import com.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryCreationService categoryCreationService;
    private final CategoryReadService categoryReadService;
    private final CategoryUpdateService categoryUpdateService;
    private final CategoryDeleteService categoryDeleteService;
    private final CategoryRepository categoryRepository;

    public ResponseMessageDTO handleCreateCategory(String messageId, CategoryCreationRequestDTO categoryCreationRequestDTO) throws Exception {
        return categoryCreationService.createCategory(messageId, categoryCreationRequestDTO);
    }

    public ResponseMessageDTO handleReadCategory(String messageId, CategoryReadRequestDTO categoryReadRequestDTO) throws Exception {
        return categoryReadService.readCategory(messageId, categoryReadRequestDTO);
    }

    public CategoryReadResponseDTO handleReadCategoryById(CategoryReadRequestDTO categoryReadRequestDTO) throws Exception {
        return categoryReadService.readCategory(categoryReadRequestDTO);
    }

    public CategoryReadResponseDTO handleReadCategoryById(Integer categoryReadRequestDTO) throws Exception {
        return categoryReadService.readCategory(categoryReadRequestDTO);
    }

    public List<CategoryReadResponseDTO> handleGetAllCategories() {
        return categoryReadService.getAllCategories();
    }
    public ResponseMessageDTO handleGetAllCategories(String messageId) throws JsonProcessingException {
        return categoryReadService.getAllCategories(messageId);
    }

    public ResponseMessageDTO handleUpdateCategory(String messageId, CategoryUpdateRequestDTO categoryUpdateRequestDTO) throws Exception {
        return categoryUpdateService.updateCategory(messageId, categoryUpdateRequestDTO);
    }

    public ResponseMessageDTO handleDeleteCategory(String messageId, CategoryDeleteRequestDTO categoryDeleteRequestDTO) {
        return categoryDeleteService.deleteCategory(messageId, categoryDeleteRequestDTO);
    }

    public Category saveCategoryIfNotExists(String categoryName) {
        return categoryRepository.findByName(categoryName).orElseGet(() -> categoryRepository.save(new Category(categoryName)));
    }
}
