package com.product.dto;

import lombok.Data;

@Data
public class ProductSearchRequestDTO {

    private String query;
    private int numberOfResults;
}
