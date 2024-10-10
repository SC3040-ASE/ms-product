package com.product.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UsersTelegramHandleDTO {
    private List<TelehandleResponse> telehandleResponseList;
}
