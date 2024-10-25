package com.product.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductActiveDTO {
    private int productId;
    private int ownerId;
    private int categoryId;
    private BigDecimal price;
    private int currentQuantity;
    private List<Integer> tags;
}

