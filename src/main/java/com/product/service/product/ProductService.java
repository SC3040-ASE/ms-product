package com.product.service.product;

import com.product.dto.*;
import com.product.dto.product.ProductCreationRequestDTO;
import com.product.dto.product.ProductUpdateRequestDTO;
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
    private final ProductSearchRangeService productSearchRangeService;

    public ResponseMessageDTO handleCreateProduct(String messageId, ProductCreationRequestDTO productCreationRequestDTO) {
        return productCreationService.createProduct(messageId, productCreationRequestDTO);
    }

    public ResponseMessageDTO handleReadProduct(String messageId, Integer id) throws Exception {
        return productReadService.readProduct(messageId, id);
    }

    public ResponseMessageDTO handleUpdateProduct(String messageId, ProductUpdateRequestDTO productUpdateRequestDTO) {
        return productUpdateService.updateProduct(messageId, productUpdateRequestDTO);
    }

    public ResponseMessageDTO handleDeleteProduct(String messageId, Integer productId, Integer ownerId, boolean isAdmin) {
        return productDeleteService.deleteProduct(messageId, productId, ownerId, isAdmin);
    }

    public ResponseMessageDTO handleSearchRangeProduct(String messageId, String query, int startRank, int endRank) throws Exception{
        return productSearchRangeService.searchRangeProduct(messageId, query, startRank, endRank);
    }

    public ResponseMessageDTO handleSearchRangeProduct(String messageId, String query, int startRank, int endRank, int sellerId) throws Exception{
        return productSearchRangeService.searchRangeProduct(messageId, query, startRank, endRank, sellerId);
    }

    public void handleUpdateProductQuantity(Integer productId, Integer quantityDiff) {
        productUpdateService.updateProductQuantity(productId, quantityDiff);
    }

}
