package com.product.controller;

import com.product.dto.category.CategoryReadResponseDTO;
import com.product.service.category.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/category")
    public CategoryReadResponseDTO findCategoryNameById(
        @RequestParam Integer id
    ) throws Exception {
        return categoryService.handleReadCategoryById(id);
    }

    @GetMapping("/category/all")
    public List<CategoryReadResponseDTO> getAllCategories() {
        return categoryService.handleGetAllCategories();
    }
}
