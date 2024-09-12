package com.product.dto.product;

import com.product.dto.image.ImageDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class ProductSearchResultDTO {
    private Integer productId;
    private Integer ownerId;
    private String ownerUsername;
    private String productName;
    private BigDecimal price;
    private String condition;
    private Integer currentQuantity;
    private Timestamp createdOn;
    private Float score;
    private ImageDTO image;
}

