package com.product.dto.category;

import lombok.Data;

@Data
public class CategorySearchRequestDTO {
    private String query;
    private int numberOfResults;
}
