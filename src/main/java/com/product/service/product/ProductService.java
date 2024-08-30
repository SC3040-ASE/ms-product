package com.product.service.product;

import com.product.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductCreationService productCreationService;
    private final ProductReadService productReadService;
    private final ProductUpdateService productUpdateService;
    private final ProductDeleteService productDeleteService;
    private final ProductSearchService productSearchService;

    public ResponseMessageDTO handleCreateProduct(String messageId, ProductCreationRequestDTO productCreationRequestDTO) {
        return productCreationService.createProduct(messageId, productCreationRequestDTO);
    }

    public ResponseMessageDTO handleReadProduct(String messageId, Integer id) {
        return productReadService.readProduct(messageId, id);
    }

    public ResponseMessageDTO handleUpdateProduct(String messageId, ProductUpdateRequestDTO productUpdateRequestDTO) {
        return productUpdateService.updateProduct(messageId, productUpdateRequestDTO);
    }

    public ResponseMessageDTO handleDeleteProduct(String messageId, ProductDeleteRequestDTO targetProduct) {
        return productDeleteService.deleteProduct(messageId, targetProduct);
    }

    public ResponseMessageDTO handleSearchProduct(String messageId, ProductSearchRequestDTO productSearchRequestDTO) {
        return productSearchService.searchProduct(messageId, productSearchRequestDTO);
    }


}
