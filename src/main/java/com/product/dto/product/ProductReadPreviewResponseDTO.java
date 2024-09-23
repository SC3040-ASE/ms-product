package com.product.dto.product;

import lombok.Data;
import java.util.List;

@Data
public class ProductReadPreviewResponseDTO {
    private List<ProductReadPreviewDTO> products;
    private Integer totalProducts;
}

