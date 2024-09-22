package com.product.service.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.image.ImageDTO;
import com.product.dto.product.ProductSearchRangeResponseDTO;
import com.product.dto.product.ProductSearchResultDTO;
import com.product.mapper.ProductMapper;
import com.product.repository.ProductRepository;
import com.product.service.blob.PictureBlobStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchRangeService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final PictureBlobStorageService pictureBlobStorageService;
    private final ObjectMapper objectMapper;

    @Transactional
    public ResponseMessageDTO searchRangeProduct(String messageId, String query, int startRank, int endRank) throws Exception {
        List<Object[]> results = productRepository.searchProductsRange(query, startRank, endRank);
        Pair<List<ProductSearchResultDTO>, Integer> mapResult = productMapper.mapToSearchResults(results);
        List<ProductSearchResultDTO> productSearchResults = mapResult.getKey();
        Integer totalResults = mapResult.getValue();

        for (ProductSearchResultDTO productSearchResultDTO : productSearchResults) {
            ImageDTO image = pictureBlobStorageService.retrieveOneProductImage(productSearchResultDTO.getProductId());
            productSearchResultDTO.setImage(image);
        }

        ProductSearchRangeResponseDTO productSearchRangeResponseDTO = new ProductSearchRangeResponseDTO(productSearchResults, totalResults);
        return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(productSearchRangeResponseDTO));
    }

    @Transactional
    public ResponseMessageDTO searchRangeProduct(String messageId, String query, int startRank, int endRank, int sellerId) throws Exception {
        List<Object[]> results = productRepository.searchProductsRange(query, startRank, endRank, sellerId);
        Pair<List<ProductSearchResultDTO>, Integer> mapResult = productMapper.mapToSearchResults(results);
        List<ProductSearchResultDTO> productSearchResults = mapResult.getKey();
        Integer totalResults = mapResult.getValue();

        for (ProductSearchResultDTO productSearchResultDTO : productSearchResults) {
            ImageDTO image = pictureBlobStorageService.retrieveOneProductImage(productSearchResultDTO.getProductId());
            productSearchResultDTO.setImage(image);
        }

        ProductSearchRangeResponseDTO productSearchRangeResponseDTO = new ProductSearchRangeResponseDTO(productSearchResults, totalResults);
        return new ResponseMessageDTO(messageId, 200, objectMapper.writeValueAsString(productSearchRangeResponseDTO));
    }
}
