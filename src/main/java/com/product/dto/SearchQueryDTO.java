package com.product.dto;

import lombok.Data;


@Data
public class SearchQueryDTO {
    private String query;
    private int numberOfResults;
}
