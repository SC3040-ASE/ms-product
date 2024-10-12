package com.product.dto.product;

import com.product.dto.image.ImageDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductReservedDTO {
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private ImageDTO image;
    private String buyerTelegramHandle;
    private Integer buyerId;
    private String sellerTelegramHandle;
    private String orderStatus;
}

