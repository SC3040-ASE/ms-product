package com.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductUpdateDTO {
    private Integer id;
    private Integer ownerId;
    private String productName;
    private Double price;
    private List<String> tags;
    private String condition;
    private String imageBase64;
    private Integer totalQuantity;
    private String category;
    private String description;
}
