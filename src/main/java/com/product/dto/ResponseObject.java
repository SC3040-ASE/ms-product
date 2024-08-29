package com.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ResponseObject {
    private String message;
    private Object returnObject;
}
