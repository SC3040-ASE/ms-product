package com.product.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class TelehandleResponse {
    private Integer userId;
    private String telegram_handle;
}
