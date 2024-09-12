package com.product.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductUpdateRequestDTO {
    private Integer ownerId;
    private Boolean isAdmin;
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private List<String> tags;
    private String condition;
    private List<String> deleteImageList;
    private List<String> newImageBase64List;
    private Integer totalQuantity;
    private Integer currentQuantity;
    private String category;
    private String description;
}
