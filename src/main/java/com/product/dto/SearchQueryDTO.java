/**
 * Data Transfer Object for search queries.
 * This class is used to encapsulate the search query parameters.
 */
package com.product.dto;

import lombok.Data;

@Data
public class SearchQueryDTO {

    private String query;
    private int numberOfResults;
}
