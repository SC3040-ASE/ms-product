package com.product.controller;

import com.product.dto.category.CategoryReadRequestDTO;
import com.product.dto.category.CategoryReadResponseDTO;
import com.product.service.category.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/category/findCategoryById")
    public CategoryReadResponseDTO findCategoryNameById(
        @RequestBody CategoryReadRequestDTO requestDTO
    ) throws Exception {
        return categoryService.handleReadCategoryById(requestDTO);
    }
}
