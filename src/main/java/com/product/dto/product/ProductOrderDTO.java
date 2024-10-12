package com.product.dto.product;

import lombok.Data;

@Data
public class ProductOrderDTO {
    private int sellerId;
    private int buyerId;
    private int productId;
    private String status;
}
