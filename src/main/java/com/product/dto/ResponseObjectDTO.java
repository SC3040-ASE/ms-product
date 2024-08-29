package com.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ResponseObjectDTO {
    private String message;
    private Object returnObject;
}
