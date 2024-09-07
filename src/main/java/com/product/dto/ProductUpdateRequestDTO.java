package com.product.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductUpdateRequestDTO {
    private Integer id;
    private Integer ownerId;
    private String productName;
    private BigDecimal price;
    private List<String> tags;
    private String condition;
    private List<String> deleteImageList;
    private List<String> newImageBase64List;
    private Integer totalQuantity;
    private String category;
    private String description;
}
