package com.product.dto.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryReadResponseDTO {
    private Integer Id;
    private String categoryName;
}
