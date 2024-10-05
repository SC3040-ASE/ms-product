package com.product.dto.product;

import com.product.dto.image.ImageDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
public class ProductReadResponseDTO {
    private Integer productId;
    private Integer ownerId;
    private String ownerUsername;
    private String productName;
    private BigDecimal price;
    private List<String> tags;
    private String condition;
    private List<ImageDTO> images;
    private Integer totalQuantity;
    private Integer currentQuantity;
    private Timestamp createdOn;
    private String categoryName;
    private String description;
}
