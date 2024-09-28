package com.product.dto.tag;

import com.product.entity.Category;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MultipleTagCreationRequestDTO {
    private Integer categoryId;
    private List<String> tagNames;
}
