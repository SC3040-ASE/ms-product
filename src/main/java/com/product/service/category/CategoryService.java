package com.product.service.category;

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

    private final CategoryRepository categoryRepository;

    public Category saveCategoryIfNotExists(String categoryName) {
        return categoryRepository.findByName(categoryName).orElseGet(() -> categoryRepository.save(new Category(categoryName)));
    }
}
