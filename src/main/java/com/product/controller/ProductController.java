package com.product.controller;

import com.product.dto.product.ProductUpdateQuantityRequestDTO;
import com.product.service.product.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;
    @PatchMapping()
    public void updateQuantity(@RequestParam Integer id, @RequestBody ProductUpdateQuantityRequestDTO productUpdateQuantityRequestDTO) {
        productService.handleUpdateProductQuantity(id, productUpdateQuantityRequestDTO.getQuantity());
    }
}
