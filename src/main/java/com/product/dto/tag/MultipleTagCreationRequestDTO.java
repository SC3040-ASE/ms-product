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
    private Category category;
    private List<String> tagNames;
}
