package com.product.dto.tag;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MultipleTagCreationRequestDTO {
    private Integer categoryId;
    private List<String> tags;
}
