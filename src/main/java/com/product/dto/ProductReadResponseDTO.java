package com.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductReadResponseDTO {
    private Integer id;
    private Integer ownerId;
    private String productName;
    private BigDecimal price;
    private List<String> tags;
    private String condition;
    private List<ImageDTO> images;
    private Integer totalQuantity;
    private Integer currentQuantity;
    private String categoryName;
    private String description;
}
