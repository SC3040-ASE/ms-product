package com.product.dto.category;

import com.product.entity.Product;
import lombok.Data;

import java.util.List;

@Data
public class CategoryCreationRequestDTO {
    String categoryName;
    List<Product> products;
}
