package com.product.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.dto.*;
import com.product.dto.product.ProductActiveDTO;
import com.product.dto.product.ProductCreationRequestDTO;
import com.product.dto.product.ProductUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductCreationService productCreationService;
    private final ProductReadService productReadService;
    private final ProductUpdateService productUpdateService;
    private final ProductDeleteService productDeleteService;
    private final ProductSearchRangeService productSearchRangeService;

    public ResponseMessageDTO handleCreateProduct(String messageId, ProductCreationRequestDTO productCreationRequestDTO) throws Exception{
        return productCreationService.createProduct(messageId, productCreationRequestDTO);
    }

    public ResponseMessageDTO handleReadProduct(String messageId, Integer id) throws JsonProcessingException {
        return productReadService.readProduct(messageId, id);
    }

    public ResponseMessageDTO handleReadProductsByOwnerId(String messageId, Integer ownerId, Integer startRank, Integer endRank) throws Exception {
        return productReadService.readProductsByOwnerId(messageId, ownerId, startRank, endRank);
    }

    public List<ProductActiveDTO> handleGetActiveProducts(Integer categoryId) {
        return productReadService.getActiveProducts(categoryId);
    }

    public ResponseMessageDTO handleReadProductsReserved(String messageId, Integer ownerId, Boolean isBuyer, String orderStatus) throws Exception{
        return productReadService.readProductsReserved(messageId, ownerId, isBuyer, orderStatus);
    }

    public ResponseMessageDTO handleUpdateProduct(String messageId, ProductUpdateRequestDTO productUpdateRequestDTO) throws JsonProcessingException {
        return productUpdateService.updateProduct(messageId, productUpdateRequestDTO);
    }

    public ResponseMessageDTO handleDeleteProduct(String messageId, Integer productId, Integer ownerId, boolean isAdmin) {
        return productDeleteService.deleteProduct(messageId, productId, ownerId, isAdmin);
    }

    public ResponseMessageDTO handleSearchRangeProduct(String messageId, String query, int startRank, int endRank) throws Exception{
        return productSearchRangeService.searchRangeProduct(messageId, query, startRank, endRank);
    }

    public void handleUpdateProductQuantity(Integer productId, Integer quantityDiff) {
        productUpdateService.updateProductQuantity(productId, quantityDiff);
    }

}
