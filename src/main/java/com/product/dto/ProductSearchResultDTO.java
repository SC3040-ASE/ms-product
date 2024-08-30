package com.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductSearchResultDTO {
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private String[] tags;
    private String condition;
    private String productImage;
    private Integer totalQuantity;
    private Integer currentQuantity;
    private String categoryName;
    private String description;
    private Float score;
}
