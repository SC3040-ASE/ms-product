package com.product.dto;

import lombok.Data;

@Data
public class ProductDeleteRequestDTO {
    private Integer productId;
    private Integer ownerId;
}
