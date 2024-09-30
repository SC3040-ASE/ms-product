package com.product.dto.category;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CategorySearchResponseDTO {
    // Should this return 1 or multiple category?
    private Integer id;
    private String categoryName;
}
