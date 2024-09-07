package com.product.dto.category;

import com.product.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategorySearchResponseDTO {
    // Should this return 1 or multiple category?
    private Integer id;
    private String categoryName;
}
