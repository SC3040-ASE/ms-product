package com.product.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductOrderRequestDTO {
    private Integer categoryId;
    private Integer currentQuantity;
    private Integer ownerId;
    private BigDecimal price;
    private Integer productId;
    private List<Integer> tags;
}
