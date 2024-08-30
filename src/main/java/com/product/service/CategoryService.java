package com.product.service;

import com.product.entity.Category;
import com.product.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category saveCategoryIfNotExists(String categoryName) {
        return categoryRepository.findByName(categoryName).orElseGet(() -> categoryRepository.save(new Category(categoryName)));
    }
}
