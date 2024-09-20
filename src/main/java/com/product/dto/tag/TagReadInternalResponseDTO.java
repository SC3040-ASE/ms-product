package com.product.dto.tag;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagReadInternalResponseDTO {
    private Integer id;

    @JsonProperty("tag_name")
    private String tagName;
}
