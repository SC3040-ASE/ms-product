package com.product.dto.tag;

import com.product.entity.Category;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagUpdateRequestDTO {
    private Integer id;
    private String tagName;
    private Category category;
}
