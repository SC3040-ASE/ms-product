package com.product.service.category;

import com.product.dto.ResponseMessageDTO;
import com.product.dto.category.CategoryReadRequestDTO;
import com.product.dto.category.CategoryReadResponseDTO;
import com.product.entity.Category;
import com.product.mapper.CategoryMapper;
import com.product.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryReadService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public ResponseMessageDTO readCategory(String messageId, CategoryReadRequestDTO categoryReadRequestDTO) {
        Optional<Category> optionalCategory;
        System.out.println("Begin search for " + categoryReadRequestDTO.getCategoryName());
        if (categoryReadRequestDTO.getId() != null && !categoryReadRequestDTO.getCategoryName().isEmpty()) {
            optionalCategory = categoryRepository.findByNameAndId(
                categoryReadRequestDTO.getCategoryName(),
                categoryReadRequestDTO.getId()
            );
        } else if (categoryReadRequestDTO.getId() == null && !categoryReadRequestDTO.getCategoryName().isEmpty()) {
            optionalCategory = categoryRepository.findByName(
                categoryReadRequestDTO.getCategoryName()
            );
        } else {
            return new ResponseMessageDTO(messageId, 400, "Bad Request.");
        }
        if (optionalCategory.isPresent()) {
            CategoryReadResponseDTO categoryReadResponseDTO = categoryMapper.mapToCategoryReadResponse(optionalCategory.get());
            System.out.println("Successfully retrieved category: " + categoryReadResponseDTO.getCategoryName());
            return new ResponseMessageDTO(messageId, 200, categoryReadResponseDTO);
        } else {
            return new ResponseMessageDTO(messageId, 404, "Product Not Found.");
        }
    }
}
