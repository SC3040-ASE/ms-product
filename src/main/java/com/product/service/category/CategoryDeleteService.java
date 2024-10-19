package com.product.service.category;

import com.product.dto.ResponseMessageDTO;
import com.product.dto.category.CategoryDeleteRequestDTO;
import com.product.entity.Category;
import com.product.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryDeleteService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public ResponseMessageDTO deleteCategory(String messageId, CategoryDeleteRequestDTO categoryDeleteRequestDTO) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryDeleteRequestDTO.getId());
        if (optionalCategory.isPresent()) {
            if (
                !optionalCategory.get()
                    .getCategoryName()
                    .equalsIgnoreCase(
                        categoryDeleteRequestDTO.getCategoryName()
                    )
            ) {
                return new ResponseMessageDTO(messageId, 403, "Forbidden Delete Request.");
            } else {
                if (optionalCategory.get().getProducts() != null && !optionalCategory.get().getProducts().isEmpty()) {
                    log.info("There are products tied to this category. Rejecting delete request.");
                    return new ResponseMessageDTO(messageId, 403, "Forbidden Delete Request.");
                }
                try {
                    categoryRepository.deleteById(categoryDeleteRequestDTO.getId());
                    log.info("Successfully deleted category.");
                    return new ResponseMessageDTO(messageId, 200, "Category Deleted Successfully.");
                } catch (Exception e) {
                    return new ResponseMessageDTO(messageId, 500, "Internal Server Error.");
                }
            }
        } else {
            return new ResponseMessageDTO(messageId, 404, "Category Not Found.");
        }
    }
}
