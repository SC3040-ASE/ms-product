package com.product.dto.product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductSearchRangeResponseDTO {
    private List<ProductSearchResultDTO> products;
    private Integer totalCount;
}


