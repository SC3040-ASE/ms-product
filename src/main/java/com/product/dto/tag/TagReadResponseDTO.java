package com.product.dto.tag;

import com.product.entity.Category;
import com.product.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TagReadResponseDTO {
    private Integer id;
    private String tagName;
    private Category category;
    private List<Product> products;
}
