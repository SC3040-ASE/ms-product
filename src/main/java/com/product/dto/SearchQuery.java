package com.product.dto;

import lombok.Data;


@Data
public class SearchQuery {
    private String query;
    private int numberOfResults;
}
