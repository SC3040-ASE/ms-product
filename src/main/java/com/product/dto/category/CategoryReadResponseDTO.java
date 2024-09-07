package com.product.dto.category;

import com.product.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryReadResponseDTO {
    private Integer Id;
    private String categoryName;
}
