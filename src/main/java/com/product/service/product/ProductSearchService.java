package com.product.service.product;

import com.product.dto.ImageDTO;
import com.product.dto.ResponseMessageDTO;
import com.product.dto.ProductSearchRequestDTO;
import com.product.dto.ProductSearchResponseDTO;
import com.product.repository.ProductRepository;
import com.product.mapper.ProductMapper;
import com.product.service.blob.PictureBlobStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final PictureBlobStorageService pictureBlobStorageService;

    @Transactional
    public ResponseMessageDTO searchProduct(String messageId, ProductSearchRequestDTO productSearchRequestDTO) {
        List<Object[]> results = productRepository.searchProducts(productSearchRequestDTO.getQuery(), productSearchRequestDTO.getNumberOfResults());
        List<ProductSearchResponseDTO> productSearchResults = productMapper.mapToSearchResults(results);

        for (ProductSearchResponseDTO productSearchResponseDTO : productSearchResults) {
            List<ImageDTO> images = pictureBlobStorageService.retrieveProductImages(productSearchResponseDTO.getId());
            productSearchResponseDTO.setImages(images);
        }

        return new ResponseMessageDTO(messageId, 200, productSearchResults);
    }
}
