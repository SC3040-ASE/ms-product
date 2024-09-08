package com.product.dto.tag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagSearchRequestDTO {
    private String query;
    private int numberOfResults;
}
