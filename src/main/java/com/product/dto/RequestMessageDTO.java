package com.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class RequestMessageDTO {
    private String id;
    private String method;
    private String path;
    private String headers;
    private String body;
}