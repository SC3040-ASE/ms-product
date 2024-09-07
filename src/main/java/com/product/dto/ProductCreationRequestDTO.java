package com.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductCreationRequestDTO {
    private Integer ownerId;
    private String productName;
    private BigDecimal price;
    private List<String> tags;
    private String condition;
    private List<String> imageBase64List;
    private Integer totalQuantity;
    private String category;
    private String description;
}
