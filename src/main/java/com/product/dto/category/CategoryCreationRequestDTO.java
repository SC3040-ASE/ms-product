package com.product.dto.category;

import com.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class CategoryCreationRequestDTO {
    String categoryName;
    List<Product> products;
}
