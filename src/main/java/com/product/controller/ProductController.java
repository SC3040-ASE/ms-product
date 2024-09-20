package com.product.controller;

import com.product.dto.product.ProductUpdateQuantityRequestDTO;
import com.product.service.product.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PatchMapping("/products")
    public void updateQuantity(@RequestParam Integer id,
            @RequestBody ProductUpdateQuantityRequestDTO productUpdateQuantityRequestDTO) {
        productService.handleUpdateProductQuantity(id, productUpdateQuantityRequestDTO.getQuantity());
    }
}
