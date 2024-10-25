package com.product.controller;

import com.product.dto.product.ProductActiveDTO;
import com.product.dto.product.ProductUpdateQuantityRequestDTO;
import com.product.service.product.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @PatchMapping("/products")
    public void updateQuantity(@RequestParam Integer id,
            @RequestBody ProductUpdateQuantityRequestDTO productUpdateQuantityRequestDTO) {
        productService.handleUpdateProductQuantity(id, productUpdateQuantityRequestDTO.getQuantity());
    }

    @GetMapping("/active")
    public List<ProductActiveDTO> getActiveProducts(@RequestParam Integer categoryId) {
        log.info("Get active products by category id: {}", categoryId);
        return productService.handleGetActiveProducts(categoryId);
    }

}
