package com.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSearchResponseDTO {
    private Integer id;
    private Integer ownerId;
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
